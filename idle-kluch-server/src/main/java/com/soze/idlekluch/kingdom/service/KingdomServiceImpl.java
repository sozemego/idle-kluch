package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.aop.annotations.AuthLog;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.events.KingdomRemovedEvent;
import com.soze.idlekluch.kingdom.exception.InvalidRegisterKingdomException;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.repository.KingdomRepository;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.event.UserRemovedEvent;
import com.soze.idlekluch.user.repository.UserRepository;
import com.soze.idlekluch.utils.PoissonDiscSampler;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.idlekluch.world.utils.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class KingdomServiceImpl implements KingdomService {

  private static final Logger LOG = LoggerFactory.getLogger(KingdomServiceImpl.class);
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private final KingdomRepository kingdomRepository;
  private final UserRepository userRepository;
  private final WorldService worldService;
  private final EntityService entityService;
  private final ApplicationEventPublisher eventPublisher;

  private final Map<String, Object> locks = new ConcurrentHashMap<>();

  @Autowired
  public KingdomServiceImpl(final KingdomRepository kingdomRepository,
                            final UserRepository userRepository,
                            final WorldService worldService,
                            final EntityService entityService,
                            final ApplicationEventPublisher eventPublisher) {
    this.kingdomRepository = Objects.requireNonNull(kingdomRepository);
    this.userRepository = Objects.requireNonNull(userRepository);
    this.worldService = Objects.requireNonNull(worldService);
    this.entityService = Objects.requireNonNull(entityService);
    this.eventPublisher = Objects.requireNonNull(eventPublisher);
  }

  @Override
  @Profiled
  @AuthLog
  public void addKingdom(final String owner, final RegisterKingdomForm form) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(form);

    validateKingdomForm(form);
    validateKingdom(owner, form);

    final TileId startingPoint = findStartingPoint();
    LOG.info("Starting point for kingdom [{}] is [{}]", form.getName(), startingPoint);
    worldService.createWorldChunk(startingPoint);

    final Kingdom kingdom = new Kingdom();
    kingdom.setKingdomId(EntityUUID.randomId());
    kingdom.setName(form.getName());
    kingdom.setCreatedAt(OffsetDateTime.now());
    kingdom.setStartingPoint(startingPoint);
    kingdom.setIdleBucks(KingdomService.STARTING_IDLE_BUCKS);

    // lets assume the user did not delete his account while making this request
    final User user = userRepository.getUserByUsername(owner).get();
    kingdom.setOwner(user);

    kingdomRepository.addKingdom(kingdom);
    LOG.info("User [{}] successfully created kingdom [{}]", owner, form.getName());
  }

  private void validateKingdomForm(final RegisterKingdomForm form) {
    final Set<ConstraintViolation<RegisterKingdomForm>> violations = VALIDATOR.validate(form);

    for (final ConstraintViolation<RegisterKingdomForm> violation: violations) {
      throw new InvalidRegisterKingdomException(violation.getPropertyPath().toString(), violation.getMessage());
    }
  }

  private void validateKingdom(final String owner, final RegisterKingdomForm form) {
    kingdomRepository
      .getKingdom(form.getName())
      .ifPresent((k) -> {
        throw new EntityAlreadyExistsException("Kingdom already exists: " + form.getName(), Kingdom.class);
      });

    kingdomRepository
      .getUsersKingdom(owner)
      .ifPresent((k) -> {
        throw new UserAlreadyHasKingdomException(owner, form.getName());
      });
  }

  @Override
  @AuthLog
  public void removeKingdom(final String owner) {
    Objects.requireNonNull(owner);

    synchronized (getLock(owner)) {
      final Kingdom kingdom = kingdomRepository
                                .getUsersKingdom(owner)
                                .<UserDoesNotHaveKingdomException>orElseThrow(() -> {
                                  throw new UserDoesNotHaveKingdomException(owner);
                                });

      eventPublisher.publishEvent(new KingdomRemovedEvent(kingdom.getKingdomId()));
      LOG.info("Found kingdom of user [{}], removing", owner);
      kingdomRepository.removeKingdom(kingdom.getName());
    }
  }

  @Override
  @AuthLog
  public void updateKingdom(final Kingdom kingdom) {
    Objects.requireNonNull(kingdom);
    kingdomRepository.updateKingdom(kingdom);
  }

  @Override
  public Optional<Kingdom> getKingdom(final String name) {
    Objects.requireNonNull(name);
    return kingdomRepository.getKingdom(name);
  }

  @Override
  public Optional<Kingdom> getUsersKingdom(final String username) {
    Objects.requireNonNull(username);
    return kingdomRepository.getUsersKingdom(username);
  }

  @Override
  public void handleUserRemovedEvent(final UserRemovedEvent userRemovedEvent) {
    Objects.requireNonNull(userRemovedEvent);
    final String username = userRemovedEvent.getUsername();
    getUsersKingdom(username).ifPresent(k -> removeKingdom(username));
  }

  /**
   * Finds a starting point for the kingdom.
   * This method takes all constructable entities and finds a point at least {@link KingdomService#MINIMUM_DISTANCE_BETWEEN_KINGDOMS}
   * tiles away.
   */
  private TileId findStartingPoint() {
    final List<Point> points = Stream.concat(getAllBuildingTiles(), getAllKingdomStartingTiles())
                                 .map(tileId -> new Point(tileId.getX(), tileId.getY()))
                                 .collect(Collectors.toList());

    final PoissonDiscSampler sampler = new PoissonDiscSampler(points, MINIMUM_DISTANCE_BETWEEN_KINGDOMS);
    return TileId.from(sampler.nextPoint());
  }

  /**
   * Returns a stream of TileId for all constructed buildings.
   */
  private Stream<TileId> getAllBuildingTiles() {
    return entityService
             .getEntitiesByNode(Nodes.BUILDING)
             .stream()
             .map(WorldUtils::getEntityTileId)
             .filter(Optional::isPresent)
             .map(Optional::get);
  }

  private Stream<TileId> getAllKingdomStartingTiles() {
    return kingdomRepository
             .getAllKingdoms()
             .stream()
             .map(Kingdom::getStartingPoint);
  }

  public Object getLock(final String name) {
    return locks.computeIfAbsent(name, k -> new Object());
  }

}

package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.repository.KingdomRepository;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.repository.UserRepository;
import com.soze.idlekluch.utils.PoissonDiscSampler;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KingdomServiceImpl implements KingdomService {

  private static final Logger LOG = LoggerFactory.getLogger(KingdomServiceImpl.class);

  private final KingdomRepository kingdomRepository;
  private final UserRepository userRepository;
  private final WorldService worldService;
  private final EntityService entityService;

  @Autowired
  public KingdomServiceImpl(final KingdomRepository kingdomRepository,
                            final UserRepository userRepository,
                            final WorldService worldService,
                            final EntityService entityService) {
    this.kingdomRepository = Objects.requireNonNull(kingdomRepository);
    this.userRepository = Objects.requireNonNull(userRepository);
    this.worldService = Objects.requireNonNull(worldService);
    this.entityService = Objects.requireNonNull(entityService);
  }

  @Override
  public void addKingdom(final String owner, final RegisterKingdomForm form) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(form);

    LOG.info("User [{}] is attempting to create kingdom [{}]", owner, form.getName());
    final Optional<Kingdom> kingdomOptional = kingdomRepository.getKingdom(form.getName());
    if(kingdomOptional.isPresent()) {
      LOG.info("Kingdom with name [{}] already exists", form.getName());
      throw new EntityAlreadyExistsException("Kingdom already exists", Kingdom.class);
    }

    final Optional<Kingdom> userKingdomOptional = kingdomRepository.getUsersKingdom(owner);
    if(userKingdomOptional.isPresent()) {
      LOG.info("User [{}] already has a kingdom", owner);
      throw new UserAlreadyHasKingdomException(owner, form.getName());
    }

    final TileId startingPoint = findStartingPoint();
    LOG.info("Starting point for kingdom [{}] is [{}]", form.getName(), startingPoint);

    final Kingdom kingdom = new Kingdom();
    kingdom.setKingdomId(EntityUUID.randomId());
    kingdom.setName(form.getName());
    kingdom.setCreatedAt(OffsetDateTime.now());
    kingdom.setStartingPoint(startingPoint);

    // lets assume the user did not delete his account while making this request
    final User user = userRepository.getUserByUsername(owner).get();
    kingdom.setOwner(user);

    worldService.createWorldChunk(startingPoint);

    kingdomRepository.addKingdom(kingdom);
    LOG.info("User [{}] successfully created kingdom [{}]", owner, form.getName());
  }

  @Override
  @Transactional
  public void removeKingdom(final String owner) {
    Objects.requireNonNull(owner);
    LOG.info("User [{}] is trying to remove their kingdom", owner);
    final Optional<Kingdom> kingdomOptional = kingdomRepository.getUsersKingdom(owner);

    if(kingdomOptional.isPresent()) {
      LOG.info("Found kingdom of user [{}], removing", owner);
      kingdomRepository.removeKingdom(kingdomOptional.get());
    } else {
      LOG.info("User [{}] does not have a kingdom", owner);
      throw new UserDoesNotHaveKingdomException(owner);
    }

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

  /**
   * Finds a starting point for the kingdom.
   * This method takes all constructable entities and finds a point at least {@link KingdomService#MINIMUM_DISTANCE_BETWEEN_KINGDOMS}
   * tiles away.
   */
  private TileId findStartingPoint() {
    //1. Load all buildings
    final List<Entity> buildings = entityService.getEntitiesByNode(Nodes.BUILDING);
    //2. Get all their positions and translate them to coordinates of tiles
    final List<Point> points = buildings
                                 .stream()
                                 .map(building -> {
                                   final PhysicsComponent physicsComponent = building.getComponent(PhysicsComponent.class);
                                   final float x = physicsComponent.getX();
                                   final float y = physicsComponent.getY();
                                   return new Point((int) x / WorldService.TILE_SIZE, (int) y / WorldService.TILE_SIZE);
                                 })
                                 .collect(Collectors.toList());

    //3. We also need to get all kingdom starting points. A player may have zero buildings,
    //   we still need to make sure the minimum distance is respected.
    kingdomRepository.getAllKingdoms()
      .forEach(kingdom -> {
        final TileId startingPoint = kingdom.getStartingPoint();
        points.add(new Point(startingPoint.getX(), startingPoint.getY()));
      });

    final PoissonDiscSampler sampler = new PoissonDiscSampler(points, MINIMUM_DISTANCE_BETWEEN_KINGDOMS);
    final Point nextPoint = sampler.nextPoint();

    return new TileId(nextPoint.x, nextPoint.y);
  }

}

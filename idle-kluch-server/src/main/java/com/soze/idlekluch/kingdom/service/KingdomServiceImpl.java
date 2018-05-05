package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.repository.KingdomRepository;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.repository.UserRepository;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.WorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Autowired
  public KingdomServiceImpl(final KingdomRepository kingdomRepository,
                            final UserRepository userRepository,
                            final WorldService worldService) {
    this.kingdomRepository = Objects.requireNonNull(kingdomRepository);
    this.userRepository = Objects.requireNonNull(userRepository);
    this.worldService = Objects.requireNonNull(worldService);
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

    final Kingdom kingdom = new Kingdom();
    kingdom.setKingdomId(EntityUUID.randomId());
    kingdom.setName(form.getName());
    kingdom.setCreatedAt(OffsetDateTime.now());
    kingdom.setStartingPoint(startingPoint);

    // lets assume the user did not delete his account while making this request
    final User user = userRepository.getUserByUsername(owner).get();
    kingdom.setOwner(user);

    kingdomRepository.addKingdom(kingdom);
    LOG.info("User [{}] successfully created kingdom [{}]", owner, form.getName());

    worldService.createWorldChunk(startingPoint);
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
   * //TODO make it into a generic algorithm, like Poisson Disc Sampling?
   */
  private TileId findStartingPoint() {
    //1. Load all kingdoms
    final List<Kingdom> kingdoms = kingdomRepository.getAllKingdoms();

    //2. For prototyping, finding a starting point will not be fancy.
    //   We just need to find a point that is not too close to any of the kingdoms
    //   or too far away.
    //   Let's brute force it for now.

    //3. All starting points
    final List<TileId> startingPoints = kingdoms
                                          .stream()
                                          .map(Kingdom::getStartingPoint)
                                          .collect(Collectors.toList());

    final int minDistance = 10;
    //lets ignore maxDistance for now
//    final int maxDistance = 25;

    TileId startingPoint = null;
    while (startingPoint == null) {
      final int x = CommonUtils.randomNumber(0, worldService.getMaxWorldWidth());
      final int y = CommonUtils.randomNumber(0, worldService.getMaxWorldHeight());

      //3a. check if all existing points are at least minDistance away
      boolean foundTileTooClose = false;
      for (final TileId kingdomStartingPoint: startingPoints) {
        final double distance = Math.hypot(x - kingdomStartingPoint.getX(), y - kingdomStartingPoint.getY());
        if(distance < minDistance) {
          foundTileTooClose = true;
          break;
        }
      }

      if(foundTileTooClose) {
        continue;
      }
      startingPoint = new TileId(x, y);
    }

    return startingPoint;
  }

}

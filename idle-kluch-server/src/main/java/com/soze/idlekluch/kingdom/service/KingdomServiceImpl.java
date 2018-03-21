package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.repository.KingdomRepository;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.repository.UserRepository;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class KingdomServiceImpl implements KingdomService {

  private static final Logger LOG = LoggerFactory.getLogger(KingdomServiceImpl.class);

  private final KingdomRepository kingdomRepository;
  private final UserRepository userRepository;

  @Autowired
  public KingdomServiceImpl(final KingdomRepository kingdomRepository, UserRepository userRepository) {
    this.kingdomRepository = Objects.requireNonNull(kingdomRepository);
    this.userRepository = Objects.requireNonNull(userRepository);
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

    final Kingdom kingdom = new Kingdom();
    kingdom.setKingdomId(EntityUUID.randomId());
    kingdom.setName(form.getName());
    kingdom.setCreatedAt(OffsetDateTime.now());

    // lets assume the user did not delete his account while making this request
    final User user = userRepository.getUserByUsername(owner).get();
    kingdom.setOwner(user);

    kingdomRepository.addKingdom(kingdom);
    LOG.info("User [{}] successfully created kingdom [{}]", owner, form.getName());
  }

  @Override
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

}

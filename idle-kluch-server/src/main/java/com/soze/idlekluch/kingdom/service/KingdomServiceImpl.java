package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.repository.KingdomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class KingdomServiceImpl implements KingdomService {

  private final KingdomRepository kingdomRepository;

  @Autowired
  public KingdomServiceImpl(final KingdomRepository kingdomRepository) {
    this.kingdomRepository = Objects.requireNonNull(kingdomRepository);
  }

  @Override
  public void addKingdom(final String owner, final RegisterKingdomForm form) {

  }

  @Override
  public boolean isNameAvailable(final String name) {
    return false;
  }

  @Override
  public void removeKingdom(final String owner) {

  }
}

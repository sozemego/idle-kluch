package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.kingdom.entity.Kingdom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class KingdomRepositoryImpl implements KingdomRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void addKingdom(final Kingdom kingdom) {

  }

  @Override
  public boolean isNameAvailable(final String name) {
    return false;
  }

  @Override
  public void removeKingdom(final Kingdom kingdom) {

  }

  @Override
  public void updateKingdom(final Kingdom kingdom) {

  }
}

package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.utils.jpa.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Objects;
import java.util.Optional;

@Repository
public class KingdomRepositoryImpl implements KingdomRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public void addKingdom(final Kingdom kingdom) {
    Objects.requireNonNull(kingdom);
    em.persist(kingdom);
  }

  @Override
  public Optional<Kingdom> getKingdom(final String name) {
    final Query query = em.createQuery("SELECT k FROM Kingdom k WHERE UPPER(k.name) = :name");
    query.setParameter("name", name.toUpperCase());

    return QueryUtils.getOptional(query, Kingdom.class);
  }

  @Override
  public Optional<Kingdom> getUsersKingdom(final String username) {
    Objects.requireNonNull(username);
    final Query query = em.createQuery("SELECT k FROM Kingdom k WHERE UPPER(k.owner.username) = :username");
    query.setParameter("username", username.toUpperCase());

    return QueryUtils.getOptional(query, Kingdom.class);
  }

  @Override
  public boolean isNameAvailable(final String name) {
    return false;
  }

  @Override
  public void removeKingdom(final Kingdom kingdom) {

  }

  @Override
  @Transactional
  public void updateKingdom(final Kingdom kingdom) {
    Objects.requireNonNull(kingdom);
    em.merge(kingdom);
  }
}

package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.jpa.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
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
  public Optional<Kingdom> getKingdom(final EntityUUID kingdomId) {
    Objects.requireNonNull(kingdomId);
    return Optional.ofNullable(em.find(Kingdom.class, kingdomId));
  }

  @Override
  public List<Kingdom> getAllKingdoms() {
    return em.createQuery("SELECT k FROM Kingdom k").getResultList();
  }

  @Override
  public Optional<Kingdom> getUsersKingdom(final String username) {
    Objects.requireNonNull(username);
    final Query query = em.createQuery("SELECT k FROM Kingdom k WHERE UPPER(k.owner.username) = :username");
    query.setParameter("username", username.toUpperCase());

    return QueryUtils.getOptional(query, Kingdom.class);
  }

  @Override
  @Transactional
  public void removeKingdom(final String name) {
    Objects.requireNonNull(name);

    final Kingdom kingdom = getKingdom(name)
                              .<EntityDoesNotExistException>orElseThrow(() -> {
                                return new EntityDoesNotExistException("Kingdom " + name + " does not exist", Kingdom.class);
                              });

    try {
      em.remove(kingdom);
    } catch (IllegalArgumentException e) {
      throw new EntityDoesNotExistException("Kingdom " + kingdom.getName() + " does not exist", Kingdom.class);
    }
  }

  @Override
  @Transactional
  public void updateKingdom(final Kingdom kingdom) {
    Objects.requireNonNull(kingdom);
    try {
      em.merge(kingdom);
    } catch (IllegalArgumentException e) {
      throw new EntityDoesNotExistException("Kingdom " + kingdom.getName() + " does not exist", Kingdom.class);
    }
  }
}

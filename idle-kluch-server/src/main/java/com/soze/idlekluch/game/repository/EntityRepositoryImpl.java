package com.soze.idlekluch.game.repository;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EntityRepositoryImpl implements EntityRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public void addEntity(final PersistentEntity entity) {
    Objects.requireNonNull(entity);
    em.persist(entity);
  }

  @Override
  @Transactional
  public void updateEntities(final List<PersistentEntity> entities) {
    Objects.requireNonNull(entities);
    entities.forEach(em::merge);
  }

  @Override
  public Optional<PersistentEntity> getEntity(final EntityUUID id) {
    Objects.requireNonNull(id);
    return Optional.ofNullable(em.find(PersistentEntity.class, id));
  }

  @Override
  public boolean entityExists(final EntityUUID id) {
    final Query query = em.createQuery("SELECT COUNT(pe.entityId) FROM PersistentEntity pe WHERE pe.entityId = :id");
    query.setParameter("id", id);
    return ((long) query.getSingleResult()) > 0;
  }

  @Override
  @Transactional
  public void deleteEntity(final EntityUUID id) {
    Objects.requireNonNull(id);
    getEntity(id).ifPresent(e -> em.remove(e));
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PersistentEntity> getAllEntities() {
    final Query query = em.createQuery("SELECT pe FROM PersistentEntity pe WHERE pe.template = false");
    return query.getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PersistentEntity> getAllEntityTemplates() {
    return em.createQuery("SELECT pe FROM PersistentEntity pe WHERE pe.template = true").getResultList();
  }

}

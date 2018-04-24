package com.soze.idlekluch.game.repository;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
  public Optional<PersistentEntity> getEntity(final EntityUUID id) {
    Objects.requireNonNull(id);

    return Optional.ofNullable(em.find(PersistentEntity.class, id));
  }

  @Override
  @Transactional
  public void deleteEntity(final EntityUUID id) {
    Objects.requireNonNull(id);
    getEntity(id).ifPresent(e -> em.remove(e));
  }

}

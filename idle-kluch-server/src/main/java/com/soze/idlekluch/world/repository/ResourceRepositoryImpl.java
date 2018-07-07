package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.jpa.QueryUtils;
import com.soze.idlekluch.kingdom.entity.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Resource> getAllAvailableResources() {
    return em.createQuery("SELECT r FROM Resource r").getResultList();
  }

  @Override
  @Transactional
  public void addResource(final Resource resource) {
    Objects.requireNonNull(resource);
    em.persist(resource);
  }

  @Override
  public Optional<Resource> getResource(final String name) {
    Objects.requireNonNull(name);
    final Query query = em.createQuery("SELECT r FROM Resource r WHERE UPPER(r.name) = :name");
    query.setParameter("name", name.toUpperCase());

    return QueryUtils.getOptional(query, Resource.class);
  }

  @Override
  public Optional<Resource> getResource(final EntityUUID id) {
    Objects.requireNonNull(id);
    final Query query = em.createQuery("SELECT r FROM Resource r WHERE r.resourceId = :id");
    query.setParameter("id", id);

    return QueryUtils.getOptional(query, Resource.class);
  }
}


package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.utils.jpa.QueryUtils;
import com.soze.idlekluch.world.entity.Forest;
import com.soze.idlekluch.world.entity.Tile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class WorldRepositoryImpl implements WorldRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Tile> getAllTiles() {
    final Query query = em.createQuery("SELECT t FROM Tile t");
    return query.getResultList();
  }

  @Override
  @Transactional
  public void addTile(final Tile tile) {
    Objects.requireNonNull(tile);
    em.persist(tile);
  }

  @Override
  @Transactional
  public void addTiles(final List<Tile> tiles) {
    Objects.requireNonNull(tiles);
    tiles.forEach(em::persist);
  }

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
  public List<Forest> getAllForests() {
    return em.createQuery("SELECT f FROM Forest f").getResultList();
  }

  @Override
  @Transactional
  public void addForest(final Forest forest) {
    Objects.requireNonNull(forest);
    em.persist(forest);
  }

}

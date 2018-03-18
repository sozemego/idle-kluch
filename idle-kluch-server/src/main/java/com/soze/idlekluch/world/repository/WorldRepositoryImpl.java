package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.world.entity.Tile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

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

}

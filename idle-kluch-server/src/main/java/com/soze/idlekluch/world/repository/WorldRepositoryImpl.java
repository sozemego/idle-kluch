package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.jpa.QueryUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.entity.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class WorldRepositoryImpl implements WorldRepository {

  private static final Logger LOG = LoggerFactory.getLogger(WorldRepositoryImpl.class);

  @PersistenceContext
  private EntityManager em;

  private final Map<TileId, Tile> tiles;

  public WorldRepositoryImpl() {
    this.tiles = new ConcurrentHashMap<>();
  }

  @PostConstruct
  public void setup() {
    getTileList()
      .forEach(tile -> tiles.put(tile.getTileId(), tile));
  }

  @Override
  public Optional<World> getCurrentWorld() {
    final Query query = em.createQuery("SELECT w FROM World w");
    final List<World> worlds = query.getResultList();
    return worlds.isEmpty() ? Optional.empty() : Optional.of(worlds.get(0));
  }

  @Override
  @Transactional
  public void saveWorld(final World world) {
    Objects.requireNonNull(world);
    if (getCurrentWorld().isPresent()) {
      em.merge(world);
    } else {
      em.persist(world);
    }
  }

  @Override
  public Map<TileId, Tile> getAllTiles() {
    return tiles;
  }

  @Override
  public Optional<Tile> getTile(final TileId tileId) {
    return Optional.ofNullable(tiles.get(tileId));
  }

  @Override
  @Transactional
  public void addTile(final Tile tile) {
    Objects.requireNonNull(tile);
    em.persist(tile);
    tiles.put(tile.getTileId(), tile);
  }

  @Override
  @Transactional
  public List<Tile> addTiles(final List<Tile> tiles) {
    Objects.requireNonNull(tiles);
    tiles.forEach(em::persist);
    tiles.forEach(tile -> this.tiles.put(tile.getTileId(), tile));
    return tiles;
  }

  @Override
  @Transactional
  public void removeTiles(final List<Tile> tiles) {
    Objects.requireNonNull(tiles);
    tiles.forEach(tile -> {
      final Tile foundTile = em.find(Tile.class, tile.getTileId());
      if (foundTile != null) {
        em.remove(foundTile);
      }
      this.tiles.remove(tile.getTileId());
    });
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

  private List<Tile> getTileList() {
    final Query query = em.createQuery("SELECT t FROM Tile t");
    return query.getResultList();
  }

}

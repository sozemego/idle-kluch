package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

@Repository
public class BuildingRepositoryImpl implements BuildingRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public void addBuilding(final Building building) {
    Objects.requireNonNull(building);
    em.persist(building);
  }

  @Override
  @Transactional
  public void updateBuilding(final Building building) {
    Objects.requireNonNull(building);
    em.merge(building);
  }

  @Override
  @Transactional
  public void removeBuilding(final EntityUUID buildingId) {
    Objects.requireNonNull(buildingId);

    final Building building = em.find(Building.class, buildingId);
    if (building != null) {
      em.remove(building);
    }
  }

  @Override
  public List<Building> getKingdomsBuildings(final EntityUUID kingdomId) {
    Objects.requireNonNull(kingdomId);

    final Query query = em.createQuery("SELECT b FROM Building b WHERE b.kingdom.kingdomId = :kingdomId");
    query.setParameter("kingdomId", kingdomId);

    return query.getResultList();
  }

  @Override
  public List<Building> getAllBuildings() {
    return em.createQuery("SELECT b FROM Building b").getResultList();
  }


}

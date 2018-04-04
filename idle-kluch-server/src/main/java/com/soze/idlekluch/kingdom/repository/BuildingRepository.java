package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import java.util.List;

public interface BuildingRepository {

  void addBuilding(final Building building);

  void updateBuilding(final Building building);

  void removeBuilding(final EntityUUID buildingId);

  List<Building> getKingdomsBuildings(final EntityUUID kingdomId);

  List<Building> getAllBuildings();

}

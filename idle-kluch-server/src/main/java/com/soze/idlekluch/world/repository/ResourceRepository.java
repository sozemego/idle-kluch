package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

  List<Resource> getAllAvailableResources();

  void addResource(Resource resource);

  Optional<Resource> getResource(String name);

}

package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

  List<Resource> getAllAvailableResources();

  void addResource(Resource resource);

  Optional<Resource> getResource(String name);

  Optional<Resource> getResource(final EntityUUID id);

}

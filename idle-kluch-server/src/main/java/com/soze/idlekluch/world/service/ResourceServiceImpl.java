package com.soze.idlekluch.world.service;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {


  private final ResourceRepository resourceRepository;

  @Autowired
  public ResourceServiceImpl(final ResourceRepository resourceRepository) {
    this.resourceRepository = Objects.requireNonNull(resourceRepository);
  }

  @Override
  public List<Resource> getAllAvailableResources() {
    return resourceRepository.getAllAvailableResources();
  }

  @Override
  public void addResource(final Resource resource) {
    resourceRepository.addResource(resource);
  }

  @Override
  public Optional<Resource> getResource(final String name) {
    return resourceRepository.getResource(name);
  }

  @Override
  public Optional<Resource> getResource(final EntityUUID id) {
    return resourceRepository.getResource(id);
  }
}

package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tree;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

public class TreeServiceImpl implements TreeService {

  private final WorldRepository worldRepository;

  @Autowired
  public TreeServiceImpl(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
  }

  @Override
  public List<Tree> getAllTrees() {
    return worldRepository.getAllTrees();
  }

  @Override
  public void addTree(final Tree tree) {
    worldRepository.addTree(tree);
  }
}

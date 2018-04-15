package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tree;

import java.util.List;

public interface TreeService {

  List<Tree> getAllTrees();

  void addTree(final Tree tree);


}

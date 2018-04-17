package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Forest;

import java.util.List;

public interface TreeService {

  List<Forest> getAllTrees();

  void addForest(final Forest forest);

  String getForestAsset(final String definitionId);

}

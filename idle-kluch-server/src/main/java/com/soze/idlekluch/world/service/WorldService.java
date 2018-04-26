package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tile;

import java.awt.*;
import java.util.Map;

public interface WorldService {

  Map<Point, Tile> getAllTiles();

  int getWorldWidth();

  int getWorldHeight();

  int getTileSize();

}

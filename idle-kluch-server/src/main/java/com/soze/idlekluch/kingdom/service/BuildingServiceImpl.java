package com.soze.idlekluch.kingdom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  @Value("buildings.json")
  private ClassPathResource buildings;

  @PostConstruct
  public void setup() {
    LOG.info("Initializing building service.");

    //read buildings here

    //create building descriptions here

    //then later create them, add to ECS, transform them etc.

    System.out.println(buildings.toString());
    try {
      System.out.println(buildings.getFile().getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}

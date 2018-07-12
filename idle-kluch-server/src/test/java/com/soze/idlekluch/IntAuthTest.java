package com.soze.idlekluch;

import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * Used for integration tests which require users to exist.
 */
public class IntAuthTest {

  @Autowired
  private UserService userService;

  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private GameEngine gameEngine;

  private final char[] password = CommonUtils.generateRandomString(15).toCharArray();

  /**
   * Creates a user with given name. Does nothing if this
   * user already exists.
   */
  protected void register(final String username) {
    Objects.requireNonNull(username);
    try {
      userService.addUser(new RegisterUserForm(username, password));
    } catch (Exception e) {

    }
  }

  /**
   * Creates a kingdom for given user.
   * @param username
   * @param kingdomName
   */
  protected Kingdom createKingdom(final String username, final String kingdomName) {
    return createKingdom(username, kingdomName, KingdomService.STARTING_IDLE_BUCKS);
  }

  protected Kingdom createKingdom(final String username, final String kingdomName, final long idleBucks) {
    register(username);
    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));
    final Kingdom kingdom = kingdomService.getKingdom(kingdomName).get();
    kingdom.setIdleBucks(idleBucks);
    kingdomService.updateKingdom(kingdom);

    buildingService
      .getOwnBuildings(username)
      .forEach(building -> gameEngine.deleteEntity((EntityUUID) building.getId()));

    return kingdom;
  }
}

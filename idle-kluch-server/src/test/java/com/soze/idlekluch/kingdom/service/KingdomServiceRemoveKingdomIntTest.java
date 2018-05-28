package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class KingdomServiceRemoveKingdomIntTest extends IntAuthTest {

  @Autowired
  private KingdomService kingdomService;

  @BeforeClass
  public static void setup() {
    DatabaseReset.resetDatabase();
  }

  @Test
  public void testRemoveKingdom() {
    final String kingdomName = CommonUtils.generateRandomString(12);
    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);
    assertTrue(kingdomService.getUsersKingdom(username).isPresent());

    kingdomService.removeKingdom(username);
    assertFalse(kingdomService.getUsersKingdom(username).isPresent());
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testRemoveKingdomDoesNotExist() {
    final String username = CommonUtils.generateRandomString(12);
    kingdomService.removeKingdom(username);
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testRemoveKingdomOnceAddedThenRemoved() {
    final String kingdomName = CommonUtils.generateRandomString(12);
    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    kingdomService.removeKingdom(username);
    kingdomService.removeKingdom(username);
  }

  @Test
  public void testAddKingdomOnceRemoved() {
    final String kingdomName = CommonUtils.generateRandomString(12);
    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    assertTrue(kingdomService.getUsersKingdom(username).isPresent());
    kingdomService.removeKingdom(username);
    assertFalse(kingdomService.getUsersKingdom(username).isPresent());
    createKingdom(username, kingdomName);
    assertTrue(kingdomService.getUsersKingdom(username).isPresent());
  }

  @Test
  public void testRemoveSameKingdomManyTimes() throws Exception {
    final String username = CommonUtils.generateRandomString(12);
    final String anotherUsername = CommonUtils.generateRandomString(12);
    createKingdom(username, CommonUtils.generateRandomString(12));
    createKingdom(anotherUsername, CommonUtils.generateRandomString(12));

    final List<Thread> threads = new ArrayList<>();
    final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

    for (int i = 0; i < 5; i++) {
      final Thread thread = new Thread(() -> {
        for (int j = 0; j < 20; j++) {
          try {
            kingdomService.removeKingdom(username);
          } catch (UserDoesNotHaveKingdomException e) {
            exceptions.add(e);
          }
        }
      });
      threads.add(thread);
      thread.start();

      final Thread anotherThread = new Thread(() -> {
        for (int j = 0; j < 20; j++) {
          try {
            kingdomService.removeKingdom(anotherUsername);
          } catch (UserDoesNotHaveKingdomException e) {
            exceptions.add(e);
          }
        }
      });
      threads.add(anotherThread);
      anotherThread.start();
    }

    for (final Thread thread: threads) {
      thread.join();
    }

    assertEquals(((5 * 20) * 2) - 2, exceptions.size());
  }


}

package com.soze.idlekluch.kingdom;

import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class,
  }
)
@WebAppConfiguration
public class BuildingIntTests {

  @Before
  public void setup() {
    DatabaseReset.resetDatabase();
  }

  @Test
  public void test() {
  }

}

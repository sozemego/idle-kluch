package com.soze.idlekluch.world.service;

import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class ChunkCreationIntTest {

  @Autowired
  private WorldService worldService;

  @Autowired
  private WorldRepository worldRepository;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.deleteData();
  }

  @Before
  public void setup() {
    worldRepository.removeTiles(new ArrayList<>(worldService.getAllTiles().values()));
  }

  @Test
  public void testCreateChunksMultiThreaded() throws Exception {
    final List<Thread> threads = new ArrayList<>();

    for(int i = 0; i < 20; i++) {
      final int y = i;
      final Thread thread = new Thread(() -> {
        for(int j = 0; j < 20; j++) {
          worldService.createWorldChunk(new TileId(0, y + j), 20, 20);
        }
      });
      thread.start();
      threads.add(thread);
    }

    for(final Thread thread: threads) {
      thread.join();
    }

    assertEquals(1239, worldService.getAllTiles().size());
  }


}

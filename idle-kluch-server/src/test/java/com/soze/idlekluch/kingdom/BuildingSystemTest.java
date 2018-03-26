package com.soze.idlekluch.kingdom;

import com.soze.idlekluch.BaseAuthTest;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class BuildingSystemTest extends BaseAuthTest {

  @Before
  public void setup() {
    super.setup();
    DatabaseReset.resetDatabase();
  }

  @Test
  public void testGetAllBuildings() {
    final ResponseEntity response = client.get(Routes.BUILDING_GET_ALL);
    final List<BuildingDefinitionDto> dtos = JsonUtils.jsonToList((String) response.getBody(), BuildingDefinitionDto.class);
    System.out.println(dtos);

    assertTrue(dtos != null);
    assertTrue(dtos.size() > 0);
  }

}

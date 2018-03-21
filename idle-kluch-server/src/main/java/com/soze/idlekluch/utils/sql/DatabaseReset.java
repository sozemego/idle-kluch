package com.soze.idlekluch.utils.sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used only for system testing,
 * mainly for resetting the database.
 */
public class DatabaseReset {

  private static final String DB_USER = System.getenv("IDLE_KLUCH_DATABASE_USERNAME");
  private static final String DB_PASS = System.getenv("IDLE_KLUCH_DATABASE_PASSWORD");
  private static final String CONNECTION_STRING = "jdbc:postgresql://127.0.0.1:5432/idle_kluch_test";

  /**
   * This method will load a .sql script located at /sql
   * and execute it. This script will hopefully contain drop / recreate scripts.
   */
  public static void resetDatabase() {

    try {
      List<String> file = Files.lines(Paths.get("sql/create.sql"))
        .collect(Collectors.toList());
      String fileContent = file.stream().reduce("", (prev, cur) -> prev += cur + '\n');
      executeSql(fileContent);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static void executeSql(String sql) {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    Connection connection = null;

    try {

      connection = DriverManager.getConnection(
        CONNECTION_STRING,
        DB_USER,
        DB_PASS
      );


      PreparedStatement statement = connection.prepareStatement(sql);
      statement.execute();
      System.out.println("CLEARED AND RESTORED THE DB.");

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }


}

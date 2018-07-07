package com.soze.idlekluch;

import org.hibernate.dialect.PostgreSQL95Dialect;

import java.sql.Types;

public class JsonPostgreSQLDialect extends PostgreSQL95Dialect {

  public JsonPostgreSQLDialect() {
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
  }

}

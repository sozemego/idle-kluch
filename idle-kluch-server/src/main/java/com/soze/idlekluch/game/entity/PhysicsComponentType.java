package com.soze.idlekluch.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.idlekluch.core.utils.JsonUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class PhysicsComponentType implements UserType {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.addMixIn(EntityUUID.class, IgnoreEntityUUIDType.class);
  }

  @Override
  public int[] sqlTypes() {
    return new int[]{ Types.JAVA_OBJECT };
  }

  @Override
  public Class returnedClass() {
    return PhysicsComponent.class;
  }

  @Override
  public Object nullSafeGet(final ResultSet rs,
                            final String[] names,
                            final SharedSessionContractImplementor session,
                            final Object owner) throws HibernateException, SQLException {
    final String json = rs.getString(names[0]);
    if (json == null) {
      return null;
    }

    try {
      return MAPPER.readValue(json.getBytes("UTF-8"), returnedClass());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void nullSafeSet(final PreparedStatement st,
                          final Object value,
                          final int index,
                          final SharedSessionContractImplementor session) throws HibernateException, SQLException {

    if(value == null) {
      st.setNull(index, Types.OTHER);
      return;
    }

    final String json = JsonUtils.objectToJson(value, MAPPER);
    st.setObject(index, json);
  }

  @Override
  public Object deepCopy(final Object value) throws HibernateException {
    if(value == null) {
      return null;
    }

    return ((BaseComponent) value).copy();
  }

  @Override
  public boolean equals(final Object x, final Object y) throws HibernateException {
    return Objects.equals(x, y);
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public int hashCode(final Object x) throws HibernateException {
    return Objects.hashCode(x);
  }

  @Override
  public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Serializable disassemble(final Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
    return deepCopy(original);
  }

  @JsonIgnoreType
  private static class IgnoreEntityUUIDType {

  }
}

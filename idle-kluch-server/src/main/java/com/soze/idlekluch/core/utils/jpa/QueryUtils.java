package com.soze.idlekluch.core.utils.jpa;

import javax.persistence.Query;
import java.util.Objects;
import java.util.Optional;

public class QueryUtils {

  public static <T> Optional<T> getOptional(final Query query, final Class<T> clazz) {
    Objects.requireNonNull(clazz);
    try {
      final Object result = query.getSingleResult();
      return Optional.of(clazz.cast(result));
    } catch (Exception e) {
      //TODO find which exceptions can be safely ignored and which have to be rethrown
      return Optional.empty();
    }
  }

}

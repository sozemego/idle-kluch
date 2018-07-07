package com.soze.idlekluch.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.idlekluch.core.utils.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class JsonUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
  }

  /**
   * Attempts to stringify a given object to json.
   * If the conversion fails, wraps {@link JsonProcessingException} in {@link IllegalArgumentException}.
   */
  public static String objectToJson(final Object object) {
    return objectToJson(object, MAPPER);
  }

  /**
   * Attempts to stringify a given object to json.
   * If the conversion fails, wraps {@link JsonProcessingException} in {@link IllegalArgumentException}.
   */
  public static String objectToJson(final Object object, final ObjectMapper mapper) {
    Objects.requireNonNull(object);
    Objects.requireNonNull(mapper);

    try {
      return mapper.writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      //TODO throw own exception
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Parses a given string and attempts to construct an object of a given class.
   * If the conversion fails, wraps {@link IOException} in {@link IllegalArgumentException}.
   */
  public static <T> T jsonToObject(final String json, final Class<T> target) {
    Objects.requireNonNull(json);
    Objects.requireNonNull(target);

    try {
      return MAPPER.readValue(json, target);
    } catch (IOException e) {
      //TODO throw own exception
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Attempts to parse a given json to a list of objects of Class clazz.
   * If the conversion fails, wraps {@link IOException} in a {@link IllegalArgumentException}.
   */
  public static <T> List<T> jsonToList(final String json, final Class<T> clazz) {
    Objects.requireNonNull(json);
    Objects.requireNonNull(clazz);

    try {
      return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
    } catch (IOException e) {
      //TODO throw own exception
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Parses a string into a Map (json Object).
   * If the parsing fails, wraps {@link IOException} in a {@link IllegalArgumentException}.
   */
  public static <T, E> Map<T, E> jsonToMap(final String json, final Class<T> key, final Class<E> value) {
    Objects.requireNonNull(json);
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);

    try {
      return MAPPER.readValue(json, MAPPER.getTypeFactory().constructMapType(HashMap.class, key, value));
    } catch (IOException e) {
      //TODO throw own exception
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Loads a string from a given resource, then parses the result into a map (json Object).
   * @see FileUtils#readClassPathResource(ClassPathResource)
   * @see JsonUtils#jsonToMap(String, Class, Class)
   */
  public static <T, E> Map<T, E> resourceToMap(final ClassPathResource resource, final Class<T> key, final Class<E> value) {
    final String fileContent = FileUtils.readClassPathResource(resource);
    return jsonToMap(fileContent, key, value);
  }

  /**
   * Loads a string from a given resource, then parses the result into a list.
   * @see FileUtils#readClassPathResource(ClassPathResource)
   * @see JsonUtils#jsonToList(String, Class)
   */
  public static <T> List<T> resourceToList(final ClassPathResource resource, final Class<T> clazz) {
    final String fileContent = FileUtils.readClassPathResource(resource);
    return jsonToList(fileContent, clazz);
  }

}

package com.soze.idlekluch.core.utils.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = false)
public class UUIDConverter implements AttributeConverter<UUID, UUID> {

  @Override
  public UUID convertToDatabaseColumn(UUID attribute) {
    return attribute;
  }

  @Override
  public UUID convertToEntityAttribute(UUID dbData) {
    return dbData;
  }
}
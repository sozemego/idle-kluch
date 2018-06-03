package com.soze.idlekluch.core.utils.io;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Objects;

public class FileUtils {

  public static String readInputStream(final InputStream inputStream) {
    Objects.requireNonNull(inputStream);
    final StringBuilder stringBuilder = new StringBuilder();

    try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      final int bufferSize = 1024;
      final char[] buffer = new char[bufferSize];
      int bytesRead;

      while((bytesRead = br.read(buffer, 0, buffer.length)) > 0) {
        stringBuilder.append(buffer, 0, bytesRead);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      return stringBuilder.toString();
    }
  }

  public static String readClassPathResource(final ClassPathResource classPathResource) {
    Objects.requireNonNull(classPathResource);
    try {
      return readInputStream(classPathResource.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}

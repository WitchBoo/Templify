package me.whereareiam.templify.common.files.content;

import com.google.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Singleton
public final class ContentWriter {
  public void writeIfChanged(Path path, String original, String updated) {
    if (Objects.equals(original, updated)) {
      return;
    }
    try {
      Files.writeString(path, updated, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      // ignore writes that fail
    }
  }
}



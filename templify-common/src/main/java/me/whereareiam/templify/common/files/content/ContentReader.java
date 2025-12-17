package me.whereareiam.templify.common.files.content;

import com.google.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

@Singleton
public final class ContentReader {
  public @Nullable String read(Path path) {
    try {
      var bytes = Files.readAllBytes(path);
      var text = new String(bytes, StandardCharsets.UTF_8);
      if (text.indexOf('\0') >= 0) {
        return null;
      }

      return text;
    } catch (IOException exception) {
      return null;
    }
  }
}



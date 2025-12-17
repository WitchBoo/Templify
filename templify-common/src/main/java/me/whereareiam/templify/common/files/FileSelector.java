package me.whereareiam.templify.common.files;

import com.google.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public final class FileSelector {
  public List<Path> findFiles(Path root, List<PathMatcher> matchers) throws IOException {
    try (Stream<Path> stream = Files.walk(root)) {
      return stream
        .filter(Files::isRegularFile)
        .filter(path -> this.matchesAny(path, matchers))
        .toList();
    }
  }

  private boolean matchesAny(Path path, List<PathMatcher> matchers) {
    for (var matcher : matchers) {
      if (matcher.matches(path)) {
        return true;
      }
    }
    return false;
  }
}



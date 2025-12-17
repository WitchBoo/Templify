package me.whereareiam.templify.common.config.resolver;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import me.whereareiam.templify.ConfigurationTypeResolver;
import me.whereareiam.templify.type.ConfigurationType;

/**
 * Resolves the configuration selector based on a small marker file next to the
 * module's data directory. If no marker is present, JSON is used by default.
 */
public final class FileSystemConfigurationTypeResolver implements ConfigurationTypeResolver {
  private final Path dataPath;

  @Inject
  public FileSystemConfigurationTypeResolver(@Named("dataPath") Path dataPath) {
    this.dataPath = dataPath;
  }

  @Override
  public ConfigurationType getConfigurationType() {
    try (Stream<Path> paths = Files.list(this.dataPath)) {
      Optional<Path> configFile = paths
        .filter(Files::isRegularFile)
        .filter(p -> p.getFileName().toString().startsWith("selector="))
        .findFirst();

      if (configFile.isPresent()) {
        String[] parts = configFile.get().getFileName().toString().split("=", 2);
        return ConfigurationType.valueOf(parts[1].toUpperCase());
      } else {
        Files.createFile(this.dataPath.resolve("selector=JSON"));
        return ConfigurationType.JSON;
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to get configuration selector", e);
    }
  }
}

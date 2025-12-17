package me.whereareiam.templify.common.config.provider.rules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.configura.Config;
import me.whereareiam.templify.ConfigurationTypeResolver;
import me.whereareiam.templify.common.config.provider.rules.example.BasicRulesExample;
import me.whereareiam.templify.common.config.provider.rules.example.ConditionalRulesExample;
import me.whereareiam.templify.common.config.provider.rules.example.RulesExample;
import me.whereareiam.templify.model.config.Replacements;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.type.ConfigurationType;

/**
 * Loads all replacement rule configurations from the rules directory and its sub-folders.
 * <p>
 * Each file that matches the active {@link ConfigurationType} extension is treated as a
 * {@link Replacements} config and all contained {@link Replacement} entries are collected.
 * <p>
 * When the rules directory is empty, example configuration files are automatically created.
 */
@Slf4j
@Singleton
public final class RulesProvider implements Provider<List<Replacement>> {
  private final Path rulesPath;
  private final ConfigurationTypeResolver typeResolver;

  private List<Replacement> cached;

  @Inject
  public RulesProvider(@Named("rulesPath") Path rulesPath, ConfigurationTypeResolver typeResolver) {
    this.rulesPath = rulesPath;
    this.typeResolver = typeResolver;
  }

  @Override
  public List<Replacement> get() {
    if (this.cached != null)
      return this.cached;

    ConfigurationType type = this.typeResolver.getConfigurationType();
    String extension = type.getExtension();

    ensureRulesDirectoryExists();
    createExampleFilesIfEmpty(extension);

    List<Replacement> allRules = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(this.rulesPath)) {
      paths
        .filter(Files::isRegularFile)
        .filter(path -> path.getFileName().toString().endsWith(extension))
        .forEach(path -> {
          Replacements replacements = Config.update(path, Replacements.class);
          if (replacements != null && replacements.getRules() != null) {
            allRules.addAll(replacements.getRules());
          }
        });
    } catch (IOException exception) {
      // If we fail to read rules we simply treat it as "no rules found" but log it for visibility.
      log.warn("Failed to read replacement rules from {}", this.rulesPath, exception);
    }

    this.cached = List.copyOf(allRules);
    return this.cached;
  }

  private void ensureRulesDirectoryExists() {
    try {
      if (!Files.exists(this.rulesPath))
        Files.createDirectories(this.rulesPath);
    } catch (IOException exception) {
      log.warn("Failed to create rules directory at {}", this.rulesPath, exception);
    }
  }

  private void createExampleFilesIfEmpty(String extension) {
    try (Stream<Path> files = Files.list(this.rulesPath)) {
      boolean hasConfigs = files.anyMatch(p -> p.toString().endsWith(extension));
      if (hasConfigs)return;
    } catch (IOException exception) {
      log.warn("Failed to check rules directory contents", exception);
      return;
    }

    for (RulesExample example : getExamples()) {
      Path examplePath = this.rulesPath.resolve(example.fileName() + extension);
      try {
        Config.getDefaultWriter().write(examplePath, example.create());
      } catch (Exception exception) {
        log.warn("Failed to create example file: {}", examplePath.getFileName(), exception);
      }
    }
  }

  /**
   * Returns all registered example configurations.
   * Add new examples here when creating additional example classes.
   */
  private List<RulesExample> getExamples() {
    return List.of(
      new BasicRulesExample(),
      new ConditionalRulesExample()
    );
  }
}

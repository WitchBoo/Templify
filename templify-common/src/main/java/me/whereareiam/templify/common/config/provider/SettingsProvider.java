package me.whereareiam.templify.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.nio.file.Path;
import me.whereareiam.configura.Config;
import me.whereareiam.templify.model.config.Settings;

/**
 * Configura-backed provider for {@link Settings}, similar to Socialismus.
 */
@Singleton
public final class SettingsProvider implements Provider<Settings> {
  private final Path dataPath;
  private Settings cached;

  @Inject
  public SettingsProvider(@Named("dataPath") Path dataPath) {
    this.dataPath = dataPath;
  }

  /**
   * Lazily loads or creates the {@link Settings} configuration using Configura.
   * Subsequent calls return the same cached instance.
   */
  @Override
  public Settings get() {
    if (this.cached == null) {
      this.cached = Config.update(this.dataPath.resolve("settings"), Settings.class);
    }

    return this.cached;
  }
}

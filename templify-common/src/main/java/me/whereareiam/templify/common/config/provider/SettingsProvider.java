package me.whereareiam.templify.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.nio.file.Path;
import me.whereareiam.configura.Config;
import me.whereareiam.templify.Reloadable;
import me.whereareiam.templify.common.config.template.SettingsTemplate;
import me.whereareiam.templify.common.provider.ReloadableProvider;
import me.whereareiam.templify.model.config.Settings;

/**
 * Configura-backed provider for {@link Settings}.
 */
@Singleton
public final class SettingsProvider implements Provider<Settings>, Reloadable {
  private final Path dataPath;
  private Settings cached;
  private boolean templatesRegistered;

  @Inject
  public SettingsProvider(@Named("dataPath") Path dataPath, ReloadableProvider reloadableProvider) {
    this.dataPath = dataPath;
    reloadableProvider.register(this);
  }

  @Override
  public Settings get() {
    if (this.cached == null) {
      ensureTemplatesRegistered();
      this.cached = Config.update(this.dataPath.resolve("settings"), Settings.class);
    }
    return this.cached;
  }

  @Override
  public void reload() {
    ensureTemplatesRegistered();
    this.cached = Config.update(this.dataPath.resolve("settings"), Settings.class);
  }

  private void ensureTemplatesRegistered() {
    if (templatesRegistered) return;
    Config.registerTemplate(SettingsTemplate.class);
    templatesRegistered = true;
  }
}

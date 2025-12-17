package me.whereareiam.templify.common.config.template;

import com.google.inject.Singleton;
import java.util.List;
import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;

/**
 * Default template / initial values for {@link Settings}.
 * This is used by Configura when creating or updating the settings config.
 */
@Singleton
public final class SettingsTemplate implements TemplateProvider<Settings> {
  @Override
  public Settings supply(Settings settings) {
    Settings.DefaultSection defaults = new Settings.DefaultSection();
    defaults.setSearchType(SearchType.ALL);
    defaults.setReplaceType(ReplaceType.FIRST);
    settings.setDefaults(defaults);

    Settings.PathSection paths = new Settings.PathSection();
    paths.setFilePatterns(List.of(
      "**/*.yml",
      "**/*.txt"
    ));
    settings.setPaths(paths);

    Settings.LimitSection limits = new Settings.LimitSection();
    limits.setMaxFileSizeBytes(524_288L); // 512 KiB
    settings.setLimits(limits);

    return settings;
  }
}

package me.whereareiam.templify.common.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.reader.ConfigReader;
import me.whereareiam.configura.type.Format;
import me.whereareiam.configura.writer.ConfigWriter;
import me.whereareiam.templify.common.config.template.SettingsTemplate;
import me.whereareiam.templify.ConfigurationTypeResolver;
import me.whereareiam.templify.type.ConfigurationType;

/**
 * Bootstraps the global Configura reader / writer for Templify based on the
 * resolved {@link ConfigurationType}. JSON is used as the default format.
 */
@Singleton
public final class ConfiguraBootstrap {
  @Inject
  public ConfiguraBootstrap(ConfigurationTypeResolver resolver) {
    // Resolve the preferred configuration format
    ConfigurationType type = resolver.getConfigurationType();
    Format format = (type == ConfigurationType.JSON) ? Format.JSON : Format.YAML;

    // Configure global reader / writer with chosen format
    ConfigReader reader = Config.reader(format);
    ConfigWriter writer = Config.writer(format);
    Config.setReader(reader);
    Config.setWriter(writer);

    // Register templates
    Config.registerTemplate(SettingsTemplate.class);
  }
}

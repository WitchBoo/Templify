package me.whereareiam.templify;

import me.whereareiam.templify.type.ConfigurationType;

/**
 * Resolves which configuration format should be used by Templify.
 */
public interface ConfigurationTypeResolver {

  /**
   * Returns the preferred configuration selector.
   *
   * @return the resolved {@link ConfigurationType}
   */
  ConfigurationType getConfigurationType();
}

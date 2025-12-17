package me.whereareiam.templify.common.config.provider.rules.example;

import me.whereareiam.templify.model.config.Replacements;

/**
 * Provides an example rules configuration.
 */
public interface RulesExample {
  /**
   * @return the filename for this example (without extension)
   */
  String fileName();

  /**
   * @return the example Replacements configuration
   */
  Replacements create();
}

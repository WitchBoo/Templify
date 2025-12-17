package me.whereareiam.templify.type;

import lombok.Getter;

/**
 * Enumeration of supported configuration file types in Templify.
 * Defines the available configuration formats and their corresponding file extensions.
 */
@Getter
public enum ConfigurationType {
  /**
   * YAML configuration format with .yml extension.
   */
  YAML(".yml"),

  /**
   * JSON configuration format with .json extension.
   */
  JSON(".json");

  /**
   * The file extension associated with this configuration selector.
   */
  private final String extension;

  ConfigurationType(String extension) {
    this.extension = extension;
  }

}

package me.whereareiam.templify.replacement;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface ReplacementService {
  /**
   * Apply template replacements to files in the given service directory.
   *
   * @param serviceInfo the service info snapshot
   * @param serviceDirectory the directory containing files to process
   * @param template optional template name for rule matching
   */
  void apply(@NotNull ServiceInfoSnapshot serviceInfo, @NotNull Path serviceDirectory, @Nullable String template);
}
package me.whereareiam.templify.replacement;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import java.nio.file.Path;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface ReplacementService {
  /**
   * Apply template replacements to files in the given service directory.
   *
   * @param serviceInfo the service info snapshot
   * @param serviceDirectory the directory containing files to process
   * @param template optional template name for rule matching
   */
  void apply(@NonNull ServiceInfoSnapshot serviceInfo, @NonNull Path serviceDirectory, @Nullable String template);
}
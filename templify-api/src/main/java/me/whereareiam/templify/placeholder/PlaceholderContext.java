package me.whereareiam.templify.placeholder;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import java.nio.file.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.placeholder.provider.PlaceholderProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Contextual information available to {@link PlaceholderProvider} implementations.
 */
@Getter
@RequiredArgsConstructor
public final class PlaceholderContext {
  @Nullable
  private final Path file;

  @Nullable
  private final ServiceInfoSnapshot serviceInfo;
}
package me.whereareiam.templify.placeholder.provider;

import me.whereareiam.templify.placeholder.PlaceholderContext;
import org.jetbrains.annotations.Nullable;

/**
 * Provider for resolving a specific placeholder value.
 *
 * <p>Each provider is registered with a specific key and is responsible for resolving
 * that placeholder's value based on the provided context.
 */
@FunctionalInterface
public interface PlaceholderProvider {
  /**
   * Resolves the placeholder value for the registered key.
   *
   * @param context the placeholder context containing all available information
   * @return the resolved placeholder value, or {@code null} if the value cannot be resolved
   */
  @Nullable
  String getValue(PlaceholderContext context);
}



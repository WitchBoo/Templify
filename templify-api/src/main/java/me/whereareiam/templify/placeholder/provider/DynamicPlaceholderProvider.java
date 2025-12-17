package me.whereareiam.templify.placeholder.provider;

import me.whereareiam.templify.placeholder.PlaceholderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provider for resolving dynamic placeholders with pattern-based keys.
 *
 * <p>Unlike {@link PlaceholderProvider} which handles exact key matches,
 * this provider can handle placeholders with dynamic suffixes like
 * {@code %env:HOME%} or {@code %config:some.path%}.
 */
public interface DynamicPlaceholderProvider {
  /**
   * Checks if this provider can handle the given placeholder key.
   *
   * @param key the normalized placeholder key (lowercase, without % delimiters)
   * @return {@code true} if this provider can resolve the placeholder
   */
  boolean matches(@NotNull String key);

  /**
   * Resolves the placeholder value for the given key.
   *
   * @param key the normalized placeholder key (lowercase, without % delimiters)
   * @param context the placeholder context containing all available information
   * @return the resolved placeholder value, or {@code null} if the value cannot be resolved
   */
  @Nullable
  String getValue(@NotNull String key, @NotNull PlaceholderContext context);
}

package me.whereareiam.templify.placeholder;

import me.whereareiam.templify.placeholder.provider.DynamicPlaceholderProvider;
import me.whereareiam.templify.placeholder.provider.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Registry for managing and resolving placeholder providers.
 *
 * <p>This registry allows registration of placeholder providers with specific keys
 * and provides methods to resolve placeholder values. Keys are normalized
 * (lowercase, without % delimiters) for consistent lookup.
 */
public interface PlaceholderRegistry {
  /**
   * Registers a placeholder provider for a specific key with default priority (0).
   *
   * @param key the placeholder key (will be normalized: lowercase, % stripped)
   * @param provider the provider that resolves the placeholder value
   */
  void register(@NotNull String key, @NotNull PlaceholderProvider provider);

  /**
   * Registers a placeholder provider for a specific key with a custom priority.
   *
   * <p>Higher priority providers will be checked first. If multiple providers
   * are registered for the same key, the one with the highest priority that
   * returns a non-null value will be used.
   *
   * @param key the placeholder key (will be normalized: lowercase, % stripped)
   * @param provider the provider that resolves the placeholder value
   * @param priority the priority (higher = checked first)
   */
  void register(@NotNull String key, @NotNull PlaceholderProvider provider, int priority);

  /**
   * Registers a dynamic placeholder provider.
   *
   * <p>Dynamic providers handle pattern-based placeholders like {@code %env:HOME%}
   * where the suffix is variable. They are checked after exact-match providers.
   *
   * @param provider the dynamic provider to register
   */
  void register(@NotNull DynamicPlaceholderProvider provider);

  /**
   * Registers a dynamic placeholder provider with a custom priority.
   *
   * @param provider the dynamic provider to register
   * @param priority the priority (higher = checked first)
   */
  void register(@NotNull DynamicPlaceholderProvider provider, int priority);

  /**
   * Resolves a placeholder value for the given key.
   *
   * <p>The key will be normalized (lowercase, % stripped) before lookup.
   *
   * @param key the placeholder key
   * @param context the placeholder context
   * @return the resolved value, or {@code null} if no provider can resolve it
   */
  @Nullable
  String getValue(@NotNull String key, @NotNull PlaceholderContext context);

  /**
   * Gets all registered placeholders resolved with their values.
   *
   * @param context the placeholder context
   * @param withPercent if {@code true}, keys will include % delimiters (e.g., "%nodeId%"),
   *                    if {@code false}, keys will be plain (e.g., "nodeId")
   * @return a map of all resolved placeholder keys to their values
   */
  @NotNull
  Map<String, String> getAll(@NotNull PlaceholderContext context, boolean withPercent);
}
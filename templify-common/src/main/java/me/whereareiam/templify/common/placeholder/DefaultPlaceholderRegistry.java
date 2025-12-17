package me.whereareiam.templify.common.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;
import me.whereareiam.templify.placeholder.provider.DynamicPlaceholderProvider;
import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.provider.PlaceholderProvider;
import me.whereareiam.templify.placeholder.PlaceholderRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link PlaceholderRegistry}.
 *
 * <p>This registry manages placeholder providers with support for priority-based resolution.
 * Keys are normalized (lowercase, % stripped) for consistent lookup.
 */
public final class DefaultPlaceholderRegistry implements PlaceholderRegistry {
  private final Map<String, List<ProviderEntry>> providers = new ConcurrentHashMap<>();
  private final List<DynamicProviderEntry> dynamicProviders = new CopyOnWriteArrayList<>();

  @Override
  public void register(@NonNull String key, @NonNull PlaceholderProvider provider) {
    this.register(key, provider, 0);
  }

  @Override
  public void register(@NonNull String key, @NonNull PlaceholderProvider provider, int priority) {
    var normalizedKey = this.normalizeKey(key);
    this.providers.computeIfAbsent(normalizedKey, _ -> new ArrayList<>())
      .add(new ProviderEntry(provider, priority));

    // Sort by priority (higher first)
    this.providers.get(normalizedKey).sort((a, b) -> Integer.compare(b.priority, a.priority));
  }

  @Override
  public void register(@NonNull DynamicPlaceholderProvider provider) {
    this.register(provider, 0);
  }

  @Override
  public void register(@NonNull DynamicPlaceholderProvider provider, int priority) {
    this.dynamicProviders.add(new DynamicProviderEntry(provider, priority));
    this.dynamicProviders.sort((a, b) -> Integer.compare(b.priority, a.priority));
  }

  @Override
  public @Nullable String getValue(@NonNull String key, @NonNull PlaceholderContext context) {
    var normalizedKey = this.normalizeKey(key);

    // Try exact match providers first
    var entries = this.providers.get(normalizedKey);
    if (entries != null && !entries.isEmpty()) {
      for (var entry : entries) {
        var value = entry.provider.getValue(context);
        if (value != null) return value;
      }
    }

    // Try dynamic providers
    for (var entry : this.dynamicProviders) {
      if (entry.provider.matches(normalizedKey)) {
        var value = entry.provider.getValue(normalizedKey, context);
        if (value != null) return value;
      }
    }

    return null;
  }

  @Override
  public @NonNull Map<String, String> getAll(@NonNull PlaceholderContext context, boolean withPercent) {
    var result = new HashMap<String, String>();
    for (var entry : this.providers.entrySet()) {
      var key = entry.getKey();
      var value = this.getValue(key, context);
      if (value != null) {
        var displayKey = withPercent ? "%" + key + "%" : key;
        result.put(displayKey, value);
      }
    }

    return result;
  }

  /**
   * Normalizes a placeholder key by removing % delimiters and converting to lowercase.
   *
   * @param key the key to normalize
   * @return the normalized key
   */
  private String normalizeKey(@NonNull String key) {
    return key.replace("%", "").toLowerCase();
  }

  /**
   * Internal entry for storing provider with priority.
   */
  private record ProviderEntry(@NonNull PlaceholderProvider provider, int priority) {}

  /**
   * Internal entry for storing dynamic provider with priority.
   */
  private record DynamicProviderEntry(@NonNull DynamicPlaceholderProvider provider, int priority) {}
}



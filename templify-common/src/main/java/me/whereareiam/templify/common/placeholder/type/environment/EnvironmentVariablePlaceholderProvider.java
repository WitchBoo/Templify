package me.whereareiam.templify.common.placeholder.type.environment;

import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.provider.DynamicPlaceholderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Dynamic placeholder provider for system environment variables.
 *
 * <p>Handles placeholders in the format {@code %env:VARIABLE_NAME%}, where
 * VARIABLE_NAME is any system environment variable.
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code %env:HOME%} - resolves to user's home directory</li>
 *   <li>{@code %env:PATH%} - resolves to system PATH</li>
 *   <li>{@code %env:JAVA_HOME%} - resolves to Java installation directory</li>
 * </ul>
 */
public final class EnvironmentVariablePlaceholderProvider implements DynamicPlaceholderProvider {
  private static final String PREFIX = "env:";

  @Override
  public boolean matches(@NotNull String key) {
    return key.startsWith(PREFIX);
  }

  @Override
  public @Nullable String getValue(@NotNull String key, @NotNull PlaceholderContext context) {
    var variableName = key.substring(PREFIX.length());
    if (variableName.isEmpty())
      return null;

    return System.getenv(variableName);
  }
}

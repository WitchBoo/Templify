package me.whereareiam.templify;

import com.google.inject.Injector;
import lombok.Getter;
import me.whereareiam.templify.placeholder.PlaceholderRegistry;
import me.whereareiam.templify.replacement.ReplacementOperationRegistry;
import me.whereareiam.templify.replacement.ReplacementService;
import org.jetbrains.annotations.NotNull;

/**
 * Main API access point for the Templify module.
 *
 * <p>External modules should use this class to access Templify services.
 * All services become available after the module is loaded.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * // Check if API is ready
 * if (!TemplifyAPI.isInitialized()) {
 *     getLogger().warning("Templify not ready yet!");
 *     return;
 * }
 *
 * // Get the placeholder registry
 * PlaceholderRegistry registry = TemplifyAPI.getPlaceholderRegistry();
 * registry.register("myKey", context -> "myValue");
 *
 * // Or get any service by class
 * ReplacementService service = TemplifyAPI.getService(ReplacementService.class);
 * }</pre>
 *
 * <p><b>Important:</b> Always check {@link #isInitialized()} before accessing services.</p>
 */
@SuppressWarnings("unused")
public final class TemplifyAPI {
  private static volatile Injector injector;
  @Getter
  private static volatile boolean initialized = false;

  private TemplifyAPI() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  /**
   * Initializes the API with the Guice injector.
   * <p>This method is called internally by Templify during startup.
   * External modules should never call this method.</p>
   *
   * @param injector the Guice injector
   * @throws IllegalStateException if already initialized
   */
  public static void initialize(@NotNull Injector injector) {
    if (TemplifyAPI.injector != null) throw new IllegalStateException("TemplifyAPI is already initialized");

    TemplifyAPI.injector = injector;
    TemplifyAPI.initialized = true;
  }

  /**
   * Shuts down the API and clears the injector reference.
   * <p>This method is called internally by Templify during shutdown.
   * External modules should never call this method.</p>
   */
  public static void shutdown() {
    TemplifyAPI.injector = null;
    TemplifyAPI.initialized = false;
  }

  /**
   * Gets a service instance from the Templify API.
   *
   * @param serviceClass the service class to retrieve
   * @param <T>          the service type
   * @return the service instance
   * @throws IllegalStateException if the API is not initialized
   */
  @NotNull
  public static <T> T getService(@NotNull Class<T> serviceClass) {
    Injector currentInjector = injector;
    if (currentInjector == null) {
      throw new IllegalStateException(
        "TemplifyAPI is not initialized. Make sure Templify module is loaded."
      );
    }

    return currentInjector.getInstance(serviceClass);
  }

  // ===== Convenience Methods for Common Services =====

  /**
   * Gets the PlaceholderRegistry for registering custom placeholder providers.
   *
   * @return the PlaceholderRegistry instance
   * @throws IllegalStateException if the API is not initialized
   */
  @NotNull
  public static PlaceholderRegistry getPlaceholderRegistry() {
    return getService(PlaceholderRegistry.class);
  }

  /**
   * Gets the ReplacementService for applying template replacements.
   *
   * @return the ReplacementService instance
   * @throws IllegalStateException if the API is not initialized
   */
  @NotNull
  public static ReplacementService getReplacementService() {
    return getService(ReplacementService.class);
  }

  /**
   * Gets the ReplacementOperationRegistry for registering custom replacement operations.
   *
   * @return the ReplacementOperationRegistry instance
   * @throws IllegalStateException if the API is not initialized
   */
  @NotNull
  public static ReplacementOperationRegistry getReplacementOperationRegistry() {
    return getService(ReplacementOperationRegistry.class);
  }
}

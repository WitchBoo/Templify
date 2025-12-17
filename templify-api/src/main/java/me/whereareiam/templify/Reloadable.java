package me.whereareiam.templify;

/**
 * Interface for components that support reloading their configuration or state.
 *
 * <p>Classes implementing this interface can be reloaded during runtime,
 * typically when the module's configuration changes or when a reload command
 * is executed. The reload operation should reset the component's state and
 * reload any configuration data.</p>
 */
public interface Reloadable {
  /**
   * Reloads the component's configuration and state.
   * This method is called when the module or specific components
   * need to be reinitialized during runtime.
   */
  void reload();
}
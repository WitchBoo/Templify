package me.whereareiam.templify;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import me.whereareiam.templify.common.CommonConfiguration;
import me.whereareiam.templify.common.listener.ServicePrepareListener;

public final class Templify extends DriverModule {
  private Injector injector;

  @ModuleTask(order = 100, lifecycle = ModuleLifeCycle.LOADED)
  public void onLoad() {
    this.refreshInjector();
  }

  @ModuleTask(order = 90, lifecycle = ModuleLifeCycle.LOADED)
  public void registerListeners(EventManager eventManager) {
    this.registerServicePrepareListener(eventManager);
  }

  @ModuleTask(order = 100, lifecycle = ModuleLifeCycle.RELOADING)
  public void onReload() {
    this.refreshInjector();
  }

  @ModuleTask(order = 90, lifecycle = ModuleLifeCycle.RELOADING)
  public void reloadListeners(EventManager eventManager) {
    eventManager.unregisterListeners(this.getClass().getClassLoader());
    this.registerServicePrepareListener(eventManager);
  }

  private void refreshInjector() {
    var dataDirectory = this.moduleWrapper().dataDirectory();
    this.injector = Guice.createInjector(
      new TemplifyConfiguration(),
      new CommonConfiguration(dataDirectory)
    );
  }

  private void registerServicePrepareListener(EventManager eventManager) {
    eventManager.registerListener(this.injector.getInstance(ServicePrepareListener.class));
  }
}

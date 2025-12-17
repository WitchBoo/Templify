package me.whereareiam.templify;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import me.whereareiam.templify.common.CommonConfiguration;
import me.whereareiam.templify.common.listener.ServicePrepareListener;

import java.util.Set;

public final class Templify extends DriverModule {
  private Injector injector;

  @ModuleTask(order = 100, lifecycle = ModuleLifeCycle.LOADED)
  public void onLoad(EventManager eventManager) {
    var dataDirectory = this.moduleWrapper().dataDirectory();
    this.injector = Guice.createInjector(
      new TemplifyConfiguration(),
      new CommonConfiguration(dataDirectory)
    );

    TemplifyAPI.initialize(this.injector);
    eventManager.registerListener(this.injector.getInstance(ServicePrepareListener.class));
  }

  @ModuleTask(order = 100, lifecycle = ModuleLifeCycle.RELOADING)
  public void onReload() {
    Set<Reloadable> reloadables = this.injector.getInstance(
      Key.get(new TypeLiteral<>() {}, Names.named("reloadables"))
    );

    for (Reloadable reloadable : reloadables)
      reloadable.reload();
  }

  @ModuleTask(order = 100, lifecycle = ModuleLifeCycle.STOPPED)
  public void onStop() {
    TemplifyAPI.shutdown();
  }
}

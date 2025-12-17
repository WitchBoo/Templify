package me.whereareiam.templify.common;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.ConfigurationTypeResolver;
import me.whereareiam.templify.Reloadable;
import me.whereareiam.templify.common.config.ConfiguraBootstrap;
import me.whereareiam.templify.common.config.provider.SettingsProvider;
import me.whereareiam.templify.common.config.provider.rules.RulesProvider;
import me.whereareiam.templify.common.config.resolver.FileSystemConfigurationTypeResolver;
import me.whereareiam.templify.common.placeholder.DefaultPlaceholderRegistry;
import me.whereareiam.templify.common.placeholder.type.NodeIdPlaceholderProvider;
import me.whereareiam.templify.common.placeholder.type.TaskNamePlaceholderProvider;
import me.whereareiam.templify.common.placeholder.type.environment.EnvironmentPlaceholderProvider;
import me.whereareiam.templify.common.placeholder.type.environment.EnvironmentVariablePlaceholderProvider;
import me.whereareiam.templify.common.placeholder.type.service.ServiceHostPlaceholderProvider;
import me.whereareiam.templify.common.placeholder.type.service.ServiceNamePlaceholderProvider;
import me.whereareiam.templify.common.placeholder.type.service.ServicePortPlaceholderProvider;
import me.whereareiam.templify.common.provider.ReloadableProvider;
import me.whereareiam.templify.common.replacement.operation.DefaultReplacementOperationRegistry;
import me.whereareiam.templify.common.replacement.operation.type.PlaceholderReplacementOperation;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.placeholder.PlaceholderRegistry;
import me.whereareiam.templify.replacement.ReplacementOperationRegistry;
import me.whereareiam.templify.replacement.ReplacementService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public final class CommonConfiguration extends AbstractModule {
  private final Path dataPath;

  @Override
  protected void configure() {
    bind(ReplacementOperationRegistry.class).to(DefaultReplacementOperationRegistry.class);
    bind(ReplacementService.class).to(TemplateReplacer.class);
    bind(PlaceholderRegistry.class).to(DefaultPlaceholderRegistry.class);

    // Configuration
    bind(ConfigurationTypeResolver.class)
        .to(FileSystemConfigurationTypeResolver.class)
        .asEagerSingleton();
    bind(ConfiguraBootstrap.class).asEagerSingleton();

    bind(Settings.class).toProvider(SettingsProvider.class);
    bind(new TypeLiteral<List<Replacement>>(){})
      .toProvider(RulesProvider.class);

    // Reloadable registry
    bind(ReloadableProvider.class).asEagerSingleton();
    bind(new TypeLiteral<Set<Reloadable>>(){})
      .annotatedWith(Names.named("reloadables")).toProvider(ReloadableProvider.class);

    requestInjection(this);
  }

  @Provides
  @Singleton
  @Named("dataPath")
  Path provideNamedDataPath() {
    return ensureDirectory(this.dataPath, "data");
  }

  @Provides
  @Singleton
  @Named("rulesPath")
  Path provideRulesPath() {
    return ensureDirectory(this.dataPath.resolve("rules"), "rules");
  }

  @Inject
  void initializePlaceholders(PlaceholderRegistry placeholderRegistry) {
    placeholderRegistry.register("nodeId", new NodeIdPlaceholderProvider());
    placeholderRegistry.register("serviceName", new ServiceNamePlaceholderProvider());
    placeholderRegistry.register("taskName", new TaskNamePlaceholderProvider());
    placeholderRegistry.register("serviceHost", new ServiceHostPlaceholderProvider());
    placeholderRegistry.register("servicePort", new ServicePortPlaceholderProvider());
    placeholderRegistry.register("environment", new EnvironmentPlaceholderProvider());

    // Register dynamic providers
    placeholderRegistry.register(new EnvironmentVariablePlaceholderProvider());
  }

  @Inject
  void initializeOperations(
          ReplacementOperationRegistry operationRegistry,
          PlaceholderReplacementOperation placeholderReplacementOperation
  ) {
    operationRegistry.register(placeholderReplacementOperation);
  }

  private Path ensureDirectory(Path path, String label) {
    try {
      Files.createDirectories(path);
      return path;
    } catch (IOException e) {
      throw new RuntimeException("Failed to create " + label + " directory", e);
    }
  }
}

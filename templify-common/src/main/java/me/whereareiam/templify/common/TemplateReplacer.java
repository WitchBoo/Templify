package me.whereareiam.templify.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.templify.replacement.ReplacementOperationRegistry;
import me.whereareiam.templify.replacement.ReplacementService;
import me.whereareiam.templify.common.files.FileSelector;
import me.whereareiam.templify.common.files.content.ContentReader;
import me.whereareiam.templify.common.files.content.ContentWriter;
import me.whereareiam.templify.model.ReplacementContext;
import me.whereareiam.templify.model.RulePlan;
import me.whereareiam.templify.common.match.RulePlanFactory;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.model.replacement.Replacement;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class TemplateReplacer implements ReplacementService {
  private final Provider<Settings> settingsProvider;
  private final FileSelector fileSelector;
  private final ContentReader contentReader;
  private final ContentWriter contentWriter;
  private final RulePlanFactory rulePlanFactory;
  private final ReplacementOperationRegistry operationRegistry;
  private final Provider<List<Replacement>> rulesProvider;

  @Override
  public void apply(@NonNull ServiceInfoSnapshot serviceInfo, @NonNull Path serviceDirectory, @Nullable String template) {
    var plan = this.rulePlanFactory.create(rulesProvider.get(), serviceInfo, template);
    if (plan.getFileMatchers().isEmpty()) return;

    try {
      var files = this.fileSelector.findFiles(serviceDirectory, plan.getFileMatchers());
      files.forEach(path -> this.applyToFile(path, serviceInfo, plan));
    } catch (IOException exception) {
      log.warn("Failed to apply template replacements in directory {}", serviceDirectory, exception);
    }
  }

  private void applyToFile(Path path, ServiceInfoSnapshot serviceInfo, RulePlan plan) {
    if (!this.withinSizeLimit(path)) return;

    var content = this.contentReader.read(path);
    if (content == null) return;

    String updated = content;
    for (var operation : this.operationRegistry.getOperations()) {
      var context = new ReplacementContext(path, updated, serviceInfo, plan);
      updated = operation.apply(context);
    }

    this.contentWriter.writeIfChanged(path, content, updated);
  }

  private boolean withinSizeLimit(Path path) {
    var limitSection = settingsProvider.get().getLimits();
    long limit = limitSection == null ? 0L : limitSection.getMaxFileSizeBytes();
    if (limit <= 0L) return true;

    try {
      return Files.size(path) <= limit;
    } catch (IOException exception) {
      return false;
    }
  }
}
package me.whereareiam.templify.common.match;

import com.google.inject.Singleton;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.model.TargetDefinition;
import me.whereareiam.templify.model.config.Settings;
import org.jetbrains.annotations.Nullable;

@Singleton
public final class RuleMatcher {
  public List<Replacement> matchingRules(
          @NonNull List<Replacement> rules,
          @NonNull ServiceInfoSnapshot serviceInfo,
          @Nullable String template
  ) {
    return rules.stream()
            .filter(this::isRuleEnabled)
            .filter(rule -> this.matches(rule, serviceInfo, template))
            .toList();
  }

  public List<String> collectGlobs(@NonNull Settings configuration, @NonNull List<Replacement> matchingRules) {
    var globs = new ArrayList<String>();
    if (configuration.getPaths() != null)
      globs.addAll(this.nonNullList(configuration.getPaths().getFilePatterns()));

    for (var rule : matchingRules)
      if (rule.getFiles() != null && !rule.getFiles().isEmpty())
        globs.addAll(rule.getFiles());

    return globs.stream().distinct().toList();
  }

  public List<PathMatcher> resolveGlobs(@NonNull Replacement rule, @NonNull Settings configuration) {
    var globs = rule.getFiles();
    if (globs == null || globs.isEmpty())
      globs = configuration.getPaths() != null ? configuration.getPaths().getFilePatterns() : List.of();

    return this.nonNullList(globs).stream()
            .map(glob -> FileSystems.getDefault().getPathMatcher("glob:**/" + glob))
            .collect(Collectors.toList());
  }

  public List<PathMatcher> toPathMatchers(@NonNull List<String> globs) {
    return globs.stream()
            .map(glob -> FileSystems.getDefault().getPathMatcher("glob:**/" + glob))
            .toList();
  }

  public boolean matchesAny(@NonNull Path path, @NonNull List<PathMatcher> matchers) {
    for (var matcher : matchers)
      if (matcher.matches(path))
        return true;

    return false;
  }

  private boolean matches(Replacement rule, ServiceInfoSnapshot serviceInfo, @Nullable String template) {
    List<TargetDefinition> targets = rule.getTargets();

    if (targets == null || targets.isEmpty()) return true;
    for (TargetDefinition target : targets)
      if (this.matchesTarget(target, serviceInfo, template))
        return true;

    return false;
  }

  private boolean matchesTarget(TargetDefinition target, ServiceInfoSnapshot serviceInfo, @Nullable String template) {
    if (target == null) return true;
    if (target.getTask() != null && !Objects.equals(target.getTask(), serviceInfo.serviceId().taskName()))
      return false;

    if (target.getService() != null && !Objects.equals(target.getService(), serviceInfo.serviceId().name()))
      return false;

    if (target.getEnvironment() != null
            && !this.environmentMatches(target.getEnvironment(), serviceInfo.serviceId().environmentName())) {
      return false;
    }

    if (target.getGroup() != null && !this.hasGroup(serviceInfo, target.getGroup()))
      return false;

    return target.getTemplate() == null || this.matchesTemplate(target.getTemplate(), serviceInfo, template);
  }

  private boolean hasGroup(ServiceInfoSnapshot serviceInfo, String desiredGroup) {
    var configuration = serviceInfo.configuration();
    if (configuration.groups().isEmpty())
      return false;

    for (var group : configuration.groups())
      if (Objects.equals(group, desiredGroup))
        return true;

    return false;
  }

  private boolean isRuleEnabled(Replacement rule) {
    return rule.getEnabled() == null || rule.getEnabled();
  }

  private boolean matchesTemplate(String targetTemplate, ServiceInfoSnapshot serviceInfo, @Nullable String template) {
    if (template != null)
      return Objects.equals(targetTemplate, template);

    var templates = serviceInfo.configuration().templates();
    return templates.stream().anyMatch(entry -> Objects.equals(entry.name(), targetTemplate));
  }

  private boolean environmentMatches(String desired, @Nullable String actual) {
    if (desired == null)
      return true;

    if (actual == null)
      return false;

    return desired.equalsIgnoreCase(actual);
  }

  private List<String> nonNullList(@Nullable List<String> input) {
    return input == null ? List.of() : input;
  }
}
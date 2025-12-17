package me.whereareiam.templify.common.match;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.model.RulePlan;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.model.replacement.Replacement;

/**
 * Factory for creating {@link me.whereareiam.templify.model.RulePlan} instances.
 *
 * <p>Contains all rule planning logic: matching rules, computing globs,
 * and building path matchers.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class RulePlanFactory {
  private final Settings settings;
  private final RuleMatcher ruleMatcher;

  public RulePlan create(
    List<Replacement> rules,
    ServiceInfoSnapshot serviceInfo,
    String template
  ) {
    var matchingRules = this.ruleMatcher.matchingRules(rules, serviceInfo, template);
    var allGlobs = this.ruleMatcher.collectGlobs(settings, matchingRules);

    List<PathMatcher> pathMatchers = this.ruleMatcher.toPathMatchers(allGlobs);
    Map<Replacement, List<PathMatcher>> rulePathMatchers = matchingRules.stream()
      .collect(Collectors.toMap(
        Function.identity(),
        rule -> this.ruleMatcher.resolveGlobs(rule, settings)
      ));

    return new RulePlan(
      matchingRules,
      rulePathMatchers,
      pathMatchers
    );
  }
}


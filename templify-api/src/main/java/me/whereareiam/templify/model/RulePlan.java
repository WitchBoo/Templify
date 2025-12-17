package me.whereareiam.templify.model;

import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.model.replacement.Replacement;

/**
 * Immutable description of how and where to apply replacement rules for a single run.
 */
@Getter
@RequiredArgsConstructor
public final class RulePlan {
  private final List<Replacement> matchingRules;
  private final Map<Replacement, List<PathMatcher>> rulePathMatchers;
  private final List<PathMatcher> fileMatchers;
}
package me.whereareiam.templify.common.replacement.operation.type;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.common.match.RuleMatcher;
import me.whereareiam.templify.common.placeholder.RulePlaceholderApplier;
import me.whereareiam.templify.model.ReplacementContext;
import me.whereareiam.templify.model.RulePlan;
import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.PlaceholderRegistry;
import me.whereareiam.templify.replacement.ReplacementOperation;

/**
 * Default replacement operation that applies the placeholder replacement rules
 * defined in the current {@link RulePlan}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class PlaceholderReplacementOperation implements ReplacementOperation {
  private final RuleMatcher ruleMatcher;
  private final RulePlaceholderApplier rulePlaceholderApplier;
  private final PlaceholderRegistry placeholderRegistry;

  @Override
  public String apply(ReplacementContext context) {
    var file = context.getFile();
    var plan = context.getRulePlan();
    var placeholderContext = new PlaceholderContext(file, context.getServiceInfo());

    var updated = context.getContent();

    var placeholders = this.placeholderRegistry.getAll(placeholderContext, true);
    if (!placeholders.isEmpty()) {
      updated = this.rulePlaceholderApplier.applyBuiltIns(updated, placeholders, placeholderContext);
    }

    for (var rule : plan.getMatchingRules()) {
      var matchers = plan.getRulePathMatchers().get(rule);
      if (matchers != null && !matchers.isEmpty() && !this.ruleMatcher.matchesAny(file, matchers)) {
        continue;
      }

      updated = this.rulePlaceholderApplier.applyRule(updated, rule, placeholderContext);
    }

    return updated;
  }
}

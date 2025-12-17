package me.whereareiam.templify.common.replacement.selector.type;

import me.whereareiam.templify.common.replacement.selector.base.ValueSelector;
import me.whereareiam.templify.model.condition.ConditionRule;
import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.PlaceholderRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public final class ConditionalValueSelector implements ValueSelector {
  private final String value;

  public ConditionalValueSelector(
    @NotNull List<ConditionRule> conditions,
    @NotNull PlaceholderRegistry registry,
    @NotNull PlaceholderContext context
  ) {
    this.value = this.evaluateConditions(conditions, registry, context);
  }

  @Override
  public String nextValue(int occurrence) {
    return value;
  }

  private String evaluateConditions(
    List<ConditionRule> conditions,
    PlaceholderRegistry registry,
    PlaceholderContext context
  ) {
    if (conditions == null || conditions.isEmpty())
      return null;

    for (var condition : conditions) {
      var when = condition.getWhen();
      if (when.getField() == null) continue;

      var actualValue = registry.getValue(when.getField(), context);
      if (actualValue == null) continue;

      if (when.getEquals() != null && actualValue.equals(when.getEquals()))
        return condition.getValue();

      if (when.getRegex() != null && Pattern.compile(when.getRegex()).matcher(actualValue).matches())
        return condition.getValue();
    }

    return null;
  }
}

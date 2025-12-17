package me.whereareiam.templify.common.replacement.selector.base;

import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.common.replacement.selector.type.ConditionalValueSelector;
import me.whereareiam.templify.common.replacement.selector.type.FirstValueSelector;
import me.whereareiam.templify.common.replacement.selector.type.RandomValueSelector;
import me.whereareiam.templify.common.replacement.selector.type.SequentialValueSelector;
import me.whereareiam.templify.model.PlaceholderReplacement;
import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.PlaceholderRegistry;
import me.whereareiam.templify.type.ReplaceType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public final class ValueSelectorFactory {
  private final PlaceholderRegistry registry;

  public @Nullable ValueSelector selector(
    ReplaceType replaceType,
    PlaceholderReplacement replacement,
    PlaceholderContext context
  ) {
    var values = replacement.getValues() == null ? List.<String>of() : replacement.getValues();
    return switch (replaceType) {
      case FIRST -> values.isEmpty() ? null : new FirstValueSelector(values);
      case RANDOM -> values.isEmpty() ? null : new RandomValueSelector(values);
      case SEQUENTIAL -> values.isEmpty() ? null : new SequentialValueSelector(values);
      case CONDITIONAL -> {
        if (replacement.getConditions() == null || replacement.getConditions().isEmpty())
          yield null;

        var selector = new ConditionalValueSelector(replacement.getConditions(), this.registry, context);
        yield selector.nextValue(0) != null ? selector : null;
      }
    };
  }
}



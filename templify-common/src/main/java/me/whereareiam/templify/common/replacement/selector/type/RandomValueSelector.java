package me.whereareiam.templify.common.replacement.selector.type;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import me.whereareiam.templify.common.replacement.selector.base.ValueSelector;
import org.jetbrains.annotations.Nullable;

public final class RandomValueSelector implements ValueSelector {
  private final List<String> values;
  private final int selectedIndex;

  public RandomValueSelector(List<String> values) {
    this.values = values;
    this.selectedIndex = values.isEmpty() ? -1 : ThreadLocalRandom.current().nextInt(values.size());
  }

  @Override
  public @Nullable String nextValue(int occurrence) {
    if (values.isEmpty() || selectedIndex < 0) {
      return null;
    }
    return values.get(selectedIndex);
  }
}

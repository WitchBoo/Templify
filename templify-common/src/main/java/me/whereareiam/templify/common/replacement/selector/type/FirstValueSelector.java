package me.whereareiam.templify.common.replacement.selector.type;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.common.replacement.selector.base.ValueSelector;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class FirstValueSelector implements ValueSelector {
  private final List<String> values;

  @Override
  public @Nullable String nextValue(int occurrence) {
    if (values.isEmpty()) {
      return null;
    }
    return values.getFirst();
  }
}

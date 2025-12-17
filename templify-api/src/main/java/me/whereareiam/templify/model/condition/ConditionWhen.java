package me.whereareiam.templify.model.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class ConditionWhen {
  @Nullable
  private final String field;

  @Nullable
  private final String equals;

  @Nullable
  private final String regex;
}


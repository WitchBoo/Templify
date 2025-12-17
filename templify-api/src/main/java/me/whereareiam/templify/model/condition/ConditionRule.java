package me.whereareiam.templify.model.condition;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ConditionRule {
  @NonNull
  private final ConditionWhen when;

  @NonNull
  private final String value;
}


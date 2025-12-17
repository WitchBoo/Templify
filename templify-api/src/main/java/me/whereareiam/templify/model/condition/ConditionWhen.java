package me.whereareiam.templify.model.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class ConditionWhen {
  @Nullable
  private String field;

  @Nullable
  private String equals;

  @Nullable
  private String regex;
}


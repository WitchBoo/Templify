package me.whereareiam.templify.model.condition;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class ConditionRule {
  @NotNull
  private ConditionWhen when;

  @NotNull
  private String value;
}


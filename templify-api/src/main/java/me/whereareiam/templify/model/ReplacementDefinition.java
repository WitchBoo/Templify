package me.whereareiam.templify.model;

import lombok.*;
import me.whereareiam.templify.model.condition.ConditionRule;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class ReplacementDefinition {
  @NotNull
  private String token;

  @Nullable
  private SearchType searchType;

  @Nullable
  private ReplaceType replaceType;

  @Nullable
  private List<String> values;

  @Nullable
  private List<ConditionRule> conditions;
}


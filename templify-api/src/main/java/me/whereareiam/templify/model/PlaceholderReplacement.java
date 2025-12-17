package me.whereareiam.templify.model;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.model.condition.ConditionRule;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class PlaceholderReplacement {
  @NonNull
  private final String token;

  @Nullable
  private final SearchType searchType;

  @Nullable
  private final ReplaceType replaceType;

  @Nullable
  private final List<String> values;

  @Nullable
  private final List<ConditionRule> conditions;
}


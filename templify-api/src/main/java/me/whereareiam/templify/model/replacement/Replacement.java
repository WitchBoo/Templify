package me.whereareiam.templify.model.replacement;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.model.PlaceholderReplacement;
import me.whereareiam.templify.model.TargetDefinition;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class Replacement {
  @Nullable
  private final String id;

  @Nullable
  private final Boolean enabled;

  @Nullable
  private final List<TargetDefinition> targets;

  @Nullable
  private final List<String> files;

  @Nullable
  private final List<PlaceholderReplacement> placeholders;
}


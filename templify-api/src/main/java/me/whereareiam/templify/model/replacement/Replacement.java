package me.whereareiam.templify.model.replacement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.whereareiam.templify.model.ReplacementDefinition;
import me.whereareiam.templify.model.TargetDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class Replacement {
  @Nullable
  private String id;

  @Nullable
  private Boolean enabled;

  @Nullable
  private List<TargetDefinition> targets;

  @Nullable
  private List<String> files;

  @Nullable
  private List<ReplacementDefinition> definitions;
}


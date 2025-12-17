package me.whereareiam.templify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class TargetDefinition {
  @Nullable
  private String task;

  @Nullable
  private String service;

  @Nullable
  private String environment;

  @Nullable
  private String group;

  @Nullable
  private String template;
}


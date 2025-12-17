package me.whereareiam.templify.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class TargetDefinition {
  @Nullable
  private final String task;

  @Nullable
  private final String service;

  @Nullable
  private final String environment;

  @Nullable
  private final String group;

  @Nullable
  private final String template;
}


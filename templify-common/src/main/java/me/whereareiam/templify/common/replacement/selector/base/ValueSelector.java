package me.whereareiam.templify.common.replacement.selector.base;

import org.jetbrains.annotations.Nullable;

public interface ValueSelector {
  @Nullable String nextValue(int occurrence);
}
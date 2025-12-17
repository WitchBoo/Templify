package me.whereareiam.templify.model.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.whereareiam.templify.model.replacement.Replacement;
import org.jetbrains.annotations.Nullable;

/**
 * Container model for a replacement rule file.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public final class Replacements {
  @Nullable
  private List<Replacement> rules;
}


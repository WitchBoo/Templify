package me.whereareiam.templify.replacement;

import me.whereareiam.templify.model.ReplacementContext;

/**
 * A single operation in the replacement pipeline.
 *
 * <p>Implementations of this interface can be registered to extend the replacement
 * capabilities of Templify. Operations are applied in order to transform file content.
 */
public interface ReplacementOperation {
  /**
   * Apply this operation to the given context.
   *
   * @param context the replacement context containing file, content, and service info
   * @return the updated content (may be the same instance or a new one)
   */
  String apply(ReplacementContext context);
}


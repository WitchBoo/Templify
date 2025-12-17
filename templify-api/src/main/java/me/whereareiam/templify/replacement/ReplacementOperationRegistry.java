package me.whereareiam.templify.replacement;

import java.util.List;
import lombok.NonNull;

/**
 * Registry for {@link ReplacementOperation} instances.
 *
 * <p>Allows API users to register custom replacement operations
 * that will be applied during the replacement process.
 */
public interface ReplacementOperationRegistry {
  /**
   * Registers a replacement operation.
   *
   * @param operation the operation to register
   */
  void register(@NonNull ReplacementOperation operation);

  /**
   * Registers a replacement operation with a specific priority.
   * Higher priority operations are executed first.
   *
   * @param operation the operation to register
   * @param priority the priority (higher = executed first)
   */
  void register(@NonNull ReplacementOperation operation, int priority);

  /**
   * Returns all registered operations in priority order.
   *
   * @return list of registered operations
   */
  @NonNull
  List<ReplacementOperation> getOperations();
}

package me.whereareiam.templify.model;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import java.nio.file.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.replacement.ReplacementOperation;

/**
 * Contextual information available to {@link ReplacementOperation} implementations.
 *
 * <p>Contains all data needed for a single file replacement operation.
 */
@Getter
@RequiredArgsConstructor
public class ReplacementContext {
  private final Path file;
  private final String content;
  private final ServiceInfoSnapshot serviceInfo;
  private final RulePlan rulePlan;
}
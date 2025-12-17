package me.whereareiam.templify.common.config.provider.rules.example;

import java.util.List;
import me.whereareiam.templify.model.PlaceholderReplacement;
import me.whereareiam.templify.model.TargetDefinition;
import me.whereareiam.templify.model.condition.ConditionRule;
import me.whereareiam.templify.model.condition.ConditionWhen;
import me.whereareiam.templify.model.config.Replacements;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;

/**
 * Example demonstrating conditional replacements based on task name
 * with target filtering for specific environments.
 */
public final class ConditionalRulesExample implements RulesExample {
  @Override
  public String fileName() {
    return "example-conditional";
  }

  @Override
  public Replacements create() {
    return Replacements.builder()
      .rules(List.of(
        // Server performance settings - only for Minecraft servers
        new Replacement(
          "server-performance-settings",
          true,
          List.of(new TargetDefinition(null, null, "MINECRAFT_SERVER", null, null)),
          List.of("server.properties", "spigot.yml"),
          List.of(
            // max-players based on task name
            new PlaceholderReplacement(
              "%max_players%",
              SearchType.ALL,
              ReplaceType.CONDITIONAL,
              null,
              List.of(
                new ConditionRule(new ConditionWhen("task", "Lobby", null), "200"),
                new ConditionRule(new ConditionWhen("task", "Survival", null), "50"),
                new ConditionRule(new ConditionWhen("task", "BedWars", null), "16"),
                new ConditionRule(new ConditionWhen(null, null, null), "20") // default
              )
            ),
            // view-distance based on task name
            new PlaceholderReplacement(
              "%view_distance%",
              SearchType.ALL,
              ReplaceType.CONDITIONAL,
              null,
              List.of(
                new ConditionRule(new ConditionWhen("task", "Lobby", null), "8"),
                new ConditionRule(new ConditionWhen("task", "Survival", null), "12"),
                new ConditionRule(new ConditionWhen("task", "BedWars", null), "6"),
                new ConditionRule(new ConditionWhen(null, null, null), "10") // default
              )
            )
          )
        )
      ))
      .build();
  }
}

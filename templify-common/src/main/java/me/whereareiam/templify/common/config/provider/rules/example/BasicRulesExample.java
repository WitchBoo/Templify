package me.whereareiam.templify.common.config.provider.rules.example;

import java.util.List;
import me.whereareiam.templify.model.PlaceholderReplacement;
import me.whereareiam.templify.model.config.Replacements;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;

/**
 * Basic example demonstrating environment-based placeholder replacements
 * for database credentials and proxy forwarding secrets.
 */
public final class BasicRulesExample implements RulesExample {
  @Override
  public String fileName() {
    return "example-basic";
  }

  @Override
  public Replacements create() {
    return Replacements.builder()
      .rules(List.of(
        // Database credentials from environment variables
        new Replacement(
          "database-credentials",
          true,
          null,
          List.of("plugins/*/config.yml", "plugins/*/database.yml"),
          List.of(
            new PlaceholderReplacement(
              "%database_host%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_HOST%"),
              null
            ),
            new PlaceholderReplacement(
              "%database_port%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_PORT%"),
              null
            ),
            new PlaceholderReplacement(
              "%database_name%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_NAME%"),
              null
            ),
            new PlaceholderReplacement(
              "%database_user%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_USER%"),
              null
            ),
            new PlaceholderReplacement(
              "%database_password%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_PASSWORD%"),
              null
            )
          )
        ),
        // Velocity forwarding secret
        new Replacement(
          "velocity-forwarding-secret",
          true,
          null,
          List.of("config/paper-global.yml", "velocity.toml"),
          List.of(
            new PlaceholderReplacement(
              "%forwarding_secret%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("your-secret-key-here"),
              null
            )
          )
        )
      ))
      .build();
  }
}

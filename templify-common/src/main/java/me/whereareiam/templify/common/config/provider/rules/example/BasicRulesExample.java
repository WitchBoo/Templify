package me.whereareiam.templify.common.config.provider.rules.example;

import java.util.List;
import me.whereareiam.templify.model.ReplacementDefinition;
import me.whereareiam.templify.model.TargetDefinition;
import me.whereareiam.templify.model.config.Replacements;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;

/**
 * Basic example demonstrating environment-based placeholder replacements
 * for database credentials and proxy forwarding secrets with target filtering.
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
        // Database credentials - only for Minecraft servers (plugins folder exists there)
        new Replacement(
          "database-credentials",
          true,
          List.of(new TargetDefinition(null, null, "MINECRAFT_SERVER", null, null)),
          List.of("plugins/*/config.yml", "plugins/*/database.yml"),
          List.of(
            new ReplacementDefinition(
              "%database_host%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_HOST%"),
              null
            ),
            new ReplacementDefinition(
              "%database_port%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_PORT%"),
              null
            ),
            new ReplacementDefinition(
              "%database_name%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_NAME%"),
              null
            ),
            new ReplacementDefinition(
              "%database_user%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_USER%"),
              null
            ),
            new ReplacementDefinition(
              "%database_password%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("%env:DATABASE_PASSWORD%"),
              null
            )
          )
        ),
        // Paper forwarding secret - only for Minecraft servers
        new Replacement(
          "paper-forwarding-secret",
          true,
          List.of(new TargetDefinition(null, null, "MINECRAFT_SERVER", null, null)),
          List.of("config/paper-global.yml"),
          List.of(
            new ReplacementDefinition(
              "%forwarding_secret%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("your-secret-key-here"),
              null
            )
          )
        ),
        // Velocity forwarding secret - only for proxies
        new Replacement(
          "velocity-forwarding-secret",
          true,
          List.of(new TargetDefinition(null, null, "VELOCITY", null, null)),
          List.of("velocity.toml"),
          List.of(
            new ReplacementDefinition(
              "%forwarding_secret%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("your-secret-key-here"),
              null
            )
          )
        ),
        // BungeeCord forwarding secret - only for BungeeCord proxies
        new Replacement(
          "bungeecord-forwarding-secret",
          true,
          List.of(new TargetDefinition(null, null, "BUNGEECORD", null, null)),
          List.of("config.yml"),
          List.of(
            new ReplacementDefinition(
              "%forwarding_secret%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("your-secret-key-here"),
              null
            )
          )
        ),
        // Task-specific config - only for Lobby task
        new Replacement(
          "lobby-specific-config",
          true,
          List.of(new TargetDefinition("Lobby", null, "MINECRAFT_SERVER", null, null)),
          List.of("plugins/LobbyPlugin/config.yml"),
          List.of(
            new ReplacementDefinition(
              "%lobby_mode%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("hub"),
              null
            )
          )
        ),
        // Group-based configuration - all servers in "minigames" group
        new Replacement(
          "minigames-group-config",
          true,
          List.of(new TargetDefinition(null, null, null, "minigames", null)),
          List.of("spigot.yml"),
          List.of(
            new ReplacementDefinition(
              "%entity_activation_range%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("16"),
              null
            )
          )
        ),
        // Service-specific configuration - target a specific running service
        new Replacement(
          "service-specific-config",
          true,
          List.of(new TargetDefinition(null, "Lobby-1", null, null, null)),
          List.of("plugins/*/config.yml"),
          List.of(
            new ReplacementDefinition(
              "%is_main_lobby%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("true"),
              null
            )
          )
        ),
        // Template-based configuration
        new Replacement(
          "template-specific-config",
          true,
          List.of(new TargetDefinition(null, null, null, null, "default")),
          List.of("server.properties"),
          List.of(
            new ReplacementDefinition(
              "%motd%",
              SearchType.ALL,
              ReplaceType.FIRST,
              List.of("Default Server Template"),
              null
            )
          )
        )
      ))
      .build();
  }
}

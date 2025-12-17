# Rules

Rules define what files to process and what replacements to apply. Each rule file in the `rules/` directory contains one
or more replacement rules.

## File Organization

Rules are loaded from the `rules/` directory. You can organize them however you like:

```
rules/
├── database.json           # Single file
├── proxy/
│   ├── velocity.json       # Subfolder for proxy rules
│   └── bungeecord.json
├── minecraft/
│   ├── lobby.json          # Subfolder for game server rules
│   └── survival.json
└── common.json             # Shared rules
```

> All rule files in `rules/` and its subfolders are automatically loaded. JSON is the default format, but you can switch
> to YAML.

## Structure

```json
{
  "rules": [
    {
      "id": "my-rule",
      "enabled": true,
      "targets": [
        ...
      ],
      "files": [
        ...
      ],
      "definitions": [
        ...
      ]
    }
  ]
}
```

## Fields

### `id` (optional)

A unique identifier for the rule. Used in logs and debugging to identify which rule was applied.

```json
{
  "id": "database-credentials"
}
```

If omitted, the rule will still work but will be harder to trace in logs.

---

### `enabled` (optional)

Controls whether the rule is active. Defaults to `true` if not specified.

```json
{
  "enabled": false
}
```

Useful for temporarily disabling a rule without deleting it.

---

### `targets` (optional)

Filters which services this rule applies to. If omitted, the rule applies to all services.

```json
{
  "targets": [
    {
      "task": "Lobby"
    },
    {
      "environment": "MINECRAFT_SERVER"
    }
  ]
}
```

See [Targets](TARGETS.md) for all filtering options.

---

### `files` (optional)

Glob patterns specifying which files to process within the service template.

```json
{
  "files": [
    "plugins/*/config.yml",
    "server.properties"
  ]
}
```

| Pattern                 | Matches                                     |
|-------------------------|---------------------------------------------|
| `*`                     | Any single path segment                     |
| `**`                    | Any number of path segments                 |
| `*.yml`                 | All `.yml` files in root                    |
| `plugins/*/config.yml`  | `config.yml` in any direct plugin subfolder |
| `plugins/**/config.yml` | `config.yml` in any nested plugin subfolder |

---

### `definitions`

The actual replacements to perform. Each definition specifies what to search for and what to replace it with.

```json
{
  "definitions": [
    {
      "token": "%database_host%",
      "searchType": "ALL",
      "replaceType": "FIRST",
      "values": [
        "%env:DATABASE_HOST%"
      ]
    }
  ]
}
```

See [Definitions](DEFINITIONS.md) for all options.

---

## Examples

### Basic: Environment Variable Injection

Replace database placeholders with environment variables in all plugin configs.

**Rule:**

```json
{
  "rules": [
    {
      "id": "database-credentials",
      "files": [
        "plugins/*/config.yml"
      ],
      "definitions": [
        {
          "token": "%db_host%",
          "values": [
            "%env:DATABASE_HOST%"
          ]
        },
        {
          "token": "%db_password%",
          "values": [
            "%env:DATABASE_PASSWORD%"
          ]
        }
      ]
    }
  ]
}
```

**Before (`plugins/MyPlugin/config.yml`):**

```yaml
database:
  host: "%db_host%"
  password: "%db_password%"
```

**After (assuming `DATABASE_HOST=10.0.0.5` and `DATABASE_PASSWORD=secret123`):**

```yaml
database:
  host: "10.0.0.5"
  password: "secret123"
```

---

### Targeted: Proxy-Specific Configuration

Apply forwarding secret only to Velocity proxies.

**Rule:**

```json
{
  "rules": [
    {
      "id": "velocity-forwarding",
      "targets": [
        {
          "environment": "VELOCITY"
        }
      ],
      "files": [
        "forwarding.secret"
      ],
      "definitions": [
        {
          "token": "%forwarding_secret%",
          "values": [
            "%env:FORWARDING_SECRET%"
          ]
        }
      ]
    }
  ]
}
```

This rule will only run on services with environment `VELOCITY`, leaving other services untouched.

---

### Conditional: Task-Based Values

Set different max players based on which task the server belongs to.

**Rule:**

```json
{
  "rules": [
    {
      "id": "max-players",
      "targets": [
        {
          "environment": "MINECRAFT_SERVER"
        }
      ],
      "files": [
        "server.properties"
      ],
      "definitions": [
        {
          "token": "%max_players%",
          "replaceType": "CONDITIONAL",
          "conditions": [
            {
              "when": {
                "field": "%taskName%",
                "equals": "Lobby"
              },
              "value": "200"
            },
            {
              "when": {
                "field": "%taskName%",
                "equals": "BedWars"
              },
              "value": "16"
            },
            {
              "when": {
                "field": "%taskName%",
                "equals": "Survival"
              },
              "value": "50"
            }
          ]
        }
      ]
    }
  ]
}
```

**Before (`server.properties`):**

```properties
max-players=%max_players%
```

**After (on a Lobby server):**

```properties
max-players=200
```

**After (on a BedWars server):**

```properties
max-players=16
```

---

### Multiple Targets: Group and Task

Apply a rule only to servers that match specific criteria.

**Rule:**

```json
{
  "rules": [
    {
      "id": "minigames-optimization",
      "targets": [
        {
          "task": "BedWars"
        },
        {
          "task": "SkyWars"
        },
        {
          "group": "minigames"
        }
      ],
      "files": [
        "spigot.yml"
      ],
      "definitions": [
        {
          "token": "%entity_range%",
          "values": [
            "16"
          ]
        }
      ]
    }
  ]
}
```

This rule applies to:

- Any service with task `BedWars`
- Any service with task `SkyWars`
- Any service in the `minigames` group


# Targets

Targets filter which services a rule applies to. When a service starts, Templify checks each rule's targets to determine
if it should be processed.

## How Matching Works

- If a rule has **no targets**, it applies to **all services**
- If a rule has **multiple targets**, the rule applies if **any** target matches (OR logic)
- Within a single target, **all specified fields** must match (AND logic)

## Fields

| Field         | Type   | Description                            | Example                            |
|---------------|--------|----------------------------------------|------------------------------------|
| `task`        | string | CloudNet task name                     | `"Lobby"`, `"BedWars"`             |
| `service`     | string | Full service name                      | `"Lobby-1"`, `"BedWars-3"`         |
| `environment` | string | Service environment (case-insensitive) | `"MINECRAFT_SERVER"`, `"VELOCITY"` |
| `group`       | string | CloudNet group name                    | `"minigames"`, `"proxies"`         |
| `template`    | string | Template name                          | `"default"`, `"lobby"`             |

---

## Examples

### Single Field: Task

Apply only to Lobby servers:

```json
{
  "targets": [
    {
      "task": "Lobby"
    }
  ]
}
```

---

### Single Field: Environment

Apply to all Minecraft servers:

```json
{
  "targets": [
    {
      "environment": "MINECRAFT_SERVER"
    }
  ]
}
```

Common environments: `MINECRAFT_SERVER`, `VELOCITY`, `BUNGEECORD`, `WATERDOG_PE`

---

### Single Field: Specific Service

Apply only to a specific service instance:

```json
{
  "targets": [
    {
      "service": "Lobby-1"
    }
  ]
}
```

---

### Single Field: Group

Apply to all services in a group:

```json
{
  "targets": [
    {
      "group": "minigames"
    }
  ]
}
```

---

### Single Field: Template

Apply to services using a specific template:

```json
{
  "targets": [
    {
      "template": "default"
    }
  ]
}
```

---

### Multiple Fields (AND)

Apply only to Lobby servers that are Minecraft servers:

```json
{
  "targets": [
    {
      "task": "Lobby",
      "environment": "MINECRAFT_SERVER"
    }
  ]
}
```

Both conditions must be true for the rule to apply.

---

### Multiple Targets (OR)

Apply to either BedWars or SkyWars tasks:

```json
{
  "targets": [
    {
      "task": "BedWars"
    },
    {
      "task": "SkyWars"
    }
  ]
}
```

The rule applies if the service matches **any** of the targets.

---

### Combined: Multiple Targets with Multiple Fields

Apply to minigames (BedWars, SkyWars) or any server in the "minigames" group:

```json
{
  "targets": [
    {
      "task": "BedWars",
      "environment": "MINECRAFT_SERVER"
    },
    {
      "task": "SkyWars",
      "environment": "MINECRAFT_SERVER"
    },
    {
      "group": "minigames"
    }
  ]
}
```

---

## No Targets (Global Rule)

Omit the `targets` field to apply the rule to all services:

```json
{
  "rules": [
    {
      "id": "global-config",
      "files": [
        "server.properties"
      ],
      "definitions": [
        ...
      ]
    }
  ]
}
```

---

## Matching Logic Summary

| Scenario                               | Result                  |
|----------------------------------------|-------------------------|
| No targets defined                     | Applies to all services |
| Single target, all fields match        | Applies                 |
| Single target, any field doesn't match | Does not apply          |
| Multiple targets, at least one matches | Applies                 |
| Multiple targets, none match           | Does not apply          |

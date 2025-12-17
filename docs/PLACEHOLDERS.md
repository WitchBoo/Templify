# Placeholders

Built-in placeholders that can be used in two ways:

1. **As replacement values** — Use them in definition values to inject dynamic content
2. **In conditions** — Reference them in condition rules to control which replacement applies

## Static Placeholders

Fixed-key placeholders that resolve to service information at processing time.

| Placeholder     | Description                        | Example Value      |
|-----------------|------------------------------------|--------------------|
| `%taskName%`    | Name of the task                   | `Lobby`            |
| `%serviceName%` | Full name of the service           | `Lobby-1`          |
| `%serviceHost%` | Host address of the service        | `127.0.0.1`        |
| `%servicePort%` | Port the service listens on        | `25565`            |
| `%environment%` | Service environment type           | `MINECRAFT_SERVER` |
| `%nodeId%`      | ID of the node running the service | `Node-1`           |

## Dynamic Placeholders

Placeholders that accept a parameter to determine their value.

| Pattern          | Description                 | Example                             |
|------------------|-----------------------------|-------------------------------------|
| `%env:VAR_NAME%` | System environment variable | `%env:DATABASE_HOST%` → `localhost` |

## Usage Examples

**In replacement values:**

```json
{
  "values": [
    "%env:DATABASE_PASSWORD%",
    "%serviceName%"
  ]
}
```

**In conditions:**

```json
{
  "conditions": [
    {
      "when": {
        "field": "%taskName%",
        "equals": "Lobby"
      },
      "value": "200"
    }
  ]
}
```

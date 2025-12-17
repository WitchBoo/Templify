# Definitions

Definitions specify what to search for in your files and how to replace them. Each definition contains a token to find
and either static values or conditional rules for replacement.

## Structure

```json
{
  "token": "%placeholder%",
  "searchType": "ALL",
  "replaceType": "FIRST",
  "values": [
    "replacement-value"
  ]
}
```

## Fields

| Field         | Type   | Required | Description                      |
|---------------|--------|----------|----------------------------------|
| `token`       | string | Yes      | The text to search for in files  |
| `searchType`  | string | No       | How many occurrences to find     |
| `replaceType` | string | No       | How to select replacement values |

| `values`     | array | No*      | List of replacement values |
| `conditions` | array | No*      | Conditional rules for value selection |
> [!NOTE]
> Use either `values` or `conditions`, not both. Use `values` with `replaceType: FIRST/RANDOM/SEQUENTIAL`,
> and `conditions` with `replaceType: CONDITIONAL`.

---

## Search Types

Controls how many occurrences of the token are found.

| Value   | Description                                    |
|---------|------------------------------------------------|
| `ALL`   | Find and process every occurrence of the token |
| `FIRST` | Find and process only the first occurrence     |

Default: `ALL` (configurable in [Settings](SETTINGS.md))

---

## Replace Types

Controls how the replacement value is selected.

### `FIRST`

Always uses the first value from the list.

**Definition:**

```json
{
  "token": "%server_name%",
  "replaceType": "FIRST",
  "values": [
    "MyServer"
  ]
}
```

**Before (`server.properties`):**

```properties
motd=Welcome to %server_name%
server-name=%server_name%
```

**After:**

```properties
motd=Welcome to MyServer
server-name=MyServer
```

---

### `RANDOM`

Picks a random value for each occurrence.

**Definition:**

```json
{
  "token": "%motd%",
  "replaceType": "RANDOM",
  "values": [
    "Welcome!",
    "Hello there!",
    "Join now!"
  ]
}
```

**Before (`server.properties`):**

```properties
motd=%motd%
```

**After (one of these randomly):**

```properties
motd=Welcome!
```

```properties
motd=Hello there!
```

```properties
motd=Join now!
```

---

### `SEQUENTIAL`

Cycles through values in order. When reaching the end, wraps back to the first value.

**Definition:**

```json
{
  "token": "%color%",
  "replaceType": "SEQUENTIAL",
  "values": [
    "red",
    "green",
    "blue"
  ]
}
```

**Before (`config.yml`):**

```yaml
colors:
  slot1: "%color%"
  slot2: "%color%"
  slot3: "%color%"
  slot4: "%color%"
```

**After:**

```yaml
colors:
  slot1: "red"
  slot2: "green"
  slot3: "blue"
  slot4: "red"
```

The 4th occurrence wraps back to `red`.

---

### `CONDITIONAL`

Selects value based on condition rules. The replacement value changes depending on which service is starting.

**Definition:**

```json
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
    }
  ]
}
```

**Before (`server.properties`):**

```properties
max-players=%max_players%
```

**After (on Lobby-1):**

```properties
max-players=200
```

**After (on BedWars-1):**

```properties
max-players=16
```

See [Conditions](#conditions) below for more details.

Default: `FIRST` (configurable in [Settings](SETTINGS.md))

---

## Values

Static replacement values used with `FIRST`, `RANDOM`, or `SEQUENTIAL` replace types.

Values can include [Placeholders](PLACEHOLDERS.md) which are resolved at processing time:

**Environment variable injection:**

```json
{
  "token": "%db_host%",
  "values": [
    "%env:DATABASE_HOST%"
  ]
}
```

**Before (`plugins/Database/config.yml`):**

```yaml
database:
  host: "%db_host%"
```

**After (with `DATABASE_HOST=10.0.0.5` environment variable):**

```yaml
database:
  host: "10.0.0.5"
```

---

**Service information injection:**

```json
{
  "token": "%server_info%",
  "values": [
    "%serviceName% running on %nodeId%"
  ]
}
```

**Before (`config.yml`):**

```yaml
display-name: "%server_info%"
```

**After (on Lobby-1 running on Node-1):**

```yaml
display-name: "Lobby-1 running on Node-1"
```

---

## Conditions

Conditional rules for dynamic value selection based on service context. Used with `replaceType: CONDITIONAL`.

### Condition Fields

| Field         | Type   | Description                               |
|---------------|--------|-------------------------------------------|
| `when.field`  | string | Placeholder to check (e.g., `%taskName%`) |
| `when.equals` | string | Exact value to match                      |
| `when.regex`  | string | Regex pattern to match                    |
| `value`       | string | Replacement value if condition matches    |

> [!TIP]
> Use either `equals` for exact matching or `regex` for pattern matching, not both.

### How Conditions Work

Conditions are evaluated in order. The first matching condition wins.

**Definition:**

```json
{
  "token": "%view_distance%",
  "replaceType": "CONDITIONAL",
  "conditions": [
    {
      "when": {
        "field": "%taskName%",
        "equals": "Lobby"
      },
      "value": "4"
    },
    {
      "when": {
        "field": "%taskName%",
        "equals": "Survival"
      },
      "value": "12"
    },
    {
      "when": {
        "field": "%environment%",
        "equals": "MINECRAFT_SERVER"
      },
      "value": "8"
    }
  ]
}
```

**Before (`spigot.yml`):**

```yaml
world-settings:
  default:
    view-distance: %view_distance%
```

| Service Starting | Matches Condition                | Result              |
|------------------|----------------------------------|---------------------|
| Lobby-1          | `taskName = Lobby`               | `view-distance: 4`  |
| Survival-1       | `taskName = Survival`            | `view-distance: 12` |
| BedWars-1        | `environment = MINECRAFT_SERVER` | `view-distance: 8`  |

---

### Regex Matching

Use `regex` for pattern-based matching:

**Definition:**

```json
{
  "token": "%arena_size%",
  "replaceType": "CONDITIONAL",
  "conditions": [
    {
      "when": {
        "field": "%taskName%",
        "regex": ".*Wars$"
      },
      "value": "small"
    },
    {
      "when": {
        "field": "%taskName%",
        "regex": ".*Games$"
      },
      "value": "large"
    }
  ]
}
```

| Service       | Task Name   | Matches    | Result  |
|---------------|-------------|------------|---------|
| BedWars-1     | BedWars     | `.*Wars$`  | `small` |
| SkyWars-2     | SkyWars     | `.*Wars$`  | `small` |
| HungerGames-1 | HungerGames | `.*Games$` | `large` |

---

### Default/Fallback Value

Add a condition with empty `when` object as a fallback when nothing else matches:

**Definition:**

```json
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
      "when": {},
      "value": "50"
    }
  ]
}
```

| Service    | Matches                | Result |
|------------|------------------------|--------|
| Lobby-1    | `taskName = Lobby`     | `200`  |
| BedWars-1  | `taskName = BedWars`   | `16`   |
| Survival-1 | Nothing (uses default) | `50`   |
| SkyWars-1  | Nothing (uses default) | `50`   |

---

## Complete Examples

### Simple Value Replacement

```json
{
  "token": "%server_ip%",
  "values": [
    "play.example.com"
  ]
}
```

### Environment Variable Injection

```json
{
  "token": "%db_password%",
  "values": [
    "%env:DATABASE_PASSWORD%"
  ]
}
```

### Task-Based Configuration

```json
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
        "equals": "SkyWars"
      },
      "value": "24"
    },
    {
      "when": {},
      "value": "50"
    }
  ]
}
```

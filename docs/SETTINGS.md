# Settings

The settings file configures global module behavior. It's located at `modules/Templify/settings.json` (or `.yml` if you
switch formats).

## Structure

```json
{
  "defaults": {
    "searchType": "ALL",
    "replaceType": "FIRST"
  },
  "paths": {
    "filePatterns": [
      "**/*.yml",
      "**/*.txt"
    ]
  },
  "limits": {
    "maxFileSizeBytes": 524288
  }
}
```

---

## Sections

### `defaults`

Default values used when a definition doesn't specify its own `searchType` or `replaceType`.

| Field         | Type   | Default | Description                      |
|---------------|--------|---------|----------------------------------|
| `searchType`  | string | `ALL`   | How many occurrences to find     |
| `replaceType` | string | `FIRST` | How to select replacement values |

See [Definitions](DEFINITIONS.md) for detailed explanation of search types and replace types.

**Example:**

```json
{
  "defaults": {
    "searchType": "ALL",
    "replaceType": "FIRST"
  }
}
```

With these defaults, any definition that doesn't specify `searchType` or `replaceType` will search all occurrences and
use the first value.

---

### `paths`

Global file patterns that apply when a rule doesn't specify its own `files` field.

| Field          | Type  | Default                    | Description                        |
|----------------|-------|----------------------------|------------------------------------|
| `filePatterns` | array | `["**/*.yml", "**/*.txt"]` | Glob patterns for files to process |

**Example:**

```json
{
  "paths": {
    "filePatterns": [
      "**/*.yml",
      "**/*.yaml",
      "**/*.properties",
      "**/*.toml"
    ]
  }
}
```

This processes all YAML, properties, and TOML files in the service template when a rule doesn't specify its own file
patterns.

---

### `limits`

Safety limits to prevent processing extremely large files.

| Field              | Type   | Default           | Description                  |
|--------------------|--------|-------------------|------------------------------|
| `maxFileSizeBytes` | number | `524288` (512 KB) | Maximum file size to process |

Files larger than this limit are skipped to avoid memory issues.

**Example:**

```json
{
  "limits": {
    "maxFileSizeBytes": 1048576
  }
}
```

This allows processing files up to 1 MB.

---

## Full Example

```json
{
  "defaults": {
    "searchType": "ALL",
    "replaceType": "FIRST"
  },
  "paths": {
    "filePatterns": [
      "**/*.yml",
      "**/*.yaml",
      "**/*.properties",
      "**/*.txt",
      "**/*.toml",
      "**/*.json"
    ]
  },
  "limits": {
    "maxFileSizeBytes": 1048576
  }
}
```

---

## Changing Configuration Format

The configuration format is controlled by a marker file in the module's data directory (`modules/Templify/`).

By default, a `type=JSON` file is created. To switch to YAML:

1. Rename `type=JSON` to `type=YAML`
2. Restart the node (or reload the module)

> [!WARNING]
> Existing configuration files are not automatically migrated or deleted. You must manually convert your settings and
> rule files to the new format.

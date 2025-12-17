# Templify

> A CloudNet v4 module for rule-based template processing before service deployment. Define replacement rules with
> target filtering (by task, service, environment, group, or template), inject environment variables, and apply
> conditional values — all through simple configuration files.

# Information

![release](https://img.shields.io/github/v/release/whereareiam/Templify)
![build](https://img.shields.io/github/actions/workflow/status/whereareiam/Templify/development.yml)

[![GitHub Downloads](https://img.shields.io/github/downloads/whereareiam/Templify/total?&label=GitHub%20Downloads)](https://github.com/whereareiam/Templify/releases)

[![Discord](https://discord.com/api/guilds/856839365938118698/widget.png?style=banner2)](https://discord.arcadeya.com/)

# Documentation

- [Settings](docs/SETTINGS.md) — Module configuration options
- [Rules](docs/RULES.md) — Creating and configuring replacement rules
- [Targets](docs/TARGETS.md) — Filtering by task, service, environment, group, or template
- [Definitions](docs/DEFINITIONS.md) — Search tokens, replace types, and conditions
- [Placeholders](docs/PLACEHOLDERS.md) — Built-in placeholders

# Developer

Extend Templify with custom placeholder providers or replacement logic by depending on the API module. See
the [Javadocs](https://maven.whereareiam.me/javadoc/release/me/whereareiam/templify-api/latest) for API reference.

## 1. Choose a Repository

| Repository  | URL                                        | When to use                        |
|-------------|--------------------------------------------|------------------------------------|
| Release     | `https://maven.whereareiam.me/release`     | Stable versions (`1.0.0`, `2.1.0`) |
| Development | `https://maven.whereareiam.me/development` | Snapshot builds (`dev-abc1234`)    |

> Use the **release** repository for production. Use **development** only if you need the latest unreleased features.

## 2. Add the Dependency

<details>
<summary><b>Gradle (Kotlin DSL)</b></summary>

```kotlin
repositories {
    maven("https://maven.whereareiam.me/release")
}

dependencies {
    compileOnly("me.whereareiam:templify-api:VERSION")
}
```

</details>

<details>
<summary><b>Gradle (Groovy)</b></summary>

```groovy
repositories {
    maven { url 'https://maven.whereareiam.me/release' }
}

dependencies {
    compileOnly 'me.whereareiam:templify-api:VERSION'
}
```

</details>

<details>
<summary><b>Maven</b></summary>

```xml

<repository>
    <id>whereareiam</id>
    <url>https://maven.whereareiam.me/release</url>
</repository>

<dependency>
    <groupId>me.whereareiam</groupId>
    <artifactId>templify-api</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

</details>

## 3. Implement & Test

Use the Templify API to build your feature, then test with a local CloudNet v4 instance.

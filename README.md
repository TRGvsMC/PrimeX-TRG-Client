# PRIMEX TRG CLIENT

A Fabric client fork based on Aoba Client, being rebranded and prepared for a Minecraft 1.21.11 migration.

> **Compatibility status:** This is an in-progress Phase 01 rebrand. The checked-in source baseline still targets Minecraft **26.3**, Java **25**, Fabric Loader **0.19.5**, and Fabric API **0.161.0+26.3**. It is **not yet a Minecraft 1.21.11 build**. Do not install it in a 1.21.11 instance until the source/API port is complete and verified.

## Project identity

- Display name: PRIMEX TRG CLIENT
- Current source namespace and mod ID: `net.aoba` / `aoba` (retained temporarily to preserve existing entrypoints, add-on discovery, mixins, access widener, and saved-data compatibility)
- Build system: Gradle with Fabric Loom
- License: GNU GPL v3 or later; see [LICENSE](LICENSE)

## Build (current source baseline)

Requirements: JDK 25 and the Gradle wrapper.

```bash
./gradlew build
```

On Windows:

```bat
gradlew.bat build
```

The build currently resolves Minecraft 26.3 dependencies from `gradle.properties`. The 1.21.11 migration is not represented as complete by changing only metadata or dependency version strings.

## Phase 01 migration notes

Minecraft 26.3 uses the post-1.21.11 unobfuscated game and newer client/rendering APIs. Minecraft 1.21.11 is the final obfuscated release, so a genuine port requires source-level remapping and API work across the client, mixins, renderers, widgets, and optional integrations. The Aoba-derived package and resource namespace are retained during this initial rebrand to avoid breaking internal references.

## Upstream attribution

This project is derived from [Aoba Client](https://github.com/Cocolots/Aoba-Client). Original copyright and contributor notices are preserved. This fork's branding and modifications do not claim authorship of upstream work.

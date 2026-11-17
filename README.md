# PRIMEX TRG CLIENT

A Fabric client fork based on Aoba Client. This branch is being ported from the upstream Minecraft 26.3 source baseline to Minecraft 1.21.11.

> **Port status:** The build configuration now targets Minecraft **1.21.11** and Java **21**, but the Java sources and mixins are still undergoing the required API backport. This branch is not a verified 1.21.11 client until CI compilation, metadata/mixin validation, and runtime checks pass. Do not install it as a release build yet.

## Project identity

- Display name: PRIMEX TRG CLIENT
- Target: Minecraft 1.21.11 / Java 21 / Fabric
- Current internal mod ID and namespace: `aoba` / `net.aoba` (retained to preserve entrypoints, add-on discovery, mixins, access widener, and saved-data compatibility)
- Artifact target: `PRIMEX-TRG-CLIENT-1.21.11.jar`
- License: GNU GPL v3 or later; see [LICENSE](LICENSE)

## Build

Requirements: JDK 21 and the Gradle wrapper.

```bash
./gradlew build
```

On Windows:

```bat
gradlew.bat build
```

## Compatibility pins

The build uses Fabric API 0.141.1+1.21.11, Fabric Loader 0.19.5, Sodium 0.8.14, Lithium 0.21.1, Iris 1.10.8 and Mod Menu 17.0.1 as the 1.21.11 integration baseline. Optional integrations are compile-only and are not nested inside the client jar.

## Porting notes

Minecraft 26.3 uses the newer unobfuscated game and rendering APIs. Minecraft 1.21.11 uses the obfuscated distribution and the remapping Loom plugin. This requires real source-level work in renderers, GUI drawing, mixin targets, Sodium hooks, and access wideners; changing Gradle metadata does not complete that work.

## Upstream attribution

This project is derived from [Aoba Client](https://github.com/Cocolots/Aoba-Client). Original copyright and contributor notices are preserved. This fork's branding and modifications do not claim authorship of upstream work.

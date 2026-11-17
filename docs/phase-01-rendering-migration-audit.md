# PRIMEX 1.21.11 core rendering migration audit

## Audit baseline

- Branch: `phase-01/primex-rebrand-mc-1.21.11`
- Baseline branch HEAD: `8723cc9c99c0e8948ee9b18af66a7b4db230e56a`
- Minecraft target: `1.21.11`
- Java target: `21`
- Main Java source files in the branch tree: 459
- Java files under `net/aoba/mixin`: 67 (includes the Sodium mixin plugin and source mixins not currently listed in configuration)
- Configured mixin entries: 63, not 65 (13 common entries + 46 client entries + 4 optional Sodium entries)
- Configured mixin source names include 3 unconfigured interface mixins; these are not automatically treated as obsolete.

## Rendering inspection

The current `Renderer2D` and `Renderer3D` sources use Minecraft's `com.mojang.blaze3d` GPU, buffer, texture, and render-pass APIs. No `com.mojang.renderpearl.api` import was found in either renderer during the initial source inspection. This is not yet a repository-wide proof that no RenderPearl references remain.

The renderer implementations are substantial (766 lines in Renderer2D and 486 lines in Renderer3D). They must be verified by compiling against the pinned 1.21.11 mappings and by checking the actual render lifecycle at runtime; source inspection alone is not acceptance.

## CI/build baseline

The existing `.github/workflows/build.yml` already targets Java 21 on Ubuntu and Windows, runs `./gradlew build` / `gradlew.bat build`, checks the remapped artifact and required JAR entries, and uploads `PRIMEX-TRG-CLIENT-1.21.11.jar` from the Linux job.

No Actions workflow runs were returned for this branch at the time of this audit. A successful build and runtime smoke test have therefore not yet been established.

## Next verification gates

1. Run the existing Java 21 Gradle build and inspect compiler diagnostics.
2. Audit every configured mixin target and callback against the 1.21.11 mappings; separately verify the four optional Sodium mixins against the pinned Sodium build.
3. Scan all Java sources and dependent GUI/rendering utilities for legacy API references and stale signatures.
4. Validate the access widener, Fabric metadata, remapping, and required entries in the distributable JAR.
5. Verify the client in a 1.21.11 runtime before declaring rendering migration complete.

This file records the inspection baseline only; it does not claim that the migration is complete.

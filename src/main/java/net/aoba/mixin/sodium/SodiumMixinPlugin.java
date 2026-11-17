package net.aoba.mixin.sodium;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Keeps the Sodium integration optional and only enables injections verified
 * against the supported Sodium 0.8.14 / Minecraft 1.21.11 API.
 */
public final class SodiumMixinPlugin implements IMixinConfigPlugin {
    private static final String SUPPORTED_SODIUM_VERSION = "0.8.14";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return FabricLoader.getInstance().getModContainer("sodium")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .map(version -> version.contains(SUPPORTED_SODIUM_VERSION))
                .orElse(false);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }
}

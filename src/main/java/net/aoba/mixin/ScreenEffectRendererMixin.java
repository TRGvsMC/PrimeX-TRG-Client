package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/** Uses the actual 1.21.11 ScreenEffectRenderer render method descriptors. */
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
    @Inject(
            method = "renderFire(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void primex$disableFireOverlay(PoseStack matrices, MultiBufferSource buffers,
            TextureAtlasSprite sprite, CallbackInfo ci) {
        if (Aoba.getInstance() == null || Aoba.getInstance().moduleManager == null) return;
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoFireOverlay()) ci.cancel();
    }

    @Inject(
            method = "renderWater(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void primex$disableWaterOverlay(Minecraft minecraft, PoseStack matrices,
            MultiBufferSource buffers, CallbackInfo ci) {
        if (Aoba.getInstance() == null || Aoba.getInstance().moduleManager == null) return;
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoLiquidOverlay()) ci.cancel();
    }
}

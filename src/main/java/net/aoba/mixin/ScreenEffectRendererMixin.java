package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;

/** Hooks the actual 1.21.11 static fire and water overlay methods. */
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
    @Inject(
            method = "renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void primex$disableFireOverlay(Minecraft minecraft, PoseStack matrices, CallbackInfo ci) {
        if (Aoba.getInstance() == null || Aoba.getInstance().moduleManager == null) return;
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoFireOverlay()) ci.cancel();
    }

    @Inject(
            method = "renderWater(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void primex$disableWaterOverlay(Minecraft minecraft, PoseStack matrices, CallbackInfo ci) {
        if (Aoba.getInstance() == null || Aoba.getInstance().moduleManager == null) return;
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoLiquidOverlay()) ci.cancel();
    }
}

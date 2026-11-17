/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow private float spinningEffectTime;
    @Shadow private float spinningEffectSpeed;

    @Inject(method = "render", at = @At("HEAD"))
    private void primex$disablePortalOverlay(CallbackInfo ci) {
        AobaClient client = Aoba.getInstance();
        if (client == null || client.moduleManager == null) return;
        NoRender noRender = client.moduleManager.norender;
        if (noRender.state.getValue()) { spinningEffectTime = 0.0F; spinningEffectSpeed = 0.0F; }
    }

    @Inject(at = @At("HEAD"), method = "bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", cancellable = true)
    private void onBobViewWhenHurt(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        AobaClient client = Aoba.getInstance();
        if (client != null && client.moduleManager != null && client.moduleManager.norender.state.getValue()) {
            ci.cancel();
        }
    }
}

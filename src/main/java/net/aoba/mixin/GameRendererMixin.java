/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.AobaClient;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", cancellable = true)
    private void onBobViewWhenHurt(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        AobaClient client = AobaClient.getInstance();
        if (client != null && client.moduleManager != null && client.moduleManager.norender.state.getValue()) {
            ci.cancel();
        }
    }
}

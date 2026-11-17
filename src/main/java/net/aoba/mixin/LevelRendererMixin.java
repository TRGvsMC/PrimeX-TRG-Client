/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.mixin;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.Render3DEvent;
import net.aoba.rendering.Renderer3D;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(at = @At("TAIL"), method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V")
    private void onRenderLevel(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline,
            Camera camera, Matrix4f viewMatrix, Matrix4f projectionMatrix, Matrix4f frustumMatrix,
            GpuBufferSlice fogBuffer, Vector4f fogColor, boolean isFoggy, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();
        if (aoba == null || aoba.moduleManager == null || aoba.render3D == null)
            return;

        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(viewMatrix);
        Frustum frustum = new Frustum(viewMatrix, projectionMatrix);
        frustum.prepare(camera.position().x, camera.position().y, camera.position().z);

        Renderer3D renderer = aoba.render3D;
        renderer.beginFrame(matrixStack, frustum, camera, tickCounter);
        aoba.eventManager.Fire(new Render3DEvent(renderer));
        renderer.render();

        if (aoba.render2D != null) aoba.render2D.captureGameSnapshot();
    }

    @Inject(at = @At("HEAD"), method = "doesMobEffectBlockSky(Lnet/minecraft/client/Camera;)Z", cancellable = true)
    private void onHasBlindnessOrDarknessEffect(Camera camera, CallbackInfoReturnable<Boolean> cir) {
        if (Aoba.getInstance().moduleManager.norender.state.getValue()) cir.setReturnValue(false);
    }
}

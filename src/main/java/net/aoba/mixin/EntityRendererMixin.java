package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.aoba.Aoba;
import net.aoba.module.modules.combat.Nametags;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    private void primex$customNameTagStart(EntityRenderState state, PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (primex$shouldCustomizeNameTag(state)) {
            float scale = (float) Aoba.getInstance().moduleManager.nametags.getNametagScale();
            Vec3 attach = state.nameTagAttachment;
            poseStack.pushPose();
            poseStack.translate(attach.x * (1.0 - scale), attach.y * (1.0 - scale), attach.z * (1.0 - scale));
            poseStack.scale(scale, scale, scale);
        }
    }

    @Inject(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("RETURN"))
    private void primex$customNameTagEnd(EntityRenderState state, PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (primex$shouldCustomizeNameTag(state)) poseStack.popPose();
    }

    @Unique
    private boolean primex$shouldCustomizeNameTag(EntityRenderState state) {
        Nametags nametags = Aoba.getInstance().moduleManager.nametags;
        if (nametags == null || !nametags.state.getValue() || state.nameTagAttachment == null) return false;
        return !nametags.getPlayersOnly() || state instanceof AvatarRenderState;
    }
}

package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.XRay;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

@Mixin(LiquidBlockRenderer.class)
public class FluidRendererMixin {
    @Inject(
            method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRender(BlockAndTintGetter world, BlockPos pos, VertexConsumer output,
            BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();
        if (aoba == null || aoba.moduleManager == null) return;

        XRay xray = aoba.moduleManager.xray;
        if (xray.state.getValue()
                && !xray.isXRayBlock(blockState.getBlock())
                && !xray.fluids.getValue()) {
            ci.cancel();
        }
    }
}

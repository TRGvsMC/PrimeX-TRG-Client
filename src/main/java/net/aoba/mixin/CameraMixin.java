package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.movement.Freecam;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
    private Entity entity;

    /**
     * Camera#setEntity and alignWithEntity were folded into setup(Level, Entity,
     * boolean, boolean, float) in 1.21.11. Keep the existing camera transform while
     * Freecam is active so the module can drive position and rotation through ICamera.
     */
    @Inject(
            method = "setup(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void primex$preserveFreecamCamera(Level level, Entity focusedEntity, boolean detached,
            boolean mirrored, float partialTick, CallbackInfo ci) {
        AobaClient client = Aoba.getInstance();
        if (client != null && client.moduleManager != null
                && client.moduleManager.freecam.state.getValue() && this.entity != null) {
            ci.cancel();
        }
    }

    @Inject(method = "isDetached()Z", at = @At("HEAD"), cancellable = true)
    private void onIsDetached(CallbackInfoReturnable<Boolean> cir) {
        AobaClient client = AobaClient.getInstance();
        if (client != null && client.moduleManager != null && client.moduleManager.freecam.state.getValue()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "getFluidInCamera()Lnet/minecraft/world/level/material/FogType;",
            at = @At("HEAD"),
            cancellable = true)
    private void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
        AobaClient client = AobaClient.getInstance();
        if (client == null || client.moduleManager == null) return;

        Freecam freecam = client.moduleManager.freecam;
        NoRender noRender = client.moduleManager.norender;
        if (freecam.state.getValue() || (noRender.state.getValue() && noRender.getNoLiquidOverlay())) {
            cir.setReturnValue(FogType.NONE);
        }
    }
}

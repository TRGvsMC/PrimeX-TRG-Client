package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.AobaClient;
import net.minecraft.client.gui.render.GuiRenderer;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(CallbackInfo ci) {
        AobaClient client = AobaClient.getInstance();
        if (client != null && client.render2D != null) client.render2D.render();
    }
}

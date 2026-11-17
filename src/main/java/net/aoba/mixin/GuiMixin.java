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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.event.events.Render2DEvent;
import net.aoba.module.modules.render.NoRender;
import net.aoba.rendering.Renderer2D;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

@Mixin(Gui.class)
public class GuiMixin {
    private static final String POWDER_SNOW_PATH = "textures/misc/powder_snow_outline.png";
    private static final String PUMPKIN_PATH = "textures/misc/pumpkinblur.png";

    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V")
    private void onRenderGui(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        Renderer2D renderer = Aoba.getInstance().render2D;
        renderer.beginFrame(context, tickCounter);
        Aoba.getInstance().eventManager.Fire(new Render2DEvent(renderer));
    }

    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    private void onRenderVignetteOverlay(GuiGraphics context, Entity entity, CallbackInfo ci) {
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoVignette()) ci.cancel();
    }

    @ModifyVariable(method = "renderTextureOverlay", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyOverlayOpacity(float opacity, GuiGraphics context, Identifier identifier) {
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender == null || !noRender.state.getValue()) return opacity;
        String path = identifier.getPath();
        if (path.equals(PUMPKIN_PATH) && noRender.getNoPumpkinOverlay()) return 0f;
        if (path.equals(POWDER_SNOW_PATH) && noRender.getNoPowderSnowOverlay()) return 0f;
        return opacity;
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(GuiGraphics context, float nauseaStrength, CallbackInfo ci) {
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoPortalOverlay()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (Aoba.getInstance().guiManager.isClickGuiOpen()) {
            ci.cancel();
            return;
        }
        NoRender noRender = Aoba.getInstance().moduleManager.norender;
        if (noRender.state.getValue() && noRender.getNoCrosshair()) ci.cancel();
    }
}

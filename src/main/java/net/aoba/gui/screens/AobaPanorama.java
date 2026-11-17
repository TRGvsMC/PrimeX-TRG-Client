package net.aoba.gui.screens;

import net.aoba.rendering.utils.TextureBank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;

/** 1.21.11 panorama adapter retained for optional callers. */
public final class AobaPanorama {
    private static final CubeMap CUBE_MAP = new CubeMap(TextureBank.mainmenu_panorama);
    private static final PanoramaRenderer PANORAMA = new PanoramaRenderer(CUBE_MAP);
    private static boolean texturesRegistered;

    public static void registerPanoramaTextures() {
        if (!texturesRegistered) {
            CUBE_MAP.registerTextures(Minecraft.getInstance().getTextureManager());
            texturesRegistered = true;
        }
    }

    public void render(GuiGraphics graphics, int width, int height, boolean shouldSpin) {
        if (!texturesRegistered) registerPanoramaTextures();
        PANORAMA.render(graphics, width, height, shouldSpin);
    }
}

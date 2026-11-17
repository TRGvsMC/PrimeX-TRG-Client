/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.gui.screens;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** PRIMEX credits screen, rendered with the 1.21.11 GuiGraphics API. */
public class AobaCreditsScreen extends Screen {
    private static final List<String> CONTRIBUTORS = Arrays.asList(
            "coltonk9043", "cvs0", "Tewxx", "OsakiTsukiko",
            "Logging4J", "TangyKiwi", "Xatsec", "BatchDebug");
    private float animationProgress;

    public AobaCreditsScreen() {
        super(Component.literal("PRIMEX TRG CLIENT - Credits"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        animationProgress += delta / 200.0f;
        if (animationProgress >= 1.0f) animationProgress -= 1.0f;
        int lineHeight = font.lineHeight + 10;
        int contentHeight = CONTRIBUTORS.size() * lineHeight;
        int baseY = (int) (height - animationProgress * (height + contentHeight));
        graphics.drawCenteredString(font, "PRIMEX TRG CLIENT", width / 2, baseY - 80, 0xFFB98AFF);
        graphics.drawCenteredString(font, "CREDITS", width / 2, baseY - 62, 0xFFF4EDFF);
        for (int i = 0; i < CONTRIBUTORS.size(); i++) {
            int y = baseY + i * lineHeight;
            float alpha = fadeAlpha(y);
            int color = ((int) (alpha * 255) << 24) | 0x00E9E1F2;
            graphics.drawCenteredString(font, CONTRIBUTORS.get(i), width / 2, y, color);
        }
        graphics.drawCenteredString(font, "Press ESC to return", width / 2, height - 24, 0xFF9B8EAA);
        super.render(graphics, mouseX, mouseY, delta);
    }

    private float fadeAlpha(int y) {
        float fadeHeight = Math.max(1.0f, height / 4.0f);
        float alpha = 1.0f;
        if (y < fadeHeight) alpha = y / fadeHeight;
        else if (y > height - fadeHeight) alpha = (height - y) / fadeHeight;
        return Math.max(0.0f, Math.min(1.0f, alpha));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF0B0A10);
        graphics.fill(0, height / 3, width, height, 0xFF100D18);
    }
}

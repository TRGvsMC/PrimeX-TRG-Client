/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.gui.components.widgets;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** 1.21.11 GuiGraphics implementation of the legacy image button. */
public class AobaImageButtonWidget extends AbstractButton {
    private Consumer<AobaImageButtonWidget> pressAction;
    private final Identifier image;
    private final boolean background;

    public AobaImageButtonWidget(int x, int y, int width, int height, Identifier image) {
        this(x, y, width, height, image, true);
    }

    public AobaImageButtonWidget(int x, int y, int width, int height, Identifier image, boolean background) {
        super(x, y, width, height, Component.empty());
        this.image = image;
        this.background = background;
    }

    public void setPressAction(Consumer<AobaImageButtonWidget> pressAction) {
        this.pressAction = pressAction;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (pressAction != null) pressAction.accept(this);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (background) {
            int fill = isHovered() ? 0xFF30213F : 0xFF19151F;
            int edge = isHovered() ? 0xFFB98AFF : 0xFF554267;
            graphics.fill(getX(), getY(), getX() + width, getY() + height, fill);
            graphics.fill(getX(), getY(), getX() + width, getY() + 1, edge);
            graphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, edge);
            graphics.fill(getX(), getY(), getX() + 1, getY() + height, edge);
            graphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, edge);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, image, getX(), getY(),
                0.0f, 0.0f, width, height, width, height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}

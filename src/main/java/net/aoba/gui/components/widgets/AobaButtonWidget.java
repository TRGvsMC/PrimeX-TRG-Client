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
import net.minecraft.network.chat.Component;

/** Lightweight 1.21.11-compatible PRIMEX button. */
public class AobaButtonWidget extends AbstractButton {
    private Consumer<AobaButtonWidget> pressAction;

    public AobaButtonWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public void setPressAction(Consumer<AobaButtonWidget> pressAction) {
        this.pressAction = pressAction;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (pressAction != null) pressAction.accept(this);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int background = isHovered() ? 0xFF30213F : 0xFF19151F;
        int border = isHovered() ? 0xFFB98AFF : 0xFF554267;
        int foreground = isHovered() ? 0xFFFFFFFF : 0xFFE9E1F2;
        graphics.fill(getX(), getY(), getX() + width, getY() + height, background);
        graphics.fill(getX(), getY(), getX() + width, getY() + 1, border);
        graphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, border);
        graphics.fill(getX(), getY(), getX() + 1, getY() + height, border);
        graphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, border);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                getMessage(), getX() + width / 2, getY() + (height - 8) / 2, foreground);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}

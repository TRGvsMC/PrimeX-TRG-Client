/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.gui.screens;

import static net.aoba.AobaClient.MC;

import java.net.URI;

import com.mojang.realmsclient.RealmsMainScreen;
import net.aoba.AobaClient;
import net.aoba.api.IAddon;
import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.aoba.gui.screens.addons.AddonScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

/** PRIMEX main menu using the 1.21.11 GuiGraphics screen lifecycle. */
public class MainMenuScreen extends Screen {
    private static final int BUTTON_WIDTH = 164;
    private static final int BUTTON_HEIGHT = 27;
    private static final int SPACING = 8;
    private int smallScreenHeightOffset;

    public MainMenuScreen() {
        super(Component.literal("PRIMEX TRG CLIENT"));
    }

    @Override
    protected void init() {
        super.init();
        smallScreenHeightOffset = height <= 650 ? 34 : 0;
        int startX = (width - (BUTTON_WIDTH * 2 + SPACING)) / 2;
        int startY = (height - (BUTTON_HEIGHT * 3 + SPACING * 2)) / 2 + smallScreenHeightOffset;

        addMenuButton(startX, startY, "Singleplayer", () -> minecraft.setScreen(new SelectWorldScreen(this)));
        addMenuButton(startX + BUTTON_WIDTH + SPACING, startY, "Multiplayer",
                () -> minecraft.setScreen(new JoinMultiplayerScreen(this)));
        addMenuButton(startX, startY + BUTTON_HEIGHT + SPACING, "Realms",
                () -> minecraft.setScreen(new RealmsMainScreen(this)));
        addMenuButton(startX + BUTTON_WIDTH + SPACING, startY + BUTTON_HEIGHT + SPACING, "Settings",
                () -> minecraft.setScreen(new OptionsScreen(this, MC.options)));
        addMenuButton(startX, startY + (BUTTON_HEIGHT + SPACING) * 2, "Add-ons",
                () -> minecraft.setScreen(new AddonScreen(this)));
        addMenuButton(startX + BUTTON_WIDTH + SPACING, startY + (BUTTON_HEIGHT + SPACING) * 2, "Quit",
                () -> minecraft.destroy());

        AobaButtonWidget credits = new AobaButtonWidget(width - 184, height - 34, 82, 22,
                Component.literal("Credits"));
        credits.setPressAction(button -> MC.setScreen(new AobaCreditsScreen()));
        addRenderableWidget(credits);

        AobaButtonWidget discord = new AobaButtonWidget(width - 94, height - 34, 82, 22,
                Component.literal("Discord"));
        discord.setPressAction(button -> Util.getPlatform().openUri(URI.create("https://discord.gg/CDa4etPFtk")));
        addRenderableWidget(discord);

        try {
            Class<?> modsScreen = getClass().getClassLoader()
                    .loadClass("com.terraformersmc.modmenu.gui.ModsScreen");
            AobaButtonWidget mods = new AobaButtonWidget(12, height - 34, 82, 22,
                    Component.literal("Mods"));
            mods.setPressAction(button -> {
                try {
                    minecraft.setScreen((Screen) modsScreen.getConstructor(Screen.class).newInstance(this));
                } catch (ReflectiveOperationException exception) {
                    exception.printStackTrace();
                }
            });
            addRenderableWidget(mods);
        } catch (ClassNotFoundException ignored) {
            // Mod Menu is optional.
        }
    }

    private void addMenuButton(int x, int y, String label, Runnable action) {
        AobaButtonWidget button = new AobaButtonWidget(x, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal(label));
        button.setPressAction(ignored -> action.run());
        addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int centerX = width / 2;
        int startY = (height - (BUTTON_HEIGHT * 3 + SPACING * 2)) / 2 + smallScreenHeightOffset;
        graphics.fill(centerX - 100, startY - 55, centerX + 100, startY - 53, 0xFF9B65E8);
        graphics.drawCenteredString(font, "PRIMEX TRG", centerX, startY - 88, 0xFFF4EDFF);
        graphics.drawCenteredString(font, "CLIENT", centerX, startY - 72, 0xFFB98AFF);
        graphics.drawString(font, "PRIMEX TRG CLIENT", 12, height - 12, 0xFFB98AFF, false);

        if (AobaClient.addons.isEmpty()) {
            String status = "No add-ons loaded";
            graphics.drawString(font, status, width - font.width(status) - 12, 12, 0xFFB8AFC4, false);
        } else {
            int y = 12;
            for (IAddon addon : AobaClient.addons) {
                String line = addon.getName() + "  /  " + addon.getAuthor();
                graphics.drawString(font, line, width - font.width(line) - 12, y, 0xFFD8C9EC, false);
                y += 12;
            }
        }
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF0B0A10);
        graphics.fill(0, 0, width, Math.max(1, height / 3), 0xFF11101A);
    }
}

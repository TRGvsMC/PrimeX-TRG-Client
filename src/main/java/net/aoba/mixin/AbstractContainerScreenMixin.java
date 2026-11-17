package net.aoba.mixin;


import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Colors;
import net.aoba.module.modules.render.Tooltips;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Unique
    private final MapRenderState primex$mapRenderState = new MapRenderState();

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Shadow @Nullable protected Slot hoveredSlot;
    @Shadow protected abstract List<Component> getTooltipFromContainerItem(ItemStack stack);
    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    @Unique
    private boolean primex$hasItems(ItemStack stack) {
        return stack.get(DataComponents.CONTAINER) != null;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("TAIL"))
    private void primex$renderContainerPreviews(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Tooltips tooltips = Aoba.getInstance().moduleManager.tooltips;
        if (!tooltips.state.getValue() || hoveredSlot == null || hoveredSlot.getItem().isEmpty()
                || minecraft.player.inventoryMenu.getCarried().isEmpty() == false) return;

        ItemStack stack = hoveredSlot.getItem();
        if (primex$hasItems(stack) && tooltips.getStorage()) {
            primex$renderShulkerTooltip(context, mouseX, mouseY, mouseX, mouseY, stack);
        } else if (stack.is(Items.FILLED_MAP) && tooltips.getMap()) {
            primex$drawMapPreview(context, stack, mouseX, mouseY);
        }
    }

    @Unique
    private boolean primex$renderShulkerTooltip(GuiGraphics context, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents == null) return false;

        float[] tint = {1.0F, 1.0F, 1.0F, 1.0F};
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock shulker) {
            try {
                Color color = new Color(Objects.requireNonNull(shulker.getColor()).getTextureDiffuseColor(), true);
                tint = new float[] {color.getRed() / 255.0F, color.getGreen() / 255.0F,
                        color.getBlue() / 255.0F, color.getAlpha() / 255.0F};
            } catch (NullPointerException ignored) {
                // Use the neutral preview tint for colorless shulker variants.
            }
        }

        List<ItemStack> items = contents.itemCopies().toList();
        int panelX = x + 8;
        int panelY = y - 82;
        primex$drawShulkerBackground(context, panelX, panelY, tint);

        int row = 0;
        int column = 0;
        for (ItemStack contained : items) {
            if (!contained.isEmpty()) {
                int itemX = panelX + 8 + column * 18;
                int itemY = panelY + 7 + row * 18;
                context.renderItem(contained, itemX, itemY);
                if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                    context.setTooltipForNextFrame(font, getTooltipFromContainerItem(contained),
                            contained.getTooltipImage(), mouseX, mouseY);
                }
            }

            if (++column >= 9) {
                column = 0;
                row++;
            }
        }

        Font previewFont = GuiManager.fontSetting.getValue().getRenderer();
        Aoba.getInstance().render2D.drawStringWithScale(stack.getHoverName().getString(),
                panelX + 7, panelY - 11, Shader.solid(Colors.White), 1.0F, previewFont);
        return true;
    }

    @Unique
    private void primex$drawShulkerBackground(GuiGraphics context, int x, int y, float[] tint) {
        int width = 178;
        int height = 70;
        int accent = new Color(tint[0], tint[1], tint[2], 1.0F).getRGB();
        context.fill(x, y, x + width, y + height, 0xF20C0B12);
        context.fill(x, y, x + width, y + 2, accent);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF554267);
        context.fill(x, y, x + 1, y + height, 0xFF554267);
        context.fill(x + width - 1, y, x + width, y + height, 0xFF554267);
    }

    @Unique
    private void primex$drawMapPreview(GuiGraphics context, ItemStack stack, int mouseX, int mouseY) {
        MapItemSavedData mapData = MapItem.getSavedData(stack, minecraft.level);
        if (mapData == null) return;

        mapData.getHoldingPlayer(minecraft.player);
        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (mapId == null) return;

        int x = mouseX + 16;
        int y = mouseY - 4;
        float scale = 84.0F / 128.0F;
        context.pose().pushMatrix();
        context.pose().translate(x, y);
        context.pose().scale(scale, scale);
        MapRenderer mapRenderer = minecraft.getMapRenderer();
        mapRenderer.extractRenderState(mapId, mapData, primex$mapRenderState);
        context.submitMapRenderState(primex$mapRenderState);
        context.pose().popMatrix();
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("HEAD"), cancellable = true)
    private void primex$hideVanillaPreviewTooltip(GuiGraphics context, int mouseX, int mouseY, CallbackInfo ci) {
        Tooltips tooltips = Aoba.getInstance().moduleManager.tooltips;
        if (!tooltips.state.getValue() || hoveredSlot == null || hoveredSlot.getItem().isEmpty()
                || !minecraft.player.inventoryMenu.getCarried().isEmpty()) return;

        ItemStack stack = hoveredSlot.getItem();
        if ((stack.is(Items.FILLED_MAP) && tooltips.getMap())
                || (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof ShulkerBoxBlock && tooltips.getStorage())) {
            ci.cancel();
        }
    }
}

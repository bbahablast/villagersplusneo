package com.lion.villagersplus.client;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.menu.AlchemistTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** Brewing-stand style screen for the alchemist table. */
public class AlchemistTableScreen extends AbstractContainerScreen<AlchemistTableMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VillagersPlus.MOD_ID, "textures/gui/container/alchemist_table.png");

    /** Heights of the bubble column, indexed by animation step. */
    private static final int[] BUBBLE_LENGTHS = {29, 24, 20, 16, 11, 6, 0};

    public AlchemistTableScreen(AlchemistTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // On 1.20.1 the container screen does not draw the dimmed backdrop itself.
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Fuel gauge.
        int fuel = this.menu.getFuel();
        if (fuel > 0) {
            int width = Mth.clamp(18 * fuel / 20, 0, 18);
            if (width > 0) {
                graphics.blit(TEXTURE, x + 60, y + 44, 176, 29, width, 4);
            }
        }

        float progress = this.menu.getBrewProgress();
        if (progress > 0.0F) {
            // Arrow fills as the brew completes; progress counts down from 1.
            int arrow = (int) (28.0F * (1.0F - progress));
            if (arrow > 0) {
                graphics.blit(TEXTURE, x + 97, y + 16, 176, 0, 9, arrow);
            }
            int bubble = BUBBLE_LENGTHS[(int) (progress * 6.0F) % BUBBLE_LENGTHS.length];
            if (bubble > 0) {
                graphics.blit(TEXTURE, x + 63, y + 14 + 29 - bubble, 185, 29 - bubble, 12, bubble);
            }
        }
    }
}

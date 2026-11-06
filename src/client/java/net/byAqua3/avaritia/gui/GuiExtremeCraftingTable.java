package net.byAqua3.avaritia.gui;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.inventory.MenuExtremeCrafting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GuiExtremeCraftingTable extends AbstractContainerScreen<MenuExtremeCrafting> {

	public static final Identifier BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID,
			"textures/gui/extreme_crafting.png");
	public static final Identifier COMPAT_BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID,
			"textures/gui/extreme_crafting_compat.png");
	public static final Identifier COMPAT_DARK_BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID,
			"textures/gui/extreme_crafting_compat_dark.png");

	public GuiExtremeCraftingTable(MenuExtremeCrafting menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 238, 256);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION,
				this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
	}
}

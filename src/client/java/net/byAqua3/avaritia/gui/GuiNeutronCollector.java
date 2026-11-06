package net.byAqua3.avaritia.gui;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.inventory.MenuNeutronCollector;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GuiNeutronCollector extends AbstractContainerScreen<MenuNeutronCollector> {

	public static final Identifier BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID, "textures/gui/neutron_collector.png");
	public static final Identifier DARK_BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID, "textures/gui/neutron_collector_dark.png");

	public GuiNeutronCollector(MenuNeutronCollector menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION,
				this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, this.titleLabelY, -12566464, false);
		String text = String.format("%.2f%%", 100.0F * this.menu.getProgress() / AvaritiaConfigs.productionTicks);
		graphics.text(this.font, text, this.imageWidth / 2 - this.font.width(text) / 2, 60, -12566464, false);
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
	}
}


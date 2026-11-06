package net.byAqua3.avaritia.gui;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.inventory.MenuNeutroniumCompressor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GuiNeutroniumCompressor extends AbstractContainerScreen<MenuNeutroniumCompressor> {

	public static final Identifier BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID, "textures/gui/compressor.png");
	public static final Identifier DARK_BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID, "textures/gui/compressor_dark.png");

	public GuiNeutroniumCompressor(MenuNeutroniumCompressor menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	private boolean isMouseHover(float x, float y, float width, float height, int mouseX, int mouseY) {
		return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
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
		if (this.menu.getCompressionProgress() > 0) {
			String text = this.menu.getCompressionProgress() + " / " + this.menu.getCompressionTarget();
			graphics.text(this.font, text, this.imageWidth / 2 - this.font.width(text) / 2, 60, -12566464, false);
		}
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractContents(graphics, mouseX, mouseY, partialTicks);

		if (this.menu.getConsumptionProgress() > 0 && this.menu.getCompressionProgress() > 0) {
			float progress = 22.0F * ((float) this.menu.getConsumptionProgress() / this.menu.getCompressionProgress());
			graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION,
					this.leftPos + 62, this.topPos + 35, 176.0F, 0.0F, (int) progress, 16, 256, 256);
		}

		if (this.menu.getCompressionProgress() > 0 && this.menu.getCompressionTarget() > 0) {
			float progress = 16.0F * ((float) this.menu.getCompressionProgress() / this.menu.getCompressionTarget());
			graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION,
					this.leftPos + 90, this.topPos + 35 + (16 - (int) progress),
					176.0F, 32.0F - (int) progress, 16, (int) progress, 256, 256);

			if (this.isMouseHover(this.leftPos + 90.0F, this.topPos + 35.0F, 16.0F, 16.0F, mouseX, mouseY)) {
				String text = String.format("%.2f%%", 100.0F * this.menu.getCompressionProgress() / this.menu.getCompressionTarget());
				graphics.setTooltipForNextFrame(this.font, Component.literal(text), mouseX, mouseY);
			}
		}
	}
}


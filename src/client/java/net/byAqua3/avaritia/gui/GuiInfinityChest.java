package net.byAqua3.avaritia.gui;

import java.text.DecimalFormat;
import java.util.List;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.inventory.MenuInfinityChest;
import net.byAqua3.avaritia.inventory.slot.SlotInfinity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GuiInfinityChest extends AbstractContainerScreen<MenuInfinityChest> {

	public static final Identifier BACKGROUND_LOCATION = Identifier.tryBuild(Avaritia.MODID, "textures/gui/infinity_chest.png");

	public GuiInfinityChest(MenuInfinityChest menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 462, 330);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION,
				this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 512, 512);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
		graphics.text(this.font, this.playerInventoryTitle, 152, 236, -12566464, false);
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (!this.menu.getCarried().isEmpty() || this.hoveredSlot == null || !this.hoveredSlot.hasItem()) {
			return;
		}
		ItemStack itemStack = this.hoveredSlot.getItem();
		List<Component> components = this.getTooltipFromContainerItem(itemStack);
		if (this.hoveredSlot instanceof SlotInfinity) {
			components.add(Component.translatable("avaritia:container.infinity_chest.info",
					itemStack.getCount(), this.menu.getSlotMaxStack(this.hoveredSlot)));
		}
		graphics.setTooltipForNextFrame(this.font, components, itemStack.getTooltipImage(), mouseX, mouseY);
	}

	@Override
	protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
		int x = slot.x;
		int y = slot.y;
		ItemStack itemStack = slot.getItem();

		Identifier noItemIcon = slot.getNoItemIcon();
		if (itemStack.isEmpty() && slot.isActive() && noItemIcon != null) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, noItemIcon, x, y, 16, 16);
			return;
		}

		if (slot.isFake()) {
			graphics.fakeItem(itemStack, x, y);
		} else {
			graphics.item(itemStack, x, y);
		}

		if (!itemStack.isEmpty()) {
			graphics.itemDecorations(this.font, itemStack, x, y, this.formatCount(itemStack.getCount()));
		}
	}

	private String formatCount(int count) {
		if (count >= 1_000_000_000) {
			return new DecimalFormat("#").format(count / 1_000_000_000) + "G";
		}
		if (count >= 1_000_000) {
			return new DecimalFormat("#").format(count / 1_000_000) + "M";
		}
		if (count >= 10_000) {
			return new DecimalFormat("#").format(count / 10_000) + "W";
		}
		if (count >= 1_000) {
			return new DecimalFormat("#").format(count / 1_000) + "K";
		}
		return null;
	}
}




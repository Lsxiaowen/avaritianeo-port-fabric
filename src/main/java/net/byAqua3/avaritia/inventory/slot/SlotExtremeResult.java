package net.byAqua3.avaritia.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Result slot for the extreme crafting table.
 *
 * Consumption iterates the *positioned* input (the bounding box of the filled
 * part of the grid) rather than the full 9x9 area. Using the whole grid while
 * also adding {@code left/top} walks past the pattern and leaves ingredients
 * behind, which is what made crafted items not consume all their materials.
 */
public class SlotExtremeResult extends ResultSlot {

	private final CraftingContainer craftSlots;

	private Recipe<CraftingInput> usedRecipe;

	public SlotExtremeResult(Player player, CraftingContainer craftSlots, Container container, int idx, int xPos,
			int yPos) {
		super(player, craftSlots, container, idx, xPos, yPos);
		this.craftSlots = craftSlots;
	}

	public void setUsedRecipe(Recipe<CraftingInput> usedRecipe) {
		this.usedRecipe = usedRecipe;
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		if (this.usedRecipe == null) {
			return;
		}

		CraftingInput.Positioned positioned = this.craftSlots.asPositionedCraftInput();
		CraftingInput input = positioned.input();
		int left = positioned.left();
		int top = positioned.top();
		int gridWidth = this.craftSlots.getWidth();

		for (int row = 0; row < input.height(); row++) {
			for (int col = 0; col < input.width(); col++) {
				int slot = (left + col) + (top + row) * gridWidth;
				if (slot < 0 || slot >= this.craftSlots.getContainerSize()) {
					continue;
				}
				ItemStack slotStack = this.craftSlots.getItem(slot);
				if (!slotStack.isEmpty()) {
					ItemStack consumed = slotStack.copy();
					consumed.shrink(1);
					this.craftSlots.setItem(slot, consumed.isEmpty() ? ItemStack.EMPTY : consumed);
				}
			}
		}
	}
}

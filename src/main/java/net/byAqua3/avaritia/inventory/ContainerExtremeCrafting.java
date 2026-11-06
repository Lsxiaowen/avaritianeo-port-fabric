package net.byAqua3.avaritia.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * 9x9 crafting grid backed by the block entity's storage.
 *
 * Every accessor is routed to {@code matrix}. {@link #asCraftInput()} must be
 * overridden as well: the superclass builds it from its own private item list,
 * which stays empty here, so the inherited version would report an empty grid
 * and no recipe would ever match.
 */
public class ContainerExtremeCrafting extends TransientCraftingContainer {

	private final SimpleContainer matrix;

	private final AbstractContainerMenu container;

	public ContainerExtremeCrafting(AbstractContainerMenu container, int width, int height, SimpleContainer matrix) {
		super(container, width, height);
		this.matrix = matrix;
		this.container = container;
	}

	@Override
	public CraftingInput asCraftInput() {
		return CraftingInput.of(this.getWidth(), this.getHeight(), this.getItems());
	}

	/**
	 * Must return the live grid contents.
	 *
	 * The inherited default returns the superclass's own private list, which stays
	 * empty here. Both {@code asPositionedCraftInput()} (used when consuming
	 * ingredients on take) and the default {@code asCraftInput()} depend on it, so
	 * leaving it unoverridden makes the recipe consume the wrong slots.
	 */
	@Override
	public List<ItemStack> getItems() {
		List<ItemStack> stacks = new ArrayList<>(this.matrix.getContainerSize());
		for (int i = 0; i < this.matrix.getContainerSize(); i++) {
			stacks.add(this.matrix.getItem(i));
		}
		return stacks;
	}

	@Override
	public int getContainerSize() {
		return this.matrix.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return this.matrix.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.matrix.getItem(slot);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return this.matrix.removeItemNoUpdate(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack itemStack = this.matrix.removeItem(slot, count);
		if (!itemStack.isEmpty()) {
			this.container.slotsChanged(this);
		}
		return itemStack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.matrix.setItem(slot, stack);
		this.container.slotsChanged(this);
	}

	@Override
	public void setChanged() {
		this.matrix.setChanged();
	}

	@Override
	public void fillStackedContents(net.minecraft.world.entity.player.StackedItemContents stackedContents) {
		for (int i = 0; i < this.matrix.getContainerSize(); i++) {
			stackedContents.accountSimpleStack(this.matrix.getItem(i));
		}
	}
}

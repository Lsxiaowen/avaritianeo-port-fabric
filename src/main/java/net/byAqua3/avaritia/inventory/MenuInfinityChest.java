package net.byAqua3.avaritia.inventory;

import net.byAqua3.avaritia.inventory.slot.SlotInfinity;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.byAqua3.avaritia.loader.AvaritiaMenus;
import net.byAqua3.avaritia.tile.TileInfinityChest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MenuInfinityChest extends AbstractContainerMenu {

	private final TileInfinityChest tile;

	public MenuInfinityChest(int id, Inventory inventory, BlockPos pos) {
		this(id, inventory, (TileInfinityChest) inventory.player.level().getBlockEntity(pos));
	}

	public MenuInfinityChest(int id, Inventory inventory, TileInfinityChest tile) {
		super(AvaritiaMenus.INFINITY_CHEST, id);
		this.tile = tile;
		this.tile.chest.startOpen(inventory.player);

		int y;
		for (y = 0; y < 12; y++) {
			for (int i = 0; i < 25; i++) {
				this.addSlot(new SlotInfinity(tile.chest, i + y * 25, 8 + i * 18, 18 + y * 18));
			}
		}

		for (y = 0; y < 3; y++) {
			for (int i = 0; i < 9; i++) {
				this.addSlot(new Slot(inventory, i + y * 9 + 9, 152 + i * 18, 248 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			this.addSlot(new Slot(inventory, x, 152 + x * 18, 306));
		}
	}

	public TileInfinityChest getTile() {
		return this.tile;
	}

	public static boolean canItemQuickReplace(Container inventory, Slot slot, ItemStack stack, boolean stackSizeMatters, int maxStackSize) {
		boolean empty = slot == null || !slot.hasItem();
		if (empty) {
			return true;
		}
		return ItemStack.isSameItemSameComponents(stack, slot.getItem())
				&& slot.getItem().getCount() + (stackSizeMatters ? 0 : stack.getCount()) < (slot instanceof SlotInfinity ? maxStackSize : stack.getMaxStackSize());
	}

	public int getSlotMaxStack(Slot slot, ItemStack stack) {
		int maxStackSize = Math.max(stack.getCount(), AvaritiaConfigs.maxStackSize);
		return slot instanceof SlotInfinity ? Math.min(slot.getMaxStackSize(), maxStackSize) : slot.getMaxStackSize(stack);
	}

	public int getSlotMaxStack(Slot slot) {
		return this.getSlotMaxStack(slot, slot.getItem());
	}

	public ItemStack safeInsert(Slot slot, ItemStack stack, int increment) {
		if (stack.isEmpty() || !slot.mayPlace(stack)) {
			return stack;
		}
		ItemStack existing = slot.getItem();
		int i = Math.min(Math.min(increment, stack.getCount()), this.getSlotMaxStack(slot, stack) - existing.getCount());

		if (existing.isEmpty()) {
			slot.setByPlayer(stack.split(i));
		} else if (ItemStack.isSameItemSameComponents(existing, stack)) {
			stack.shrink(i);
			existing.grow(i);
			slot.setByPlayer(existing);
		}
		return stack;
	}

	@Override
	public void clicked(int slotId, int button, ContainerInput input, Player player) {
		// Delegate to the vanilla 26.1 pipeline; the infinity chest only widens
		// stack limits, which SlotInfinity already enforces through getMaxStackSize.
		super.clicked(slotId, button, input, player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack slotStack = slot.getItem();
		ItemStack original = slotStack.copy();

		if (index < 300) {
			if (!this.moveItemStackTo(slotStack, 300, 336, true)) {
				return ItemStack.EMPTY;
			}
		} else if (!this.moveItemStackTo(slotStack, 0, 300, false)) {
			return ItemStack.EMPTY;
		}

		if (slotStack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return original;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.tile.chest.stopOpen(player);
	}

	@Override
	public boolean stillValid(Player player) {
		BlockPos pos = this.tile.getBlockPos();
		return this.tile.getLevel().getBlockState(pos).is(AvaritiaBlocks.INFINITY_CHEST)
				&& player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}
}

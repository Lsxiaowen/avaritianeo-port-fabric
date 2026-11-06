package net.byAqua3.avaritia.tile;

import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileNeutronCollector extends TileMachine implements WorldlyContainer {

	private static final int[] OUTPUT_SLOTS = { 0 };
	private static final int[] NO_SLOTS = {};

	public final SimpleContainer result = new SimpleContainer(1) {
		@Override
		public void setChanged() {
			TileNeutronCollector.this.setChanged();
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			return false;
		}
	};

	private int progress;

	public final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return index == 0 ? TileNeutronCollector.this.progress : 0;
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				TileNeutronCollector.this.progress = value;
			}
		}

		@Override
		public int getCount() {
			return 1;
		}
	};

	public TileNeutronCollector(BlockPos pos, BlockState state) {
		super(AvaritiaBlocks.NEUTRON_COLLECTOR_TILE, pos, state);
	}

	public int getProgress() {
		return this.progress;
	}

	@Override
	public int getContainerSize() {
		return this.result.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return this.result.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.result.getItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return this.result.removeItem(slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return this.result.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.result.setItem(slot, stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		this.result.clearContent();
		this.setChanged();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction face) {
		return face == Direction.DOWN ? OUTPUT_SLOTS : NO_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
		return slot == 0 && face == Direction.DOWN;
	}

	@Override
	protected boolean canWork() {
		ItemStack itemStack = this.result.getItem(0);
		return itemStack.isEmpty()
				|| (itemStack.is(AvaritiaItems.NEUTRON_PILE) && itemStack.getCount() < itemStack.getMaxStackSize());
	}

	@Override
	protected void doWork() {
		if (++this.progress >= AvaritiaConfigs.productionTicks) {
			ItemStack itemStack = this.result.getItem(0);
			if (itemStack.isEmpty()) {
				this.result.setItem(0, new ItemStack(AvaritiaItems.NEUTRON_PILE));
			} else {
				itemStack.setCount(itemStack.getCount() + 1);
			}
			this.progress = 0;
		}
	}

	@Override
	protected void onWorkStopped() {
		this.progress = 0;
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.progress = input.getIntOr("progress", 0);
		ContainerHelper.loadAllItems(input, this.result.getItems());
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("progress", this.progress);
		ContainerHelper.saveAllItems(output, this.result.getItems());
	}
}

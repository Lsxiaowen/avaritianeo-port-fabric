package net.byAqua3.avaritia.tile;

import net.byAqua3.avaritia.item.ItemJsonSingularity;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.loader.AvaritiaSingularities;
import net.byAqua3.avaritia.recipe.RecipeCompressor;
import net.byAqua3.avaritia.singularity.Singularity;
import net.byAqua3.avaritia.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileNeutroniumCompressor extends TileMachine implements WorldlyContainer {

	public static int CONSUME_TICKS = 1;

	public final SimpleContainer matrix = new SimpleContainer(1) {
		@Override
		public void setChanged() {
			TileNeutroniumCompressor.this.setChanged();
		}
	};

	public final SimpleContainer result = new SimpleContainer(1) {
		@Override
		public void setChanged() {
			TileNeutroniumCompressor.this.setChanged();
		}
	};

	private ItemStack targetStack = ItemStack.EMPTY;

	private int compressionTarget;

	private int consumptionProgress;

	private int compressionProgress;

	public final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			switch (index) {
			case 0:
				return TileNeutroniumCompressor.this.resolvedCompressionTarget();
			case 1:
				return TileNeutroniumCompressor.this.consumptionProgress;
			case 2:
				return TileNeutroniumCompressor.this.compressionProgress;
			case 3:
				return BuiltInRegistries.ITEM.getId(TileNeutroniumCompressor.this.targetStack.getItem());
			case 4:
				if(TileNeutroniumCompressor.this.targetStack.getItem() instanceof ItemJsonSingularity) {
				   String singularityId = TileNeutroniumCompressor.this.targetStack.getOrDefault(AvaritiaDataComponents.SINGULARITY_ID, "null");
				   Singularity singularity = AvaritiaSingularities.getInstance().getSingularity(singularityId);
				   if (singularity != null) {
					   return AvaritiaSingularities.getInstance().getSingularities().indexOf(singularity);
				   }
				}
				return -1;
			default:
				return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0:
				TileNeutroniumCompressor.this.compressionTarget = value;
				break;
			case 1:
				TileNeutroniumCompressor.this.consumptionProgress = value;
				break;
			case 2:
				TileNeutroniumCompressor.this.compressionProgress = value;
				break;
			}
		}

		@Override
		public int getCount() {
			return 5;
		}
	};

	public TileNeutroniumCompressor(BlockPos pos, BlockState state) {
		super(AvaritiaBlocks.COMPRESSOR_TILE, pos, state);
	}

	public ItemStack getTargetStack() {
		return this.targetStack;
	}

	/**
	 * Lazily restores {@code compressionTarget} after a reload.
	 *
	 * 26.1's {@code BlockEntity} has no {@code onLoad()} hook, so the sentinel set
	 * in {@link #loadAdditional} was never repaired: the target stayed -1, every
	 * {@code compressionProgress >= -1} check passed instantly and a single input
	 * item produced a singularity. The value is now persisted, and this method
	 * repairs old saves that still carry the -1 sentinel.
	 */
	private int resolvedCompressionTarget() {
		if (this.compressionTarget > 0) {
			return this.compressionTarget;
		}
		if (this.level != null && !this.level.isClientSide() && !this.targetStack.isEmpty()) {
			RecipeCompressor recipe = RecipeUtils.getCompressorRecipeFromResult(this.level, this.targetStack);
			if (recipe != null) {
				this.compressionTarget = recipe.cost;
				return this.compressionTarget;
			}
			this.targetStack = ItemStack.EMPTY;
			this.compressionProgress = 0;
		}
		return 0;
	}

	@Override
	protected boolean canWork() {
		ItemStack matrixItem = this.matrix.getItem(0);
		if (matrixItem.isEmpty()) {
			return false;
		}
		RecipeCompressor recipe = RecipeUtils.getCompressorRecipe(this.level, matrixItem);

		if (recipe == null) {
			return false;
		}

		if (!this.targetStack.isEmpty()) {
			return ItemStack.isSameItemSameComponents(recipe.result.create(), this.targetStack);
		}

		ItemStack resultItem = this.result.getItem(0);
		if (resultItem.isEmpty()) {
			return true;
		}
		return ItemStack.isSameItemSameComponents(recipe.result.create(), resultItem)
				&& resultItem.getCount() < resultItem.getMaxStackSize();
	}

	@Override
	protected void doWork() {
		if (this.targetStack.isEmpty()) {
			ItemStack matrixItem = this.matrix.getItem(0);
			RecipeCompressor recipe = RecipeUtils.getCompressorRecipe(this.level, matrixItem);
			
			if (recipe != null) {
				this.targetStack = recipe.result.create();
				this.compressionTarget = recipe.cost;
			}
		}

		this.consumptionProgress++;
		if (this.consumptionProgress >= CONSUME_TICKS) {
			this.consumptionProgress = 0;
			ItemStack matrixItem = this.matrix.getItem(0);
			matrixItem.setCount(matrixItem.getCount() - 1);
			this.compressionProgress++;
		}
		int target = this.resolvedCompressionTarget();
		// Guard against a non-positive target: comparing progress against it would
		// always succeed and hand out a singularity after a single item.
		if (target > 0 && this.compressionProgress >= target) {
			this.compressionProgress = 0;
			ItemStack resultItem = this.result.getItem(0);
			if (resultItem.isEmpty()) {
				this.result.setItem(0, this.targetStack.copy());
			} else {
				resultItem.setCount(resultItem.getCount() + 1);
			}
			this.targetStack = ItemStack.EMPTY;
			this.compressionTarget = 0;
		}
	}

	@Override
	protected void onWorkStopped() {
		this.consumptionProgress = 0;
	}

	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public boolean isEmpty() {
		return this.matrix.isEmpty() && this.result.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot == 0) {
			return this.matrix.getItem(0);
		} else {
			return this.result.getItem(0);
		}
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (slot == 0) {
			return this.matrix.removeItem(0, amount);
		} else if (slot == 1) {
			return this.result.removeItem(0, amount);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot == 0) {
			return this.matrix.removeItemNoUpdate(0);
		} else if (slot == 1) {
			return this.result.removeItemNoUpdate(0);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot == 0) {
			this.matrix.setItem(0, stack);
		} else if (slot == 1) {
			this.result.setItem(0, stack);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		BlockPos blockPos = this.getBlockPos();
		return this.getLevel().getBlockEntity(blockPos) == this && player.distanceToSqr(blockPos.getX() + 0.5D,
				blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent() {

	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.UP) {
			return new int[] { 0 };
		} else {
			return new int[] { 1 };
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (stack.isEmpty()) {
			return false;
		}
		if (index == 0) {
			if (targetStack.isEmpty()) {
				return true;
			}

			RecipeCompressor recipe = RecipeUtils.getCompressorRecipe(this.level, stack);

			if (recipe == null) {
				return false;
			}

			if (!this.targetStack.isEmpty()) {
				return ItemStack.isSameItemSameComponents(recipe.result.create(), this.targetStack);
			}
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (index == 1 && direction != Direction.UP) {
			return true;
		}
		return false;
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.consumptionProgress = input.getIntOr("consumptionProgress", 0);
		this.compressionProgress = input.getIntOr("compressionProgress", 0);
		// Persisted now; older saves lack the key and fall back to the -1 sentinel,
		// which resolvedCompressionTarget() repairs on first use.
		this.compressionTarget = input.getIntOr("compressionTarget", -1);
		this.targetStack = input.read("target", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.matrix.getItems());
		ContainerHelper.loadAllItems(input, this.result.getItems());
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("consumptionProgress", this.consumptionProgress);
		output.putInt("compressionProgress", this.compressionProgress);
		output.putInt("compressionTarget", this.compressionTarget);
		if (!this.targetStack.isEmpty()) {
			output.store("target", ItemStack.CODEC, this.targetStack);
		}
		ContainerHelper.saveAllItems(output, this.matrix.getItems());
		ContainerHelper.saveAllItems(output, this.result.getItems());
	}
}


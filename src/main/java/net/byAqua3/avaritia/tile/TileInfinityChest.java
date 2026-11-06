package net.byAqua3.avaritia.tile;

import java.util.List;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.component.ClusterContainerContents;
import net.byAqua3.avaritia.inventory.MenuInfinityChest;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileInfinityChest extends BlockEntity implements LidBlockEntity {

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHEST_OPEN,
					SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHEST_CLOSE,
					SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
			TileInfinityChest.this.signalOpenCount(level, pos, state, count, openCount);
		}

		@Override
		public boolean isOwnContainer(Player player) {
			return player.containerMenu instanceof MenuInfinityChest menu
					&& menu.getTile() == TileInfinityChest.this;
		}
	};

	private final ChestLidController chestLidController = new ChestLidController();

	public final SimpleContainer chest = new SimpleContainer(300) {
		@Override
		public void setChanged() {
			TileInfinityChest.this.setChanged();
		}

		@Override
		public int getMaxStackSize() {
			return Integer.MAX_VALUE;
		}

		@Override
		public int getMaxStackSize(ItemStack stack) {
			int maxStackSize = Math.max(stack.getCount(), AvaritiaConfigs.maxStackSize);
			return Math.min(this.getMaxStackSize(), maxStackSize);
		}

		@Override
		public void fillStackedContents(StackedItemContents stackedContents) {
			for (ItemStack itemStack : this.getItems()) {
				stackedContents.accountStack(itemStack);
			}
		}

		@Override
		public void startOpen(ContainerUser user) {
			if (!TileInfinityChest.this.remove && !user.getLivingEntity().isSpectator()) {
				TileInfinityChest.this.openersCounter.incrementOpeners(user.getLivingEntity(),
						TileInfinityChest.this.getLevel(), TileInfinityChest.this.getBlockPos(),
						TileInfinityChest.this.getBlockState(), user.getContainerInteractionRange());
			}
		}

		@Override
		public void stopOpen(ContainerUser user) {
			if (!TileInfinityChest.this.remove && !user.getLivingEntity().isSpectator()) {
				TileInfinityChest.this.openersCounter.decrementOpeners(user.getLivingEntity(),
						TileInfinityChest.this.getLevel(), TileInfinityChest.this.getBlockPos(),
						TileInfinityChest.this.getBlockState());
			}
		}
	};

	public TileInfinityChest(BlockPos pos, BlockState state) {
		super(AvaritiaBlocks.INFINITY_CHEST_TILE, pos, state);
	}

	public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, TileInfinityChest blockEntity) {
		blockEntity.chestLidController.tickLid();
	}

	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int eventId, int eventParam) {
		level.blockEvent(pos, state.getBlock(), 1, eventParam);
	}

	public void recheckOpen() {
		if (!this.remove) {
			this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public float getOpenNess(float partialTicks) {
		return this.chestLidController.getOpenness(partialTicks);
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == 1) {
			this.chestLidController.shouldBeOpen(type > 0);
			return true;
		}
		return super.triggerEvent(id, type);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		ContainerHelper.loadAllItems(input, this.chest.getItems());
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.chest.getItems());
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);

		NonNullList<ItemStack> itemStacks = NonNullList.withSize(300, ItemStack.EMPTY);
		ClusterContainerContents contents = components.getOrDefault(AvaritiaDataComponents.CLUSTER_CONTAINER, ClusterContainerContents.EMPTY);
		contents.copyInto(itemStacks);

		for (int i = 0; i < itemStacks.size(); i++) {
			this.chest.getItems().set(i, itemStacks.get(i));
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);
		builder.set(AvaritiaDataComponents.CLUSTER_CONTAINER, ClusterContainerContents.fromItems(this.chest.getItems()));
	}
}

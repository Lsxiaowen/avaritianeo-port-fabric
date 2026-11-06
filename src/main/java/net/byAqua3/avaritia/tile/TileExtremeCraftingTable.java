package net.byAqua3.avaritia.tile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.byAqua3.avaritia.component.ClusterContainerContents;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;



import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileExtremeCraftingTable extends BlockEntity {

	public final SimpleContainer matrix = new SimpleContainer(81) {
		@Override
		public void setChanged() {
			TileExtremeCraftingTable.this.setChanged();
		}
	};

	public final SimpleContainer result = new SimpleContainer(1) {
		@Override
		public void setChanged() {
			TileExtremeCraftingTable.this.setChanged();
		}
	};

	public TileExtremeCraftingTable(BlockPos pos, BlockState state) {
		super(AvaritiaBlocks.EXTREME_CRAFTING_TABLE_TILE, pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		ContainerHelper.loadAllItems(input, this.matrix.getItems());
		ContainerHelper.loadAllItems(input, this.result.getItems());
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.matrix.getItems());
		ContainerHelper.saveAllItems(output, this.result.getItems());
	}

	@Override
	protected void applyImplicitComponents(net.minecraft.core.component.DataComponentGetter componentInput) {
		super.applyImplicitComponents(componentInput);

		NonNullList<ItemStack> itemStacks = NonNullList.withSize(82, ItemStack.EMPTY);
		componentInput.getOrDefault(AvaritiaDataComponents.CLUSTER_CONTAINER, ClusterContainerContents.EMPTY).copyInto(itemStacks);

		if (!itemStacks.isEmpty()) {
			for (int i = 0; i < itemStacks.size(); i++) {
				if (i < itemStacks.size() - 1) {
					this.matrix.getItems().set(i, itemStacks.get(i));
				} else {
					this.result.getItems().set(0, itemStacks.get(i));
				}
			}
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);

		List<ItemStack> itemStacks = Stream.concat(this.matrix.getItems().stream(), this.result.getItems().stream()).collect(Collectors.toList());
		builder.set(AvaritiaDataComponents.CLUSTER_CONTAINER, ClusterContainerContents.fromItems(itemStacks));
	}
}


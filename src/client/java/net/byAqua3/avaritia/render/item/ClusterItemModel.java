package net.byAqua3.avaritia.render.item;

import org.joml.Matrix4fc;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.byAqua3.avaritia.item.ItemMatterCluster;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record ClusterItemModel(ItemModel empty, ItemModel full) implements ItemModel {
	@Override
	public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver,
			ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed) {
		(ItemMatterCluster.getClusterCount(ItemMatterCluster.getClusterItems(stack)) == ItemMatterCluster.CAPACITY ? full : empty)
				.update(state, stack, resolver, context, level, owner, seed);
	}

	public record Unbaked(ItemModel.Unbaked empty, ItemModel.Unbaked full) implements ItemModel.Unbaked {
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemModels.CODEC.fieldOf("empty").forGetter(Unbaked::empty),
				ItemModels.CODEC.fieldOf("full").forGetter(Unbaked::full)).apply(instance, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return CODEC;
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
			empty.resolveDependencies(resolver);
			full.resolveDependencies(resolver);
		}

		@Override
		public ItemModel bake(BakingContext context, Matrix4fc transformation) {
			return new ClusterItemModel(empty.bake(context, transformation), full.bake(context, transformation));
		}
	}
}

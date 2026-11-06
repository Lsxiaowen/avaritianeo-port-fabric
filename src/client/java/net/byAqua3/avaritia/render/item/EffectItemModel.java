package net.byAqua3.avaritia.render.item;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.byAqua3.avaritia.item.ItemMatterCluster;
import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.mixin.ItemLayerAccessor;
import net.byAqua3.avaritia.mixin.ItemStackRenderStateAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class EffectItemModel implements ItemModel {
	private final ItemModel base;
	private final ModelRenderProperties properties;
	private final Matrix4fc transformation;
	private final ItemEffectsRenderer renderer;
	private final boolean cosmic;
	private final boolean cluster;
	private final boolean pulse;
	private final int haloType;

	private EffectItemModel(ItemModel base, ModelRenderProperties properties, Matrix4fc transformation,
			ItemEffectsRenderer renderer, boolean cosmic, boolean cluster, boolean pulse, int haloType) {
		this.base = base;
		this.properties = properties;
		this.transformation = new Matrix4f(transformation);
		this.renderer = renderer;
		this.cosmic = cosmic;
		this.cluster = cluster;
		this.pulse = pulse;
		this.haloType = haloType;
	}

	@Override
	public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver,
			ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed) {
		Minecraft minecraft = Minecraft.getInstance();
		float yaw = context == ItemDisplayContext.GUI || minecraft.player == null ? 0 : minecraft.player.getYRot() * Mth.DEG_TO_RAD;
		float pitch = context == ItemDisplayContext.GUI || minecraft.player == null ? 0 : -minecraft.player.getXRot() * Mth.DEG_TO_RAD;
		float opacity = 1;
		if (cluster && stack.has(AvaritiaDataComponents.CLUSTER_CONTAINER)) {
			int count = ItemMatterCluster.getClusterCount(ItemMatterCluster.getClusterItems(stack));
			if (count <= ItemMatterCluster.CAPACITY) {
				opacity = Mth.clamp((float) count / ItemMatterCluster.CAPACITY, 0, 1);
			}
		}
		ItemEffectsRenderer.Arguments arguments = new ItemEffectsRenderer.Arguments(context == ItemDisplayContext.GUI,
				new AvaritiaShaders.Parameters(AvaritiaShaders.time(), yaw, pitch, context == ItemDisplayContext.GUI ? 100 : 1, opacity));
		if (!cosmic && context == ItemDisplayContext.GUI) {
			addEffectLayer(state, context, arguments);
			state.setOversizedInGui(true);
		}
		ItemStackRenderStateAccessor access = (ItemStackRenderStateAccessor) state;
		int first = access.avaritia$getActiveLayerCount();
		base.update(state, stack, resolver, context, level, owner, seed);
		if (context == ItemDisplayContext.GUI && (pulse || haloType == 0)) {
			float scale = ThreadLocalRandom.current().nextFloat(0.95F, 1.05F);
			float offset = (1.0F - scale) / 2.0F;
			for (int index = first; index < access.avaritia$getActiveLayerCount(); index++) {
				ItemStackRenderState.LayerRenderState layer = access.avaritia$getLayers()[index];
				Matrix4f transform = new Matrix4f(((ItemLayerAccessor) layer).avaritia$getLocalTransform());
				layer.setLocalTransform(transform.scale(scale, scale, 1.0001F).translate(offset, offset, 0));
			}
			state.appendModelIdentityElement(scale);
		}
		if (cosmic && opacity > 0) {
			addEffectLayer(state, context, arguments);
		}
		state.setAnimated();
		state.appendModelIdentityElement(arguments);
	}

	private void addEffectLayer(ItemStackRenderState state, ItemDisplayContext context, ItemEffectsRenderer.Arguments arguments) {
		ItemStackRenderState.LayerRenderState layer = state.newLayer();
		properties.applyToLayer(layer, context);
		layer.setLocalTransform(transformation);
		layer.setExtents(renderer::extents);
		layer.setupSpecialModel(renderer, arguments);
	}

	public record Unbaked(ItemModel.Unbaked base, Identifier model, Optional<Identifier> mask,
			int haloType, float alpha, boolean pulse, boolean cluster) implements ItemModel.Unbaked {
		public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemModels.CODEC.fieldOf("base").forGetter(Unbaked::base),
				Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model),
				Identifier.CODEC.optionalFieldOf("mask").forGetter(Unbaked::mask),
				Codec.intRange(-1, 2).optionalFieldOf("halo_type", -1).forGetter(Unbaked::haloType),
				Codec.floatRange(0, 1).optionalFieldOf("alpha", 1.0F).forGetter(Unbaked::alpha),
				Codec.BOOL.optionalFieldOf("pulse", false).forGetter(Unbaked::pulse),
				Codec.BOOL.optionalFieldOf("cluster", false).forGetter(Unbaked::cluster)).apply(instance, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return CODEC;
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
			base.resolveDependencies(resolver);
			resolver.markDependency(model);
			mask.ifPresent(resolver::markDependency);
		}

		@Override
		public ItemModel bake(BakingContext context, Matrix4fc transformation) {
			ResolvedModel source = context.blockModelBaker().getModel(model);
			ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(context.blockModelBaker(), source, source.getTopTextureSlots());
			List<BakedQuad> quads = mask.map(id -> {
				ResolvedModel overlay = context.blockModelBaker().getModel(id);
				return overlay.bakeTopGeometry(overlay.getTopTextureSlots(), context.blockModelBaker(), BlockModelRotation.IDENTITY).getAll();
			}).orElseGet(List::of);
			ItemEffectsRenderer renderer = new ItemEffectsRenderer(quads, haloType, alpha);
			return new EffectItemModel(base.bake(context, transformation), properties, transformation, renderer,
					mask.isPresent(), cluster, pulse, haloType);
		}
	}
}

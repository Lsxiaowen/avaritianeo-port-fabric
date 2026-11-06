package net.byAqua3.avaritia.loader;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.compat.iris.IrisCompat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

public final class AvaritiaRenderTypes {
	private static final RenderPipeline COSMIC_ITEM = cosmicPipeline("cosmic", CompareOp.EQUAL, true);
	private static final RenderPipeline COSMIC_ITEM_IRIS = cosmicPipeline("cosmic_iris", CompareOp.LESS_THAN_OR_EQUAL, false);
	private static final RenderPipeline COSMIC_HAND_IRIS = cosmicPipeline("cosmic_hand_iris", CompareOp.LESS_THAN_OR_EQUAL, false);
	private static final RenderPipeline COSMIC_ARMOR = cosmicPipeline("cosmic_armor", CompareOp.LESS_THAN_OR_EQUAL, false);
	private static final RenderPipeline COSMIC_ARMOR_IRIS = cosmicPipeline("cosmic_armor_iris", CompareOp.LESS_THAN_OR_EQUAL, false);
	private static final RenderPipeline GLOW = texturedPipeline("glow", BlendFunction.GLINT, false, true);
	private static final RenderPipeline GLOW_IRIS = texturedPipeline("glow_iris", BlendFunction.GLINT, false, true);
	private static final RenderPipeline WING_GLOW = texturedPipeline("wing_glow", BlendFunction.LIGHTNING, false, true);
	private static final RenderPipeline HALO = texturedPipeline("halo", BlendFunction.TRANSLUCENT, false, true);
	private static final RenderPipeline VOID_PIPELINE = texturedPipeline("void", null, true, true);
	private static final RenderPipeline VOID_HALO = texturedPipeline("void_halo", BlendFunction.TRANSLUCENT, false, true);
	private static final Map<Identifier, RenderType> GLOWS = new ConcurrentHashMap<>();
	private static final Map<Identifier, RenderType> IRIS_GLOWS = new ConcurrentHashMap<>();
	private static final Map<Identifier, RenderType> WING_GLOWS = new ConcurrentHashMap<>();
	private static final Map<Identifier, RenderType> HALOS = new ConcurrentHashMap<>();
	private static final Map<Identifier, RenderType> VOIDS = new ConcurrentHashMap<>();
	private static final Map<Identifier, RenderType> VOID_HALOS = new ConcurrentHashMap<>();
	private static final Map<RenderPipeline, CosmicTypes> COSMIC_TYPES = new IdentityHashMap<>();

	private static final class CosmicTypes {
		private final List<RenderType> types = new ArrayList<>();
		private final Map<AvaritiaShaders.Parameters, RenderType> frame = new HashMap<>();
		private int used;
	}

	private AvaritiaRenderTypes() {
	}

	public static void initialize() {
	}

	private static RenderPipeline.Builder builder(String name) {
		return RenderPipeline.builder().withLocation(Avaritia.id("pipeline/" + name))
				.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
				.withUniform("Projection", UniformType.UNIFORM_BUFFER)
				.withUniform("Fog", UniformType.UNIFORM_BUFFER).withSampler("Sampler0")
				.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS).withCull(false);
	}

	private static RenderPipeline cosmicPipeline(String name, CompareOp depthTest, boolean writeDepth) {
		RenderPipeline.Builder builder = builder(name).withVertexShader(Avaritia.id("core/cosmic"))
				.withFragmentShader(Avaritia.id("core/cosmic")).withSampler("Sampler2").withSampler("Sampler3")
				.withUniform("CosmicParameters", UniformType.UNIFORM_BUFFER)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.withDepthStencilState(new DepthStencilState(depthTest, writeDepth,
						name.endsWith("_iris") ? -1.0F : 0.0F, name.endsWith("_iris") ? -10.0F : 0.0F));
		if (name.equals("cosmic_hand_iris")) builder.withShaderDefine("IRIS_HAND");
		return RenderPipelines.register(builder.build());
	}

	private static RenderPipeline texturedPipeline(String name, BlendFunction blend, boolean writeDepth, boolean depthTest) {
		RenderPipeline.Builder builder = builder(name).withVertexShader("core/entity").withFragmentShader("core/entity")
				.withShaderDefine("EMISSIVE").withShaderDefine("NO_OVERLAY").withShaderDefine("NO_CARDINAL_LIGHTING")
				.withColorTargetState(new ColorTargetState(Optional.ofNullable(blend), ColorTargetState.WRITE_ALL));
		if (blend == BlendFunction.GLINT) builder.withShaderDefine("ALPHA_CUTOUT", 0.1F);
		if (depthTest) {
			builder.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, writeDepth,
					name.endsWith("_iris") ? -1.0F : 0.0F, name.endsWith("_iris") ? -10.0F : 0.0F));
		} else {
			builder.withDepthStencilState(Optional.empty());
		}
		return RenderPipelines.register(builder.build());
	}

	public static boolean isCosmicItem(RenderType type) {
		return type.pipeline() == COSMIC_ITEM || type.pipeline() == COSMIC_ITEM_IRIS || type.pipeline() == COSMIC_HAND_IRIS;
	}

	public static RenderType cosmic(AvaritiaShaders.Parameters parameters, boolean armor) {
		boolean shaders = IrisCompat.isShaderPackInUse();
		RenderPipeline pipeline = armor ? (shaders ? COSMIC_ARMOR_IRIS : COSMIC_ARMOR)
				: shaders ? (IrisCompat.isRenderingHand() ? COSMIC_HAND_IRIS : COSMIC_ITEM_IRIS) : COSMIC_ITEM;
		CosmicTypes pool = COSMIC_TYPES.computeIfAbsent(pipeline, ignored -> new CosmicTypes());
		RenderType type = pool.frame.get(parameters);
		if (type != null) return type;
		if (pool.used < pool.types.size()) {
			type = pool.types.get(pool.used);
		} else {
			type = createCosmicType(pipeline, armor, shaders);
			pool.types.add(type);
		}
		pool.used++;
		pool.frame.put(parameters, type);
		AvaritiaShaders.register(type, parameters);
		return type;
	}

	private static RenderType createCosmicType(RenderPipeline pipeline, boolean armor, boolean shaders) {
		RenderSetup.RenderSetupBuilder setup = RenderSetup.builder(pipeline)
				.withTexture("Sampler0", armor ? TextureAtlas.LOCATION_BLOCKS : TextureAtlas.LOCATION_ITEMS)
				.withTexture("Sampler3", TextureAtlas.LOCATION_BLOCKS).useLightmap().bufferSize(1536);
		if (armor || shaders) {
			setup.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING);
		}
		return RenderType.create(armor ? "avaritia_cosmic_armor" : "avaritia_cosmic", setup.createRenderSetup());
	}

	static void endFrame() {
		for (CosmicTypes pool : COSMIC_TYPES.values()) {
			pool.frame.clear();
			pool.used = 0;
		}
	}

	static void close() {
		COSMIC_TYPES.clear();
	}

	private static RenderType texture(String name, RenderPipeline pipeline, Identifier texture, boolean offset) {
		RenderSetup.RenderSetupBuilder setup = RenderSetup.builder(pipeline).withTexture("Sampler0", texture).bufferSize(1536);
		if (offset) {
			setup.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING);
		}
		return RenderType.create(name, setup.createRenderSetup());
	}

	public static RenderType glow(Identifier texture) {
		boolean shaders = IrisCompat.isShaderPackInUse();
		return (shaders ? IRIS_GLOWS : GLOWS).computeIfAbsent(texture,
				id -> texture("avaritia_glow", shaders ? GLOW_IRIS : GLOW, id, true));
	}

	public static RenderType wingGlow(Identifier texture) {
		return WING_GLOWS.computeIfAbsent(texture, id -> texture("avaritia_wing_glow", WING_GLOW, id, true));
	}

	public static RenderType halo(Identifier texture) {
		return HALOS.computeIfAbsent(texture, id -> texture("avaritia_halo", HALO, id, false));
	}

	public static RenderType voidBody(Identifier texture) {
		return VOIDS.computeIfAbsent(texture, id -> texture("avaritia_void", VOID_PIPELINE, id, false));
	}

	public static RenderType voidHalo(Identifier texture) {
		return VOID_HALOS.computeIfAbsent(texture, id -> texture("avaritia_void_halo", VOID_HALO, id, false));
	}
}

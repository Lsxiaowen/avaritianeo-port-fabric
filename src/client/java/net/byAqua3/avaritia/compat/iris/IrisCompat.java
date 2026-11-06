package net.byAqua3.avaritia.compat.iris;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Supplier;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.byAqua3.avaritia.Avaritia;
import net.fabricmc.loader.api.FabricLoader;

public final class IrisCompat {
	private IrisCompat() {
	}

	private record Bridge(MethodHandle pipeline, Class<?> pipelineClass, MethodHandle overrides,
			MethodHandle bind, MethodHandle shadows, MethodHandle hand, MethodHandle getBypass, MethodHandle setBypass) {
		private static Bridge load() {
			if (!FabricLoader.getInstance().isModLoaded("iris")) {
				return null;
			}
			try {
				ClassLoader loader = IrisCompat.class.getClassLoader();
				Class<?> iris = Class.forName("net.irisshaders.iris.Iris", false, loader);
				Class<?> manager = Class.forName("net.irisshaders.iris.pipeline.PipelineManager", false, loader);
				Class<?> world = Class.forName("net.irisshaders.iris.pipeline.WorldRenderingPipeline", false, loader);
				Class<?> pipeline = Class.forName("net.irisshaders.iris.pipeline.IrisRenderingPipeline", false, loader);
				Class<?> shadows = Class.forName("net.irisshaders.iris.shadows.ShadowRenderingState", false, loader);
				Class<?> immediate = Class.forName("net.irisshaders.iris.vertices.ImmediateState", false, loader);
				Class<?> hand = Class.forName("net.irisshaders.iris.pathways.HandRenderer", false, loader);
				var lookup = MethodHandles.publicLookup();
				MethodHandle getManager = lookup.findStatic(iris, "getPipelineManager", MethodType.methodType(manager));
				MethodHandle getPipeline = lookup.findVirtual(manager, "getPipelineNullable", MethodType.methodType(world));
				return new Bridge(MethodHandles.filterReturnValue(getManager, getPipeline)
						.asType(MethodType.methodType(Object.class)), pipeline,
						lookup.findVirtual(pipeline, "shouldOverrideShaders", MethodType.methodType(boolean.class))
								.asType(MethodType.methodType(boolean.class, Object.class)),
						lookup.findVirtual(pipeline, "bindDefault", MethodType.methodType(void.class))
								.asType(MethodType.methodType(void.class, Object.class)),
						lookup.findStatic(shadows, "areShadowsCurrentlyBeingRendered", MethodType.methodType(boolean.class)),
						MethodHandles.filterReturnValue(lookup.findStaticGetter(hand, "INSTANCE", hand),
								lookup.findVirtual(hand, "isActive", MethodType.methodType(boolean.class))),
						lookup.findStaticGetter(immediate, "bypass", boolean.class),
						lookup.findStaticSetter(immediate, "bypass", boolean.class));
			} catch (ReflectiveOperationException exception) {
				Avaritia.LOGGER.error("Unsupported Iris rendering API; Avaritia shader compatibility is disabled", exception);
				return null;
			}
		}
	}

	private static final class Holder {
		private static final Bridge BRIDGE = Bridge.load();
	}

	public static boolean isCustomPipeline(RenderPipeline pipeline) {
		return pipeline != null && Avaritia.MODID.equals(pipeline.getLocation().getNamespace());
	}

	public static boolean isShaderPackInUse() {
		if (Holder.BRIDGE == null) return false;
		try {
			Object current = Holder.BRIDGE.pipeline.invokeExact();
			return Holder.BRIDGE.pipelineClass.isInstance(current);
		} catch (Throwable exception) {
			throw new IllegalStateException("Cannot query the Iris shader pack", exception);
		}
	}

	public static boolean isRenderingHand() {
		if (Holder.BRIDGE == null) return false;
		try {
			return (boolean) Holder.BRIDGE.hand.invokeExact();
		} catch (Throwable exception) {
			throw new IllegalStateException("Cannot query Iris hand rendering", exception);
		}
	}

	public static boolean skipShadow(RenderPipeline pipeline) {
		if (!isCustomPipeline(pipeline) || Holder.BRIDGE == null) {
			return false;
		}
		try {
			return (boolean) Holder.BRIDGE.shadows.invokeExact();
		} catch (Throwable exception) {
			throw new IllegalStateException("Cannot query Iris shadow rendering", exception);
		}
	}

	public static void bindRenderTarget(RenderPipeline pipeline) {
		if (!isCustomPipeline(pipeline) || Holder.BRIDGE == null) {
			return;
		}
		Bridge bridge = Holder.BRIDGE;
		try {
			Object current = bridge.pipeline.invokeExact();
			if (bridge.pipelineClass.isInstance(current) && (boolean) bridge.overrides.invokeExact(current)
					&& !(boolean) bridge.shadows.invokeExact()) {
				bridge.bind.invokeExact(current);
			}
		} catch (Throwable exception) {
			throw new IllegalStateException("Cannot bind the Iris effect render target", exception);
		}
	}

	public static <T> T withoutShaderOverride(RenderPipeline pipeline, Supplier<T> compile) {
		if (!isCustomPipeline(pipeline) || Holder.BRIDGE == null) {
			return compile.get();
		}
		Bridge bridge = Holder.BRIDGE;
		boolean previous;
		try {
			previous = (boolean) bridge.getBypass.invokeExact();
			bridge.setBypass.invokeExact(true);
		} catch (Throwable exception) {
			throw new IllegalStateException("Cannot preserve the Avaritia shader program", exception);
		}
		try {
			return compile.get();
		} finally {
			try {
				bridge.setBypass.invokeExact(previous);
			} catch (Throwable exception) {
				throw new IllegalStateException("Cannot restore Iris shader overrides", exception);
			}
		}
	}
}

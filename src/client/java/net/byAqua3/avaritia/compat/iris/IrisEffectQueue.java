package net.byAqua3.avaritia.compat.iris;

import java.util.ArrayDeque;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;

public final class IrisEffectQueue {
	private static final ArrayDeque<Draw> DRAWS = new ArrayDeque<>();
	private static boolean collecting;

	private IrisEffectQueue() {
	}

	private record Draw(PreparedRenderType type, StagedVertexBuffer.ExecuteInfo geometry, GpuBufferSlice projection,
			ProjectionType projectionType, GpuBufferSlice fog, GpuBufferSlice lights) {
	}

	public static void beginFrame() {
		clear();
		collecting = true;
	}

	public static boolean defer(PreparedRenderType type, StagedVertexBuffer.ExecuteInfo geometry) {
		if (!collecting || !IrisCompat.isCustomPipeline(type.pipeline())
				|| RenderSystem.getProjectionType() != ProjectionType.PERSPECTIVE || IrisCompat.skipShadow(type.pipeline())) {
			return false;
		}
		DRAWS.add(new Draw(type, geometry, RenderSystem.getProjectionMatrixBuffer(), RenderSystem.getProjectionType(),
				RenderSystem.getShaderFog(), RenderSystem.getShaderLights()));
		return true;
	}

	public static void flush() {
		collecting = false;
		if (DRAWS.isEmpty()) return;
		GpuBufferSlice projection = RenderSystem.getProjectionMatrixBuffer();
		ProjectionType projectionType = RenderSystem.getProjectionType();
		GpuBufferSlice fog = RenderSystem.getShaderFog();
		GpuBufferSlice lights = RenderSystem.getShaderLights();
		try {
			while (!DRAWS.isEmpty()) {
				Draw draw = DRAWS.removeFirst();
				RenderSystem.setProjectionMatrix(draw.projection, draw.projectionType);
				RenderSystem.setShaderFog(draw.fog);
				RenderSystem.setShaderLights(draw.lights);
				draw.type.drawFromBuffer(draw.geometry);
			}
		} finally {
			RenderSystem.setProjectionMatrix(projection, projectionType);
			RenderSystem.setShaderFog(fog);
			RenderSystem.setShaderLights(lights);
			clear();
		}
	}

	public static void clear() {
		collecting = false;
		DRAWS.clear();
	}
}

package net.byAqua3.avaritia.compat.iris;

import java.util.ArrayDeque;
import java.nio.ByteBuffer;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.byAqua3.avaritia.mixin.MeshDataAccessor;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public final class IrisEffectQueue {
	private static final ArrayDeque<Draw> DRAWS = new ArrayDeque<>();
	private static ByteBufferBuilder snapshots;
	private static boolean collecting;

	private IrisEffectQueue() {
	}

	private record Draw(RenderType type, MeshData mesh, Matrix4f modelView, GpuBufferSlice projection,
			ProjectionType projectionType, GpuBufferSlice fog, GpuBufferSlice lights) {
	}

	public static void beginFrame() {
		clear();
		collecting = true;
	}

	public static boolean defer(RenderType type, MeshData mesh) {
		if (!collecting || !IrisCompat.isCustomPipeline(type.pipeline())
				|| RenderSystem.getProjectionType() != ProjectionType.PERSPECTIVE || IrisCompat.skipShadow(type.pipeline())) {
			return false;
		}
		MeshData snapshot = null;
		try {
			snapshot = snapshot(mesh);
			DRAWS.add(new Draw(type, snapshot, new Matrix4f(RenderSystem.getModelViewMatrix()),
					RenderSystem.getProjectionMatrixBuffer(), RenderSystem.getProjectionType(),
					RenderSystem.getShaderFog(), RenderSystem.getShaderLights()));
			return true;
		} catch (RuntimeException | Error failure) {
			if (snapshot != null) snapshot.close();
			throw failure;
		} finally {
			mesh.close();
		}
	}

	private static MeshData snapshot(MeshData source) {
		ByteBufferBuilder.Result vertices = copy(source.vertexBuffer());
		ByteBufferBuilder.Result indices = null;
		try {
			ByteBuffer indexBuffer = source.indexBuffer();
			if (indexBuffer != null) indices = copy(indexBuffer);
			MeshData mesh = new MeshData(vertices, source.drawState());
			((MeshDataAccessor) mesh).avaritia$setIndexBuffer(indices);
			return mesh;
		} catch (RuntimeException | Error failure) {
			vertices.close();
			if (indices != null) indices.close();
			throw failure;
		}
	}

	private static ByteBufferBuilder.Result copy(ByteBuffer source) {
		if (snapshots == null) snapshots = new ByteBufferBuilder(1536);
		int bytes = source.remaining();
		MemoryUtil.memCopy(MemoryUtil.memAddress(source), snapshots.reserve(bytes), bytes);
		return snapshots.build();
	}

	public static void flush() {
		collecting = false;
		if (DRAWS.isEmpty()) return;
		GpuBufferSlice projection = RenderSystem.getProjectionMatrixBuffer();
		ProjectionType projectionType = RenderSystem.getProjectionType();
		GpuBufferSlice fog = RenderSystem.getShaderFog();
		GpuBufferSlice lights = RenderSystem.getShaderLights();
		var modelView = RenderSystem.getModelViewStack();
		modelView.pushMatrix();
		try {
			while (!DRAWS.isEmpty()) {
				Draw draw = DRAWS.removeFirst();
				try {
					modelView.set(draw.modelView);
					RenderSystem.setProjectionMatrix(draw.projection, draw.projectionType);
					RenderSystem.setShaderFog(draw.fog);
					RenderSystem.setShaderLights(draw.lights);
					draw.type.draw(draw.mesh);
				} finally {
					draw.mesh.close();
				}
			}
		} finally {
			modelView.popMatrix();
			RenderSystem.setProjectionMatrix(projection, projectionType);
			RenderSystem.setShaderFog(fog);
			RenderSystem.setShaderLights(lights);
			clear();
		}
	}

	public static void clear() {
		collecting = false;
		while (!DRAWS.isEmpty()) DRAWS.removeFirst().mesh.close();
		if (snapshots != null) snapshots.clear();
	}

	public static void destroy() {
		clear();
		if (snapshots != null) {
			snapshots.close();
			snapshots = null;
		}
	}
}

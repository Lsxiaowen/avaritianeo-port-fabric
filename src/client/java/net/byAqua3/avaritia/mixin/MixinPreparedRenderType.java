package net.byAqua3.avaritia.mixin;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.byAqua3.avaritia.compat.iris.IrisEffectQueue;
import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.byAqua3.avaritia.render.PreparedCosmicUniforms;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PreparedRenderType.class)
public abstract class MixinPreparedRenderType implements PreparedCosmicUniforms {
	@Unique
	private AvaritiaShaders.Parameters avaritia$parameters;
	@Unique
	private GpuBufferSlice avaritia$uniform;

	@Override
	public void avaritia$setCosmicParameters(AvaritiaShaders.Parameters parameters) {
		avaritia$parameters = parameters;
	}

	@Inject(method = "drawFromBuffer(Lnet/minecraft/client/renderer/StagedVertexBuffer$ExecuteInfo;)V", at = @At("HEAD"), cancellable = true)
	private void avaritia$deferIrisEffects(StagedVertexBuffer.ExecuteInfo draw, CallbackInfo callback) {
		if (IrisEffectQueue.defer((PreparedRenderType) (Object) this, draw)) {
			callback.cancel();
		}
	}

	@Inject(method = "drawFromBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/IndexType;III)V", at = @At("HEAD"))
	private void avaritia$uploadUniform(GpuBuffer vertices, GpuBuffer indices, IndexType indexType, int baseVertex,
			int firstIndex, int indexCount, CallbackInfo callback) {
		avaritia$uniform = AvaritiaShaders.upload(avaritia$parameters);
	}

	@Redirect(method = "drawFromBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/IndexType;III)V",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/blaze3d/systems/RenderPass;)V"))
	private void avaritia$bindUniforms(RenderPass pass) {
		RenderSystem.bindDefaultUniforms(pass);
		if (avaritia$uniform != null) pass.setUniform("CosmicParameters", avaritia$uniform);
	}
}

package net.byAqua3.avaritia.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public abstract class MixinRenderType {
	@Unique
	private GpuBufferSlice avaritia$cosmicParameters;

	@Inject(method = "draw", at = @At("HEAD"))
	private void avaritia$prepareUniforms(MeshData mesh, CallbackInfo callbackInfo) {
		avaritia$cosmicParameters = AvaritiaShaders.prepare((RenderType) (Object) this);
	}

	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/blaze3d/systems/RenderPass;)V"))
	private void avaritia$bindUniforms(RenderPass pass) {
		RenderSystem.bindDefaultUniforms(pass);
		AvaritiaShaders.bind(avaritia$cosmicParameters, pass);
	}
}

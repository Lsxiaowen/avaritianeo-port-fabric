package net.byAqua3.avaritia.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.byAqua3.avaritia.compat.iris.IrisCompat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice", priority = 1100)
public abstract class MixinGlDevice {
	@WrapMethod(method = "getOrCompilePipeline")
	private GlRenderPipeline avaritia$preserveEffectShader(RenderPipeline pipeline, Operation<GlRenderPipeline> original) {
		return IrisCompat.withoutShaderOverride(pipeline, () -> original.call(pipeline));
	}
}

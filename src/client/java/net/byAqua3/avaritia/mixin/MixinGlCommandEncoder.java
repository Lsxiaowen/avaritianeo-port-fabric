package net.byAqua3.avaritia.mixin;

import java.util.Collection;
import net.byAqua3.avaritia.compat.iris.IrisCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder", priority = 1100)
public abstract class MixinGlCommandEncoder {
	@Inject(method = "trySetup", at = @At("HEAD"), cancellable = true)
	private void avaritia$skipShadowEffects(@Coerce Object pass, Collection<String> uniforms,
			CallbackInfoReturnable<Boolean> callback) {
		var pipeline = ((GlRenderPassAccessor) pass).avaritia$getPipeline();
		if (pipeline != null && IrisCompat.skipShadow(pipeline.info())) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "trySetup", at = @At("RETURN"))
	private void avaritia$bindEffectTarget(@Coerce Object pass, Collection<String> uniforms,
			CallbackInfoReturnable<Boolean> callback) {
		if (callback.getReturnValueZ()) {
			var pipeline = ((GlRenderPassAccessor) pass).avaritia$getPipeline();
			if (pipeline != null) {
				IrisCompat.bindRenderTarget(pipeline.info());
			}
		}
	}
}

package net.byAqua3.avaritia.mixin;

import net.byAqua3.avaritia.compat.iris.IrisEffectQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.pipeline.IrisRenderingPipeline", remap = false)
public abstract class MixinIrisRenderingPipeline {
	@Inject(method = "beginLevelRendering", at = @At("TAIL"))
	private void avaritia$beginEffects(CallbackInfo callback) {
		IrisEffectQueue.beginFrame();
	}

	@Inject(method = "finalizeLevelRendering", at = @At("TAIL"))
	private void avaritia$finishEffects(CallbackInfo callback) {
		IrisEffectQueue.flush();
	}

	@Inject(method = "destroy", at = @At("HEAD"))
	private void avaritia$clearEffects(CallbackInfo callback) {
		IrisEffectQueue.destroy();
	}
}

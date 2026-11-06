package net.byAqua3.avaritia.mixin;

import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.minecraft.client.renderer.DynamicUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicUniforms.class)
public abstract class MixinDynamicUniforms {
	@Inject(method = "reset", at = @At("TAIL"))
	private void avaritia$endFrame(CallbackInfo callbackInfo) {
		AvaritiaShaders.endFrame();
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void avaritia$close(CallbackInfo callbackInfo) {
		AvaritiaShaders.close();
	}
}

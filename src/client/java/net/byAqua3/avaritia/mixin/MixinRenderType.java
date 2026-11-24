package net.byAqua3.avaritia.mixin;

import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.byAqua3.avaritia.render.PreparedCosmicUniforms;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public abstract class MixinRenderType {
	@Inject(method = "prepare", at = @At("RETURN"))
	private void avaritia$prepareUniforms(CallbackInfoReturnable<PreparedRenderType> callback) {
		var parameters = AvaritiaShaders.parameters((RenderType) (Object) this);
		if (parameters != null) {
			((PreparedCosmicUniforms) (Object) callback.getReturnValue()).avaritia$setCosmicParameters(parameters);
		}
	}
}

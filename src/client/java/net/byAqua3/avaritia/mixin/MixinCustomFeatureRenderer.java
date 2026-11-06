package net.byAqua3.avaritia.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.byAqua3.avaritia.loader.AvaritiaRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CustomFeatureRenderer.class)
public abstract class MixinCustomFeatureRenderer {
	@Redirect(method = "renderTranslucent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;getBuffer(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
	private VertexConsumer avaritia$flushCosmicItemBase(MultiBufferSource.BufferSource buffers, RenderType type) {
		if (AvaritiaRenderTypes.isCosmicItem(type)) {
			buffers.endBatch(Sheets.cutoutItemSheet());
			buffers.endBatch(Sheets.translucentItemSheet());
			buffers.endBatch(Sheets.cutoutBlockItemSheet());
			buffers.endBatch(Sheets.translucentBlockItemSheet());
		}
		return buffers.getBuffer(type);
	}
}

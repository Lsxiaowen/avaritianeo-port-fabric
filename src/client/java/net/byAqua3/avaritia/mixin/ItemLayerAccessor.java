package net.byAqua3.avaritia.mixin;

import org.joml.Matrix4f;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemLayerAccessor {
	@Accessor("localTransform")
	Matrix4f avaritia$getLocalTransform();
}

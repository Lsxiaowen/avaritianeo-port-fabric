package net.byAqua3.avaritia.mixin;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {
	@Accessor("activeLayerCount")
	int avaritia$getActiveLayerCount();

	@Accessor("layers")
	ItemStackRenderState.LayerRenderState[] avaritia$getLayers();
}

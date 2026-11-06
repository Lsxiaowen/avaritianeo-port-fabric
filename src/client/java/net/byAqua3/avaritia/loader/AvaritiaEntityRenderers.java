package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.render.RenderGapingVoid;
import net.byAqua3.avaritia.render.RenderInfinityArrow;
import net.byAqua3.avaritia.render.blockentity.RenderInfinityChest;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class AvaritiaEntityRenderers {

	public static void registerEntityRenderers() {
		EntityRendererRegistry.register(AvaritiaEntities.INFINITY_ARROW, RenderInfinityArrow::new);
		EntityRendererRegistry.register(AvaritiaEntities.ENDEST_PEARL, ThrownItemRenderer::new);
		EntityRendererRegistry.register(AvaritiaEntities.GAPING_VOID, RenderGapingVoid::new);

		BlockEntityRendererRegistry.register(AvaritiaBlocks.INFINITY_CHEST_TILE, RenderInfinityChest::new);
	}
}

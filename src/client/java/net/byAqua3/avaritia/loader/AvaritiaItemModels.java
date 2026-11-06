package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.mixin.ItemModelsAccessor;
import net.byAqua3.avaritia.mixin.ItemTintSourcesAccessor;
import net.byAqua3.avaritia.render.item.ClusterItemModel;
import net.byAqua3.avaritia.render.item.EffectItemModel;
import net.byAqua3.avaritia.render.item.SingularityTint;

public final class AvaritiaItemModels {
	private AvaritiaItemModels() {
	}

	public static void register() {
		ItemModelsAccessor.avaritia$getTypes().put(Avaritia.id("effect"), EffectItemModel.Unbaked.CODEC);
		ItemModelsAccessor.avaritia$getTypes().put(Avaritia.id("cluster"), ClusterItemModel.Unbaked.CODEC);
		ItemTintSourcesAccessor.avaritia$getTypes().put(Avaritia.id("singularity"), SingularityTint.CODEC);
	}
}

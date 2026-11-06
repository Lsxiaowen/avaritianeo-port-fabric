package net.byAqua3.avaritia.loader;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.render.ObjMesh;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class AvaritiaModels extends SimplePreparableReloadListener<ObjMesh> {
	private static volatile ObjMesh voidMesh = new ObjMesh(List.of());

	public static void register() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Avaritia.id("models"), new AvaritiaModels());
	}

	public static ObjMesh voidMesh() {
		return voidMesh;
	}

	@Override
	protected ObjMesh prepare(ResourceManager resources, ProfilerFiller profiler) {
		try (Reader reader = resources.getResourceOrThrow(Avaritia.id("model/model.obj")).openAsReader()) {
			return ObjMesh.read(reader);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to load the Avaritia void model", exception);
		}
	}

	@Override
	protected void apply(ObjMesh mesh, ResourceManager resources, ProfilerFiller profiler) {
		voidMesh = mesh;
	}
}

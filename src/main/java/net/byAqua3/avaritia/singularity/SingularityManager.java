package net.byAqua3.avaritia.singularity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.JsonOps;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.loader.AvaritiaSingularities;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SingularityManager extends SimpleJsonResourceReloadListener<Singularity> {

	private Map<Identifier, Singularity> singularities = ImmutableMap.of();

	public SingularityManager() {
		super(Singularity.CODEC.codec(), FileToIdConverter.json("singularities"));
	}

	@Override
	protected void apply(Map<Identifier, Singularity> object, ResourceManager resourceManager, ProfilerFiller profiler) {
		ImmutableMap.Builder<Identifier, Singularity> builder = ImmutableMap.builder();
		long lastTime = System.currentTimeMillis();

		for (Entry<Identifier, Singularity> entry : object.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}

		this.singularities = builder.build().entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));

		AvaritiaSingularities.getInstance().getSingularities().clear();
		AvaritiaSingularities.getInstance().getSingularities().addAll(this.singularities.values());
		Avaritia.LOGGER.info("Loaded {} singularities took {}ms", this.singularities.size(),
				System.currentTimeMillis() - lastTime);
	}

	public Map<Identifier, Singularity> getSingularities() {
		return this.singularities;
	}
}


package net.byAqua3.avaritia.loader;

import com.mojang.serialization.Codec;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.component.ClusterContainerContents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

public class AvaritiaDataComponents {

	public static final DataComponentType<ClusterContainerContents> CLUSTER_CONTAINER = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Avaritia.id("cluster_container"),
			DataComponentType.<ClusterContainerContents>builder()
					.persistent(ClusterContainerContents.CODEC)
					.networkSynchronized(ClusterContainerContents.STREAM_CODEC)
					.cacheEncoding()
					.build());

	public static final DataComponentType<Boolean> HAMMER = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Avaritia.id("hammer"),
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.cacheEncoding()
					.build());

	public static final DataComponentType<Boolean> DESTROYER = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Avaritia.id("destroyer"),
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.cacheEncoding()
					.build());

	public static final DataComponentType<Boolean> FLY = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Avaritia.id("fly"),
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.cacheEncoding()
					.build());

	public static final DataComponentType<String> SINGULARITY_ID = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Avaritia.id("singularity_id"),
			DataComponentType.<String>builder()
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8)
					.cacheEncoding()
					.build());

	public static void registerDataComponents() {
	}
}

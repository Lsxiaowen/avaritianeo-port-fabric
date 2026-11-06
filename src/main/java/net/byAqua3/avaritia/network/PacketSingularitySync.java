package net.byAqua3.avaritia.network;

import java.util.ArrayList;
import java.util.List;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.loader.AvaritiaSingularities;
import net.byAqua3.avaritia.singularity.Singularity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PacketSingularitySync(List<Singularity> singularities) implements CustomPacketPayload {

	public static final Type<PacketSingularitySync> TYPE = new Type<>(Avaritia.id("singularity_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PacketSingularitySync> STREAM_CODEC =
			StreamCodec.of(PacketSingularitySync::toNetwork, PacketSingularitySync::fromNetwork);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private static PacketSingularitySync fromNetwork(RegistryFriendlyByteBuf buf) {
		int size = buf.readVarInt();
		List<Singularity> singularities = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			singularities.add(Singularity.streamCodec().decode(buf));
		}
		return new PacketSingularitySync(singularities);
	}

	private static void toNetwork(RegistryFriendlyByteBuf buf, PacketSingularitySync packet) {
		buf.writeVarInt(packet.singularities.size());
		for (Singularity singularity : packet.singularities) {
			Singularity.streamCodec().encode(buf, singularity);
		}
	}

	public static void apply(PacketSingularitySync packet) {
		AvaritiaSingularities.getInstance().getSingularities().clear();
		AvaritiaSingularities.getInstance().getSingularities().addAll(packet.singularities);
	}
}

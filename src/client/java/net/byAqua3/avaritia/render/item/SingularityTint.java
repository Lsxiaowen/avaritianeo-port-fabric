package net.byAqua3.avaritia.render.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.byAqua3.avaritia.item.ItemSingularity;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record SingularityTint(int layer) implements ItemTintSource {
	public static final MapCodec<SingularityTint> CODEC = Codec.intRange(0, 1).fieldOf("layer")
			.xmap(SingularityTint::new, SingularityTint::layer);

	@Override
	public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
		if (stack.getItem() instanceof ItemSingularity singularity) {
			return layer == 0 ? singularity.getColor(stack).getRGB() : singularity.getLayerColor(stack).getRGB();
		}
		return -1;
	}

	@Override
	public MapCodec<SingularityTint> type() {
		return CODEC;
	}
}

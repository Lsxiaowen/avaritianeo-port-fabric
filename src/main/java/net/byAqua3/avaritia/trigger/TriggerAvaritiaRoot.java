package net.byAqua3.avaritia.trigger;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class TriggerAvaritiaRoot extends SimpleCriterionTrigger<TriggerAvaritiaRoot.Instance> {

	public static final Codec<TriggerAvaritiaRoot.Instance> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
							.forGetter(TriggerAvaritiaRoot.Instance::player))
					.apply(instance, TriggerAvaritiaRoot.Instance::new));

	@Override
	public Codec<TriggerAvaritiaRoot.Instance> codec() {
		return CODEC;
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, instance -> true);
	}

	public static class Instance implements SimpleCriterionTrigger.SimpleInstance {
		private final Optional<ContextAwarePredicate> player;

		public Instance(Optional<ContextAwarePredicate> player) {
			this.player = player;
		}

		@Override
		public Optional<ContextAwarePredicate> player() {
			return this.player;
		}
	}
}

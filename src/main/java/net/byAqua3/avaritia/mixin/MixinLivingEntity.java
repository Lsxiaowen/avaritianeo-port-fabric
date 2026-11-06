package net.byAqua3.avaritia.mixin;

import java.util.ArrayList;
import java.util.List;

import net.byAqua3.avaritia.event.AvaritiaEvent;
import net.byAqua3.avaritia.item.ItemSkullFireSword;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	@Redirect(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;)V"))
	private void avaritia$skullfireDrops(LivingEntity entity, ServerLevel level, DamageSource source,
			boolean killedByPlayer, ResourceKey<LootTable> lootTable) {
		if (entity instanceof AbstractSkeleton skeleton && source.getEntity() instanceof Player player
				&& player.getMainHandItem().getItem() instanceof ItemSkullFireSword) {
			List<ItemStack> drops = new ArrayList<>();
			entity.dropFromLootTable(level, source, killedByPlayer, lootTable, drops::add);
			AvaritiaEvent.onLivingDrops(level, player, skeleton, drops);
			for (ItemStack stack : drops) {
				entity.spawnAtLocation(level, stack);
			}
		} else {
			entity.dropFromLootTable(level, source, killedByPlayer, lootTable);
		}
	}
}

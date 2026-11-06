package net.byAqua3.avaritia.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.byAqua3.avaritia.damage.DamageSourceInfinity;
import net.byAqua3.avaritia.item.ItemInfinityAxe;
import net.byAqua3.avaritia.item.ItemInfinityAxe.BlockSwapper;
import net.byAqua3.avaritia.item.ItemInfinityHoe;
import net.byAqua3.avaritia.item.ItemInfinityPickaxe;
import net.byAqua3.avaritia.item.ItemInfinityShovel;
import net.byAqua3.avaritia.item.ItemSkullFireSword;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.byAqua3.avaritia.loader.AvaritiaTriggers;
import net.byAqua3.avaritia.util.ItemUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AvaritiaEvent {

	/** Players whose flight was granted by the infinity chestplate. */
	private static final Set<UUID> FLIGHT_GRANTED = new HashSet<>();

	/** Players whose step height was raised by the infinity boots. */
	private static final Set<UUID> STEP_GRANTED = new HashSet<>();

	private static final float DEFAULT_FLYING_SPEED = 0.05F;

	public static void registerEvents() {
		ServerTickEvents.END_SERVER_TICK.register(AvaritiaEvent::onServerTick);

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			FLIGHT_GRANTED.clear();
			STEP_GRANTED.clear();
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (entity instanceof Player player && ItemUtils.isInfinityArmor(player)
					&& !(source instanceof DamageSourceInfinity)) {
				player.hurtTime = 0;
				player.deathTime = 0;
				return false;
			}
			return true;
		});

		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
			if (entity instanceof Player player && ItemUtils.isInfinityArmor(player)
					&& !(source instanceof DamageSourceInfinity)) {
				player.hurtTime = 0;
				player.deathTime = 0;
				player.setHealth(Math.max(20.0F, player.getMaxHealth()));
				return false;
			}
			return true;
		});

		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
			ItemStack stack = player.getMainHandItem();
			Item item = stack.getItem();
			if (item instanceof ItemInfinityAxe || item instanceof ItemInfinityShovel
					|| item instanceof ItemInfinityHoe) {
				return;
			}
			if (item instanceof ItemInfinityPickaxe) {
				onPickaxeBreak(level, player, pos, state, stack);
			}
		});

		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
			ItemStack stack = player.getItemInHand(hand);
			Item item = stack.getItem();
			BlockState state = level.getBlockState(pos);
			if (level.isClientSide() || !player.getAbilities().mayBuild || !level.mayInteract(player, pos)) {
				return net.minecraft.world.InteractionResult.PASS;
			}

			if (item instanceof ItemInfinityPickaxe) {
				boolean hammer = stack.getOrDefault(AvaritiaDataComponents.HAMMER, false);
				boolean unbreakable = state.getDestroySpeed(level, pos) < 0.0F;
				if (hammer && (player.isCreative() || unbreakable)) {
					item.mineBlock(stack, level, state, pos, player);
					return net.minecraft.world.InteractionResult.SUCCESS;
				}
				if (!hammer && !player.isCreative() && unbreakable) {
					onPickaxeBreak(level, player, pos, state, stack);
					return net.minecraft.world.InteractionResult.SUCCESS;
				}
			}

			if ((item instanceof ItemInfinityAxe || item instanceof ItemInfinityShovel) && player.isCreative()) {
				item.mineBlock(stack, level, state, pos, player);
			}
			return net.minecraft.world.InteractionResult.PASS;
		});
	}

	private static void onPickaxeBreak(Level level, Player player, BlockPos pos, BlockState state, ItemStack stack) {
		if (!stack.getOrDefault(AvaritiaDataComponents.HAMMER, false)) {
			if (!level.isClientSide() && !player.isCreative()) {
				List<ItemStack> blockDrops = net.minecraft.world.level.block.Block.getDrops(
						state, (ServerLevel) level, pos, null, player, stack);
				if (blockDrops.isEmpty()) {
					Item blockItem = state.getBlock().asItem();
					if (!blockItem.equals(net.minecraft.world.item.Items.AIR)) {
						var itemEntity = new net.minecraft.world.entity.item.ItemEntity(level,
								pos.getX(), pos.getY(), pos.getZ(), new ItemStack(blockItem));
						itemEntity.setDefaultPickUpDelay();
						level.addFreshEntity(itemEntity);
						level.destroyBlock(pos, false);
					}
				}
			}
		}
	}

	public static void onServerTick(net.minecraft.server.MinecraftServer server) {
		if (!ItemInfinityAxe.BlockSwappers.isEmpty()) {
			List<BlockSwapper> blockSwappers = List.copyOf(ItemInfinityAxe.BlockSwappers);
			ItemInfinityAxe.BlockSwappers.clear();
			for (BlockSwapper blockSwapper : blockSwappers) {
				blockSwapper.tick();
			}
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			applyArmorEffects(player);
			AvaritiaTriggers.ROOT.trigger(player);
		}
	}

	private static void applyArmorEffects(ServerPlayer player) {
		ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
		ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

		if (head.is(AvaritiaItems.INFINITY_HELMET)) {
			player.setAirSupply(300);
			player.getFoodData().setFoodLevel(20);
			player.getFoodData().setSaturation(20.0F);
			if (AvaritiaConfigs.nightVision) {
				addEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
			}
		}

		if (chest.is(AvaritiaItems.INFINITY_CHESTPLATE)) {
			grantFlight(player);
			syncWingFlag(chest, player.getAbilities().flying);
			if (AvaritiaConfigs.clearBadEffect) {
				for (MobEffectInstance effect : List.copyOf(player.getActiveEffects())) {
					if (!effect.getEffect().value().isBeneficial()) {
						removeEffect(player, effect);
					}
				}
			}
		} else {
			revokeFlight(player);
		}

		if (legs.is(AvaritiaItems.INFINITY_LEGGINGS)) {
			if (AvaritiaConfigs.clearFire) {
				player.clearFire();
			}
			if (AvaritiaConfigs.clearFrozen) {
				player.setTicksFrozen(0);
			}
		}

		if (feet.is(AvaritiaItems.INFINITY_BOOTS)) {
			grantStepHeight(player);
		} else {
			revokeStepHeight(player);
		}
	}

	private static void grantFlight(ServerPlayer player) {
		boolean changed = false;

		if (!player.getAbilities().mayfly) {
			player.getAbilities().mayfly = true;
			FLIGHT_GRANTED.add(player.getUUID());
			changed = true;
		}

		float target = DEFAULT_FLYING_SPEED * (float) (2.0F * AvaritiaConfigs.flySpeedValue);
		if (Math.abs(player.getAbilities().getFlyingSpeed() - target) > 1.0E-4F) {
			player.getAbilities().setFlyingSpeed(target);
			changed = true;
		}

		if (changed) {
			syncAbilities(player);
		}
	}

	private static void revokeFlight(ServerPlayer player) {
		if (!FLIGHT_GRANTED.contains(player.getUUID()) || player.isCreative() || player.isSpectator()) {
			return;
		}

		FLIGHT_GRANTED.remove(player.getUUID());
		player.getAbilities().mayfly = false;
		player.getAbilities().flying = false;
		player.getAbilities().setFlyingSpeed(DEFAULT_FLYING_SPEED);
		syncAbilities(player);
	}

	/**
	 * Mirrors the player's flight state into the chestplate's {@code avaritia:fly}
	 * component.
	 *
	 * The wings render layer runs on the client and the render state only carries
	 * equipment stacks, not player abilities — so the flag has to travel on the
	 * stack. Only written when it actually changes, to avoid a sync packet per tick.
	 */
	private static void syncWingFlag(ItemStack chestplate, boolean flying) {
		if (chestplate.getOrDefault(AvaritiaDataComponents.FLY, false) != flying) {
			chestplate.set(AvaritiaDataComponents.FLY, flying);
		}
	}

	private static void grantStepHeight(ServerPlayer player) {
		if (!AvaritiaConfigs.stepHeight || STEP_GRANTED.contains(player.getUUID())) {
			return;
		}
		player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0625F);
		STEP_GRANTED.add(player.getUUID());
		syncAttributes(player);
	}

	private static void revokeStepHeight(ServerPlayer player) {
		if (!STEP_GRANTED.remove(player.getUUID())) {
			return;
		}
		player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.5F);
		syncAttributes(player);
	}

	private static void syncAbilities(ServerPlayer player) {
		if (player.connection != null) {
			player.onUpdateAbilities();
		}
	}

	private static void syncAttributes(ServerPlayer player) {
		if (player.connection != null) {
			player.connection.send(new ClientboundUpdateAttributesPacket(
					player.getId(), player.getAttributes().getSyncableAttributes()));
		}
	}

	private static void addEffect(ServerPlayer player, MobEffectInstance effect) {
		if (player.connection == null) {
			player.getActiveEffectsMap().put(effect.getEffect(), effect);
		} else {
			player.addEffect(effect);
		}
	}

	private static void removeEffect(ServerPlayer player, MobEffectInstance effect) {
		if (player.connection == null) {
			player.removeEffectNoUpdate(effect.getEffect());
		} else {
			player.removeEffect(effect.getEffect());
		}
	}

	public static void onLivingDrops(ServerLevel level, Player player, AbstractSkeleton skeleton, List<ItemStack> drops) {
		ItemStack itemStack = player.getMainHandItem();
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemSkullFireSword) {
			drops.removeIf(drop -> drop.getItem() == net.minecraft.world.item.Items.WITHER_SKELETON_SKULL);

			int randomInt = player.getRandom().nextInt(100);
			if (randomInt < AvaritiaConfigs.dropChange) {
				drops.add(new ItemStack(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL));
			}
		}
	}
}

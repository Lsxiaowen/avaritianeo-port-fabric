package net.byAqua3.avaritia.item;

import java.util.function.Consumer;

import net.byAqua3.avaritia.event.AvaritiaClientEvent;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Infinity armor.
 *
 * The per-slot effects (flight, night vision, fire immunity, speed) are applied
 * from {@code AvaritiaEvent}'s server tick rather than {@link #inventoryTick}:
 * in 26.1 the armor slots live in {@code EntityEquipment}, and relying on the
 * item tick made behaviour depend on which slot the stack happened to be in.
 */
public class ItemInfinityArmor extends Item {

	private final EquipmentSlot slot;

	public ItemInfinityArmor(EquipmentSlot slot, Item.Properties properties) {
		super(properties);
		this.slot = slot;
	}

	public EquipmentSlot getEquipmentSlot() {
		return this.slot;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		if (this.slot == EquipmentSlot.FEET) {
			tooltip.accept(Component.literal(""));
			tooltip.accept(Component.literal(ChatFormatting.BLUE + "+"
					+ AvaritiaClientEvent.makeSANIC("SANIC").getString() + ChatFormatting.BLUE + "% Speed"));
		}
	}
}

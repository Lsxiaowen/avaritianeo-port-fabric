package net.byAqua3.avaritia.event;

import net.byAqua3.avaritia.item.ItemInfinitySword;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AvaritiaClientEvent {

	public static long lastTime = System.currentTimeMillis();

	private static final ChatFormatting[] rainbow = new ChatFormatting[] { ChatFormatting.RED, ChatFormatting.GOLD,
			ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE,
			ChatFormatting.LIGHT_PURPLE };
	private static final ChatFormatting[] sanic = new ChatFormatting[] { ChatFormatting.BLUE, ChatFormatting.BLUE,
			ChatFormatting.BLUE, ChatFormatting.BLUE, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.WHITE,
			ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.WHITE, ChatFormatting.WHITE, ChatFormatting.BLUE,
			ChatFormatting.RED, ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY,
			ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY,
			ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY,
			ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY };

	public static String ColorTransformationFormatting(String input, ChatFormatting[] colours, double delay, int step, int posstep) {
		StringBuilder stringBuilder = new StringBuilder(input.length() * 3);

		if (delay <= 0.0D) {
			delay = 0.001D;
		}

		int offset = Mth.floor((System.currentTimeMillis() - lastTime) / delay) % colours.length;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			int colorIndex = ((i * posstep) + colours.length - offset) % colours.length;
			stringBuilder.append(colours[colorIndex].toString());
			stringBuilder.append(c);
		}

		return stringBuilder.toString();
	}

	public static Component makeRainbow(String text) {
		MutableComponent result = Component.empty();
		int offset = Math.floorMod(Mth.floor((System.currentTimeMillis() - lastTime) / 80.0D), rainbow.length);
		for (int index = 0; index < text.length(); index++) {
			result.append(Component.literal(String.valueOf(text.charAt(index)))
					.withStyle(rainbow[Math.floorMod(index - offset, rainbow.length)]));
		}
		return result;
	}

	public static Component makeSANIC(String text) {
		return Component.literal(ColorTransformationFormatting(text, sanic, 50.0D, 2, 1));
	}

	public static void applyInfinitySwordTooltip(ItemStack itemStack, java.util.List<Component> tooltip) {
		Item item = itemStack.getItem();
		if (!(item instanceof ItemInfinitySword)) {
			return;
		}
		for (int i = 0; i < tooltip.size(); i++) {
			tooltip.set(i, replaceAttackDamage(tooltip.get(i)));
		}
	}

	private static Component replaceAttackDamage(Component component) {
		MutableComponent result = component.plainCopy();
		if (component.getContents() instanceof TranslatableContents translation
				&& translation.getKey().startsWith("attribute.modifier.equals.")
				&& translation.getArgs().length >= 2
				&& translation.getArgs()[1] instanceof Component attribute && isAttackDamage(attribute)) {
			Object[] arguments = translation.getArgs().clone();
			arguments[0] = makeRainbow(Component.translatable("tip.infinity").getString());
			result = Component.translatableWithFallback(translation.getKey(), translation.getFallback(), arguments);
		}
		result.setStyle(component.getStyle());
		for (Component sibling : component.getSiblings()) {
			result.append(replaceAttackDamage(sibling));
		}
		return result;
	}

	private static boolean isAttackDamage(Component component) {
		if (component.getContents() instanceof TranslatableContents translation
				&& translation.getKey().equals(Attributes.ATTACK_DAMAGE.value().getDescriptionId())) {
			return true;
		}
		return component.getSiblings().stream().anyMatch(AvaritiaClientEvent::isAttackDamage);
	}
}

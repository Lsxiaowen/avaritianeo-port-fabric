package net.byAqua3.avaritia.recipe;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeCompressor implements RecipeExtremeCrafting {

	public static final MapCodec<RecipeCompressor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
			Codec.INT.fieldOf("cost").forGetter(recipe -> recipe.cost),
			Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients))
			.apply(instance, RecipeCompressor::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeCompressor> STREAM_CODEC = StreamCodec.of(
			RecipeCompressor::toNetwork, RecipeCompressor::fromNetwork);

	public final String group;
	public final ItemStackTemplate result;
	public final int cost;
	public final List<Ingredient> ingredients;

	public RecipeCompressor(String group, ItemStackTemplate result, int cost, List<Ingredient> ingredients) {
		this.group = group;
		this.result = result;
		this.cost = cost;
		this.ingredients = List.copyOf(ingredients);
	}

	@Override
	public ItemStack resultStack() {
		return this.result.create();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		List<ItemStack> remaining = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				remaining.add(stack.copy());
			}
		}

		if (remaining.size() != this.ingredients.size()) {
			return false;
		}

		for (Ingredient ingredient : this.ingredients) {
			boolean matched = false;
			for (int i = 0; i < remaining.size(); i++) {
				if (ingredient.test(remaining.get(i))) {
					remaining.remove(i);
					matched = true;
					break;
				}
			}
			if (!matched) {
				return false;
			}
		}
		return true;
	}


	@Override
	public ItemStack assemble(CraftingInput input) {
		return this.result.create();
	}


	@Override
	public boolean showNotification() {
		return false;
	}


	@Override
	public String group() {
		return this.group;
	}


	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.addAll(this.ingredients);
		return list;
	}


	@Override
	public RecipeSerializer<? extends RecipeCompressor> getSerializer() {
		return AvaritiaRecipes.COMPRESSOR_RECIPE;
	}

	private static RecipeCompressor fromNetwork(RegistryFriendlyByteBuf buf) {
		String group = buf.readUtf();
		ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
		int cost = buf.readVarInt();
		int size = buf.readVarInt();
		List<Ingredient> ingredients = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
		}
		return new RecipeCompressor(group, result, cost, ingredients);
	}

	private static void toNetwork(RegistryFriendlyByteBuf buf, RecipeCompressor recipe) {
		buf.writeUtf(recipe.group);
		ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result);
		buf.writeVarInt(recipe.cost);
		buf.writeVarInt(recipe.ingredients.size());
		for (Ingredient ingredient : recipe.ingredients) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
		}
	}
}




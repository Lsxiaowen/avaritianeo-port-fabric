package net.byAqua3.avaritia.recipe;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.byAqua3.avaritia.item.ItemInfinitySingularity;
import net.byAqua3.avaritia.item.ItemJsonSingularity;
import net.byAqua3.avaritia.item.ItemSingularity;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.loader.AvaritiaSingularities;
import net.byAqua3.avaritia.singularity.Singularity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeExtremeShapeless implements RecipeExtremeCrafting {

	public static final MapCodec<RecipeExtremeShapeless> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
			Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
			Codec.BOOL.optionalFieldOf("singularities", false).forGetter(recipe -> recipe.hasSingularities))
			.apply(instance, RecipeExtremeShapeless::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeExtremeShapeless> STREAM_CODEC = StreamCodec.of(
			RecipeExtremeShapeless::toNetwork, RecipeExtremeShapeless::fromNetwork);

	public final String group;
	public final ItemStackTemplate result;
	public final List<Ingredient> ingredients;
	public final boolean hasSingularities;

	public RecipeExtremeShapeless(String group, ItemStackTemplate result, List<Ingredient> ingredients, boolean hasSingularities) {
		this.group = group;
		this.result = result;
		this.ingredients = List.copyOf(ingredients);
		this.hasSingularities = hasSingularities;
	}

	@Override
	public ItemStack resultStack() {
		return this.result.create();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		List<Ingredient> expected = this.getIngredients();
		List<ItemStack> remaining = new ArrayList<>();

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				remaining.add(stack.copy());
			}
		}

		if (remaining.size() != expected.size()) {
			return false;
		}

		for (Ingredient ingredient : expected) {
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
		if (this.hasSingularities) {
			List<Ingredient> list = new ArrayList<>(this.ingredients);
			for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
				if (item instanceof ItemSingularity && !(item instanceof ItemJsonSingularity)
						&& !(item instanceof ItemInfinitySingularity)) {
					list.add(Ingredient.of(item));
				}
			}
			for (Singularity singularity : AvaritiaSingularities.getInstance().getSingularities()) {
				ItemStack stack = new ItemStack(AvaritiaItems.JSON_SINGULARITY);
				stack.set(AvaritiaDataComponents.SINGULARITY_ID, singularity.getId());
				list.add(Ingredient.of(stack.getItem()));
			}
			NonNullList<Ingredient> result = NonNullList.create();
			result.addAll(list);
			return result;
		}
		NonNullList<Ingredient> result = NonNullList.create();
		result.addAll(this.ingredients);
		return result;
	}


	@Override
	public RecipeSerializer<? extends RecipeExtremeShapeless> getSerializer() {
		return AvaritiaRecipes.EXTREME_SHAPELESS_RECIPE;
	}

	private static RecipeExtremeShapeless fromNetwork(RegistryFriendlyByteBuf buf) {
		String group = buf.readUtf();
		int size = buf.readVarInt();
		List<Ingredient> ingredients = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
		}
		ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
		boolean hasSingularities = buf.readBoolean();
		return new RecipeExtremeShapeless(group, result, ingredients, hasSingularities);
	}

	private static void toNetwork(RegistryFriendlyByteBuf buf, RecipeExtremeShapeless recipe) {
		buf.writeUtf(recipe.group);
		buf.writeVarInt(recipe.ingredients.size());
		for (Ingredient ingredient : recipe.ingredients) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
		}
		ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result);
		buf.writeBoolean(recipe.hasSingularities);
	}
}




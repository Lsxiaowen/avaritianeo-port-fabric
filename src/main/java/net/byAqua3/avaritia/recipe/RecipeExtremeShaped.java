package net.byAqua3.avaritia.recipe;

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
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public class RecipeExtremeShaped implements RecipeExtremeCrafting {

	public static final MapCodec<RecipeExtremeShaped> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtremeShapedPatternCodec.MAP_CODEC.forGetter(recipe -> recipe.pattern),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result))
			.apply(instance, RecipeExtremeShaped::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeExtremeShaped> STREAM_CODEC = StreamCodec.composite(
			ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
			ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
			RecipeExtremeShaped::new);

	public final ShapedRecipePattern pattern;
	public final ItemStackTemplate result;

	public RecipeExtremeShaped(ShapedRecipePattern pattern, ItemStackTemplate result) {
		this.pattern = pattern;
		this.result = result;
	}

	@Override
	public ItemStack resultStack() {
		return this.result.create();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		return this.pattern.matches(input);
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
		return "";
	}


	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		this.pattern.ingredients().forEach(optional -> {
			optional.ifPresent(ingredients::add);
		});
		return ingredients;
	}


	@Override
	public RecipeSerializer<? extends RecipeExtremeShaped> getSerializer() {
		return AvaritiaRecipes.EXTREME_SHAPED_RECIPE;
	}

	/**
	 * The full 9x9 grid, one entry per cell including the empty ones. JEI needs
	 * this to place ingredients by grid position; {@link #getIngredients()}
	 * filters the gaps out and shifts everything.
	 */
	public java.util.List<java.util.Optional<Ingredient>> patternIngredients() {
		return this.pattern.ingredients();
	}

	public int getWidth() {
		return this.pattern.width();
	}

	public int getHeight() {
		return this.pattern.height();
	}
}






package net.byAqua3.avaritia.recipe;

import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeType;

public interface RecipeExtremeCrafting extends Recipe<CraftingInput> {

	@Override
	default RecipeType<? extends Recipe<CraftingInput>> getType() {
		return AvaritiaRecipes.EXTREME_CRAFTING;
	}

	NonNullList<Ingredient> getIngredients();

	default ItemStack resultStack() {
		return ItemStack.EMPTY;
	}

	@Override
	default PlacementInfo placementInfo() {
		java.util.List<java.util.Optional<Ingredient>> optionals = new java.util.ArrayList<>();
		for (Ingredient ingredient : this.getIngredients()) {
			optionals.add(ingredient.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(ingredient));
		}
		return PlacementInfo.createFromOptionals(optionals);
	}

	@Override
	default RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}
}



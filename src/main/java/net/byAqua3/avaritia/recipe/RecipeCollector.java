package net.byAqua3.avaritia.recipe;

import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Pseudo recipe used only to display the neutron collector output in JEI.
 */
public class RecipeCollector implements RecipeExtremeCrafting {

	@Override
	public boolean matches(CraftingInput input, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return new ItemStack(AvaritiaItems.NEUTRON_PILE);
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeSerializer<? extends net.minecraft.world.item.crafting.Recipe<CraftingInput>> getSerializer() {
		return (RecipeSerializer<? extends net.minecraft.world.item.crafting.Recipe<CraftingInput>>) (RecipeSerializer<?>) AvaritiaRecipes.COMPRESSOR_RECIPE;
	}

	@Override
	public boolean showNotification() {
		return false;
	}
}


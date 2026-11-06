package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.recipe.RecipeCompressor;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class AvaritiaRecipes {

	public static final RecipeType<RecipeExtremeCrafting> EXTREME_CRAFTING = Registry.register(
			BuiltInRegistries.RECIPE_TYPE, Avaritia.id("extreme_crafting"),
			new RecipeType<>() {
				@Override
				public String toString() {
					return Avaritia.id("extreme_crafting").toString();
				}
			});

	public static final RecipeType<RecipeCompressor> COMPRESSOR = Registry.register(
			BuiltInRegistries.RECIPE_TYPE, Avaritia.id("compressor"),
			new RecipeType<>() {
				@Override
				public String toString() {
					return Avaritia.id("compressor").toString();
				}
			});

	public static final RecipeSerializer<RecipeExtremeShaped> EXTREME_SHAPED_RECIPE = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER, Avaritia.id("extreme_shaped"),
			new RecipeSerializer<>(RecipeExtremeShaped.CODEC, RecipeExtremeShaped.STREAM_CODEC));

	public static final RecipeSerializer<RecipeExtremeShapeless> EXTREME_SHAPELESS_RECIPE = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER, Avaritia.id("extreme_shapeless"),
			new RecipeSerializer<>(RecipeExtremeShapeless.CODEC, RecipeExtremeShapeless.STREAM_CODEC));

	public static final RecipeSerializer<RecipeCompressor> COMPRESSOR_RECIPE = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER, Avaritia.id("compressor"),
			new RecipeSerializer<>(RecipeCompressor.CODEC, RecipeCompressor.STREAM_CODEC));

	public static void registerRecipes() {
	}
}

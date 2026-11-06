package net.byAqua3.avaritia.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import net.byAqua3.avaritia.inventory.ContainerExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeCollector;
import net.byAqua3.avaritia.recipe.RecipeCompressor;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public class RecipeUtils {

	private static Collection<RecipeHolder<?>> allRecipes(RecipeManager manager) {
		return manager.getRecipes();
	}

	private static Collection<RecipeHolder<?>> allRecipes(Level level) {
		return allRecipes(recipeManagerOf(level));
	}

	/**
	 * {@code Level#getServer()} is null on a client level, yet JEI lookups run on
	 * the client thread. In single player the recipes live on the integrated
	 * server, reachable via {@code Minecraft#getSingleplayerServer()}. That class
	 * only exists on the client, and this class lives in the main source set, so
	 * the lookup goes through reflection instead of a hard reference.
	 */
	private static RecipeManager recipeManagerOf(Level level) {
		MinecraftServer server = level.getServer();
		if (server == null) {
			server = singleplayerServer();
		}
		if (server == null) {
			throw new IllegalStateException("No server available to read recipes from");
		}
		return server.getRecipeManager();
	}

	private static MinecraftServer singleplayerServer() {
		try {
			Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
			Object minecraft = minecraftClass.getMethod("getInstance").invoke(null);
			if (minecraft == null) {
				return null;
			}
			Object server = minecraftClass.getMethod("getSingleplayerServer").invoke(minecraft);
			return server instanceof MinecraftServer minecraftServer ? minecraftServer : null;
		} catch (ReflectiveOperationException | LinkageError e) {
			return null;
		}
	}

	public static List<RecipeExtremeCrafting> getExtremeCraftingRecipes(Collection<RecipeHolder<?>> recipes) {
		List<RecipeExtremeCrafting> list = new ArrayList<>();
		for (RecipeHolder<?> holder : recipes) {
			Recipe<?> recipe = holder.value();
			if (recipe instanceof RecipeExtremeShaped || recipe instanceof RecipeExtremeShapeless) {
				list.add((RecipeExtremeCrafting) recipe);
			}
		}
		list.sort(Comparator.comparingInt(recipe -> BuiltInRegistries.ITEM.getId(recipe.resultStack().getItem())));
		return list;
	}

	public static List<RecipeExtremeCrafting> getExtremeCraftingRecipes(RecipeManager manager) {
		return getExtremeCraftingRecipes(allRecipes(manager));
	}

	public static List<RecipeExtremeCrafting> getExtremeCraftingRecipes(Level level) {
		return getExtremeCraftingRecipes(allRecipes(level));
	}

	public static List<RecipeCompressor> getCompressorRecipes(Collection<RecipeHolder<?>> recipes) {
		List<RecipeCompressor> list = new ArrayList<>();
		for (RecipeHolder<?> holder : recipes) {
			if (holder.value() instanceof RecipeCompressor compressor && !compressor.getIngredients().isEmpty()) {
				list.add(compressor);
			}
		}
		list.sort(Comparator.comparingInt(recipe -> BuiltInRegistries.ITEM.getId(recipe.resultStack().getItem())));
		return list;
	}

	public static List<RecipeCompressor> getCompressorRecipes(RecipeManager manager) {
		return getCompressorRecipes(allRecipes(manager));
	}

	public static List<RecipeCompressor> getCompressorRecipes(Level level) {
		return getCompressorRecipes(allRecipes(level));
	}

	public static RecipeCompressor getCompressorRecipe(Collection<RecipeHolder<?>> recipes, ItemStack input) {
		SimpleContainer matrix = new SimpleContainer(1);
		matrix.addItem(input);
		ContainerExtremeCrafting crafting = new ContainerExtremeCrafting(null, 1, 1, matrix);
		for (RecipeHolder<?> holder : recipes) {
			if (holder.value() instanceof RecipeCompressor compressor
					&& compressor.matches(crafting.asCraftInput(), null)) {
				return compressor;
			}
		}
		return null;
	}

	public static RecipeCompressor getCompressorRecipe(Level level, ItemStack input) {
		return getCompressorRecipe(allRecipes(level), input);
	}

	public static RecipeCompressor getCompressorRecipeFromResult(Collection<RecipeHolder<?>> recipes, ItemStack result) {
		for (RecipeHolder<?> holder : recipes) {
			if (holder.value() instanceof RecipeCompressor compressor
					&& ItemStack.isSameItemSameComponents(compressor.resultStack(), result)) {
				return compressor;
			}
		}
		return null;
	}

	public static RecipeCompressor getCompressorRecipeFromResult(Level level, ItemStack result) {
		return getCompressorRecipeFromResult(allRecipes(level), result);
	}

	public static RecipeCompressor getCompressorRecipeFromResult(Collection<RecipeHolder<?>> recipes, ItemStack result, DataComponentType<?> type) {
		for (RecipeHolder<?> holder : recipes) {
			if (holder.value() instanceof RecipeCompressor compressor) {
				ItemStack stack = compressor.resultStack();
				if (ItemStack.isSameItem(stack, result) && stack.has(type) && result.has(type)
						&& stack.get(type).equals(result.get(type))) {
					return compressor;
				}
			}
		}
		return null;
	}

	public static RecipeCompressor getCompressorRecipeFromResult(Level level, ItemStack result, DataComponentType<?> type) {
		return getCompressorRecipeFromResult(allRecipes(level), result, type);
	}

	public static List<RecipeCollector> getCollectorRecipes() {
		List<RecipeCollector> list = new ArrayList<>();
		list.add(new RecipeCollector());
		return list;
	}

	public static ResourceKey<Recipe<?>> getRecipeId(Collection<RecipeHolder<?>> recipes, Recipe<?> recipe) {
		for (RecipeHolder<?> holder : recipes) {
			if (recipe.equals(holder.value())) {
				return holder.id();
			}
		}
		return null;
	}

	public static ResourceKey<Recipe<?>> getRecipeId(Level level, Recipe<?> recipe) {
		return getRecipeId(allRecipes(level), recipe);
	}
}


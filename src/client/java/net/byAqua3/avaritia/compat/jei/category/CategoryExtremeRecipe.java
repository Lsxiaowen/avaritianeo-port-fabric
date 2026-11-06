package net.byAqua3.avaritia.compat.jei.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.byAqua3.avaritia.block.BlockExtremeCraftingTable;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import net.byAqua3.avaritia.gui.GuiExtremeCraftingTable;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CategoryExtremeRecipe implements IRecipeCategory<RecipeExtremeCrafting> {

	private static final int GRID = 9;

	private final IDrawable background;
	private final IDrawable icon;

	public CategoryExtremeRecipe(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(GuiExtremeCraftingTable.COMPAT_BACKGROUND_LOCATION, 0, 0, 189, 163);
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(AvaritiaBlocks.EXTREME_CRAFTING_TABLE_ITEM));
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public int getWidth() {
		return this.background.getWidth();
	}

	@Override
	public int getHeight() {
		return this.background.getHeight();
	}

	@Override
	public Component getTitle() {
		return BlockExtremeCraftingTable.TITLE;
	}

	@Override
	public IRecipeType<RecipeExtremeCrafting> getRecipeType() {
		return AvaritiaJEIPlugin.EXTREME_CRAFTING;
	}

	@Override
	public void draw(RecipeExtremeCrafting recipe, IRecipeSlotsView slotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		this.background.draw(graphics, 0, 0);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeExtremeCrafting recipe, IFocusGroup group) {
		List<IRecipeSlotBuilder> inputSlots = new ArrayList<>(GRID * GRID);
		for (int y = 0; y < GRID; y++) {
			for (int x = 0; x < GRID; x++) {
				inputSlots.add(builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 2, y * 18 + 2));
			}
		}

		if (recipe instanceof RecipeExtremeShaped shaped) {
			// Read the pattern's slot list directly: it keeps one entry per grid cell,
			// including the empty ones. Using the filtered getIngredients() shifts
			// every later ingredient into the wrong slot.
			List<Optional<Ingredient>> pattern = shaped.patternIngredients();
			int width = shaped.getWidth();
			int height = shaped.getHeight();
			int offsetX = (GRID - width) / 2;
			int offsetY = (GRID - height) / 2;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int patternIndex = x + y * width;
					if (patternIndex >= pattern.size()) {
						continue;
					}
					Optional<Ingredient> ingredient = pattern.get(patternIndex);
					if (ingredient.isEmpty()) {
						continue;
					}
					int slotIndex = (offsetX + x) + (offsetY + y) * GRID;
					inputSlots.get(slotIndex).add(ingredient.get());
				}
			}
		} else {
			builder.setShapeless();
			List<Ingredient> ingredients = recipe.getIngredients();
			for (int i = 0; i < ingredients.size() && i < inputSlots.size(); i++) {
				inputSlots.get(i).add(ingredients.get(i));
			}
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 168, 74).add(recipe.resultStack());
	}
}

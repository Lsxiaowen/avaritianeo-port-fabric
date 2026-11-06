package net.byAqua3.avaritia.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.byAqua3.avaritia.block.BlockNeutronCollector;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import net.byAqua3.avaritia.gui.GuiNeutronCollector;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.byAqua3.avaritia.recipe.RecipeCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategoryCollectorRecipe implements IRecipeCategory<RecipeCollector> {

	private final IDrawable background;
	private final IDrawable icon;

	public CategoryCollectorRecipe(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(GuiNeutronCollector.BACKGROUND_LOCATION, 37, 29, 102, 41);
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(AvaritiaBlocks.NEUTRON_COLLECTOR_ITEM));
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
		return BlockNeutronCollector.TITLE;
	}

	@Override
	public IRecipeType<RecipeCollector> getRecipeType() {
		return AvaritiaJEIPlugin.COLLECTOR;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeCollector recipe, IFocusGroup group) {
		builder.addSlot(RecipeIngredientRole.OUTPUT, 43, 6).add(recipe.assemble(null));
	}

	@Override
	public void draw(RecipeCollector recipe, IRecipeSlotsView slotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		this.background.draw(graphics, 0, 0);
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		String time = String.format("%.2f", AvaritiaConfigs.productionTicks / 20.0F / 60.0F);
		String text = Component.translatable("avaritia:container.neutron_collector.info", time).getString();
		graphics.text(font, text, this.background.getWidth() / 2 - font.width(text) / 2, 31, -12566464, false);
	}
}



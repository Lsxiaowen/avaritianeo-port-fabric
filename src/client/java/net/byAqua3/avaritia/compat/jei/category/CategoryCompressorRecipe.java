package net.byAqua3.avaritia.compat.jei.category;

import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.byAqua3.avaritia.block.BlockNeutroniumCompressor;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import net.byAqua3.avaritia.gui.GuiNeutroniumCompressor;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.recipe.RecipeCompressor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategoryCompressorRecipe implements IRecipeCategory<RecipeCompressor> {

	/** Inline slot counter position, matching the vanilla recipe book style. */
	private static final int COUNT_X = 12;
	private static final int COUNT_Y = 17;

	private final IDrawable background;
	private final IDrawable icon;
	private final LoadingCache<Integer, IDrawableAnimated> singularities;

	public CategoryCompressorRecipe(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(GuiNeutroniumCompressor.BACKGROUND_LOCATION, 37, 29, 102, 41);
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(AvaritiaBlocks.COMPRESSOR_ITEM));
		this.singularities = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer, IDrawableAnimated>() {
			@Override
			public IDrawableAnimated load(Integer key) {
				return guiHelper.drawableBuilder(GuiNeutroniumCompressor.BACKGROUND_LOCATION, 176, 16, 16, 16)
						.buildAnimated(Math.max(key / 16, 1), IDrawableAnimated.StartDirection.BOTTOM, false);
			}
		});
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
		return BlockNeutroniumCompressor.TITLE;
	}

	@Override
	public IRecipeType<RecipeCompressor> getRecipeType() {
		return AvaritiaJEIPlugin.COMPRESSOR;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeCompressor recipe, IFocusGroup group) {
		IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 2, 6);
		recipe.getIngredients().forEach(input::add);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 6).add(recipe.resultStack());
	}

	/**
	 * Draws the required item count over the input slot. JEI has no API for
	 * giving an ingredient an amount, so the number is rendered manually in the
	 * same corner a vanilla stack count would occupy.
	 */
	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeCompressor recipe, IFocusGroup group) {
		builder.addText(List.of(Component.literal(Integer.toString(recipe.cost))
				.withStyle(ChatFormatting.GRAY)), COUNT_X, COUNT_Y);
	}

	@Override
	public void draw(RecipeCompressor recipe, IRecipeSlotsView slotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		this.background.draw(graphics, 0, 0);
		IDrawableAnimated singularity = this.singularities.getUnchecked(recipe.cost);
		singularity.draw(graphics, 53, 6);

		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		String text = Component.translatable("avaritia:container.neutronium_compressor.info", recipe.cost).getString();
		graphics.text(font, text, this.background.getWidth() / 2 - font.width(text) / 2, 31, -12566464, false);
	}
}


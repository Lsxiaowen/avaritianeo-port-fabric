package net.byAqua3.avaritia.gui;

import net.byAqua3.avaritia.config.AvaritiaClientConfig;
import net.byAqua3.avaritia.config.AvaritiaClientConfig.Option;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class AvaritiaConfigScreen extends Screen {

	private final Screen parent;
	private boolean saveFailed;

	public AvaritiaConfigScreen(Screen parent) {
		super(Component.translatable("config.avaritia.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int buttonWidth = Math.min(310, width - 20);
		int left = (width - buttonWidth) / 2;
		int top = 52;
		int index = 0;
		for (Option option : Option.values()) {
			CycleButton<Boolean> button = CycleButton.onOffBuilder(AvaritiaClientConfig.INSTANCE.enabled(option))
					.create(left, top + index++ * 24, buttonWidth, 20, Component.translatable(option.translationKey()),
							(widget, value) -> AvaritiaClientConfig.INSTANCE.set(option, value));
			button.setTooltip(Tooltip.create(Component.translatable(option.translationKey() + ".tooltip")));
			addRenderableWidget(button);
		}
		int footerWidth = (buttonWidth - 8) / 3;
		addRenderableWidget(Button.builder(Component.translatable("config.avaritia.lowEffects"), button -> {
			AvaritiaClientConfig.INSTANCE.lowEffects();
			rebuildWidgets();
		}).bounds(left, height - 28, footerWidth, 20).build());
		addRenderableWidget(Button.builder(Component.translatable("config.avaritia.reset"), button -> {
			AvaritiaClientConfig.INSTANCE.reset();
			rebuildWidgets();
		}).bounds(left + footerWidth + 4, height - 28, footerWidth, 20).build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
				.bounds(left + (footerWidth + 4) * 2, height - 28, footerWidth, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		graphics.centeredText(font, title, width / 2, 16, 0xFFFFFFFF);
		graphics.centeredText(font, Component.translatable("config.avaritia.clientOnly"), width / 2, 34, 0xFFAAAAAA);
		if (saveFailed) {
			graphics.centeredText(font, Component.translatable("config.avaritia.saveFailed"), width / 2, height - 42, 0xFFFF5555);
		}
	}

	@Override
	public void onClose() {
		if (AvaritiaClientConfig.INSTANCE.save()) {
			minecraft.gui.setScreen(parent);
		} else {
			saveFailed = true;
		}
	}
}

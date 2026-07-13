package net.simpleraces.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.simpleraces.client.settings.ClientVisualSettings;

public class RaceVisualSettingsScreen extends Screen {
	private final Screen lastScreen;

	public RaceVisualSettingsScreen(Screen lastScreen) {
		super(Component.translatable("options.simpleraces.visual_settings"));
		this.lastScreen = lastScreen;
	}

	@Override
	protected void init() {
		super.init();
		int centerX = this.width / 2;
		int baseY = this.height / 6;

		this.addRenderableWidget(CycleButton.onOffBuilder(ClientVisualSettings.isAmbientRaceEffectsEnabled())
				.create(centerX - 155, baseY + 40, 310, 20,
						Component.translatable("options.simpleraces.ambient_race_effects"),
						(button, value) -> ClientVisualSettings.setAmbientRaceEffectsEnabled(value)));

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen))
				.pos(centerX - 100, this.height - 27)
				.size(200, 20)
				.build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.lastScreen);
	}
}





package net.simpleraces.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.simpleraces.SimpleracesMod;

@EventBusSubscriber(modid = SimpleracesMod.MODID, value = Dist.CLIENT)
public class RaceVisualSettingsButtonHandler {
	@SubscribeEvent
	public static void onOptionsScreenInit(ScreenEvent.Init.Post event) {
		Screen screen = event.getScreen();
		if (!(screen instanceof OptionsScreen)) {
			return;
		}

		int x = screen.width / 2 - 155;
		int y = screen.height / 6 + 144;
		event.addListener(Button.builder(Component.translatable("options.simpleraces.visual_settings"),
						button -> screen.getMinecraft().setScreen(new RaceVisualSettingsScreen(screen)))
				.pos(x, y)
				.size(150, 20)
				.build());
	}
}






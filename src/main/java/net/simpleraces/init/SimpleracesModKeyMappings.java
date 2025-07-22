
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.network.ModMessages;
import org.lwjgl.glfw.GLFW;

import net.simpleraces.network.OpenSelectMessage;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class SimpleracesModKeyMappings {
	public static final KeyMapping OPEN_SELECT = new KeyMapping("key.simpleraces.open_select", GLFW.GLFW_KEY_M, "key.categories.simpleraces") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ModMessages.INSTANCE.sendToServer(new OpenSelectMessage(0, 0));
				OpenSelectMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_SELECT);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				OPEN_SELECT.consumeClick();
			}
		}
	}
}


/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.network.ModMessages;
import org.lwjgl.glfw.GLFW;

import net.simpleraces.network.OpenSelectMessage;
import net.simpleraces.SimpleracesMod;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@EventBusSubscriber(modid = SimpleracesMod.MODID, value = Dist.CLIENT)
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

	@EventBusSubscriber(modid = SimpleracesMod.MODID, value = Dist.CLIENT)
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.screen == null) {
				OPEN_SELECT.consumeClick();
			}
		}
	}
}






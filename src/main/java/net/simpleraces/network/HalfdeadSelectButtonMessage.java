
package net.simpleraces.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.procedures.*;
import net.simpleraces.world.inventory.DragonSelectMenu;

import java.util.HashMap;
import java.util.function.Supplier;

public class HalfdeadSelectButtonMessage {
	private final int buttonID, x, y, z;

	public HalfdeadSelectButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public HalfdeadSelectButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(HalfdeadSelectButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(HalfdeadSelectButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = DragonSelectMenu.guistate;

		if (buttonID == 0) {

			SelectedHalfdeadProcedure.execute(entity);
		}
		if (buttonID == 1) {

			OpenArachaProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 2) {

			OpenWerewolfProcedure.execute(world, x, y, z, entity);
		}
	}
}

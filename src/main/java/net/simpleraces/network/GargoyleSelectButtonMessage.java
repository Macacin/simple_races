package net.simpleraces.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.procedures.OpenArachaProcedure;
import net.simpleraces.procedures.OpenHalfdeadProcedure;
import net.simpleraces.procedures.OpenHumanProcedure;
import net.simpleraces.procedures.SelectedGargoyleProcedure;
import net.simpleraces.world.inventory.DragonSelectMenu;

import java.util.HashMap;
import java.util.function.Supplier;

public class GargoyleSelectButtonMessage {
	private final int buttonID, x, y, z;

	public GargoyleSelectButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public GargoyleSelectButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(GargoyleSelectButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(GargoyleSelectButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			handleButtonAction(entity, message.buttonID, message.x, message.y, message.z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = DragonSelectMenu.guistate;

		if (buttonID == 0) {
			SelectedGargoyleProcedure.execute(entity);
		}
		if (buttonID == 1) {
			OpenArachaProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 2) {
			OpenHumanProcedure.execute(world, x, y, z, entity);
		}
	}
}






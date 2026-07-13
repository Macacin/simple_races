package net.simpleraces.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.procedures.SelectRandomRaceProcedure;

import java.util.function.Supplier;

public class RandomRaceButtonMessage {
	public RandomRaceButtonMessage() {
	}

	public RandomRaceButtonMessage(FriendlyByteBuf buffer) {
	}

	public static void buffer(RandomRaceButtonMessage message, FriendlyByteBuf buffer) {
	}

	public static void handler(RandomRaceButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayer entity = context.getSender();
			if (entity != null) {
				SelectRandomRaceProcedure.execute(entity);
			}
		});
		context.setPacketHandled(true);
	}
}






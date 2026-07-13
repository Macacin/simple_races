package net.simpleraces.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.procedures.race.GargoyleRaceMechanics;

import java.util.function.Supplier;

public class StartGargoyleGlidePacket {
    public StartGargoyleGlidePacket() {
    }

    public StartGargoyleGlidePacket(FriendlyByteBuf buffer) {
    }

    public static void encode(StartGargoyleGlidePacket msg, FriendlyByteBuf buf) {
    }

    public static StartGargoyleGlidePacket decode(FriendlyByteBuf buf) {
        return new StartGargoyleGlidePacket(buf);
    }

    public static void handle(StartGargoyleGlidePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (player != null) {
                GargoyleRaceMechanics.startManualGlide(player);
            }
        });
        context.setPacketHandled(true);
    }
}






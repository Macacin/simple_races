package net.simpleraces.network;


import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.client.SyncVars;

import java.util.function.Supplier;

public record SyncWerewolfPacket(boolean isWerewolf) {

    public static void encode(SyncWerewolfPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isWerewolf);
    }

    public static SyncWerewolfPacket decode(FriendlyByteBuf buf) {
        return new SyncWerewolfPacket(buf.readBoolean());
    }

    public static void handle(SyncWerewolfPacket packet, Supplier<NetworkEvent.Context> ctx) {
        SyncVars.syncWerewolf(packet.isWerewolf);
    }
}






package net.simpleraces.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.client.SyncVars;

import java.util.UUID;
import java.util.function.Supplier;


public class SyncHeatPacket {
    private final int heat;
    private final boolean overheated;
    private final UUID playerUUID;

    public SyncHeatPacket(UUID playerUUID, int heat, boolean overheated) {
        this.playerUUID = playerUUID;
        this.heat = heat;
        this.overheated = overheated;
    }

    public static void encode(SyncHeatPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerUUID);
        buf.writeInt(packet.heat);
        buf.writeBoolean(packet.overheated);
    }

    public static SyncHeatPacket decode(FriendlyByteBuf buf) {
        return new SyncHeatPacket(buf.readUUID(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(SyncHeatPacket packet, Supplier<NetworkEvent.Context> ctx) {
        SyncVars.syncHeat(packet.heat, packet.overheated);
    }
}
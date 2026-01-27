package net.simpleraces.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.simpleraces.client.SyncVars;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncHeatPacket {
    private final UUID playerUUID;
    private final int heat;
    private final int maxHeat;
    private final boolean overheated;

    private final int overheatTicks;
    private final int maxOverheatTicks;

    public SyncHeatPacket(UUID playerUUID, int heat, int maxHeat, boolean overheated, int overheatTicks, int maxOverheatTicks) {
        this.playerUUID = playerUUID;
        this.heat = heat;
        this.maxHeat = maxHeat;
        this.overheated = overheated;
        this.overheatTicks = overheatTicks;
        this.maxOverheatTicks = maxOverheatTicks;
    }

    public static void encode(SyncHeatPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerUUID);
        buf.writeInt(packet.heat);
        buf.writeInt(packet.maxHeat);
        buf.writeBoolean(packet.overheated);

        buf.writeInt(packet.overheatTicks);
        buf.writeInt(packet.maxOverheatTicks);
    }

    public static SyncHeatPacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        int heat = buf.readInt();
        int maxHeat = buf.readInt();
        boolean overheated = buf.readBoolean();

        int overheatTicks = buf.readInt();
        int maxOverheatTicks = buf.readInt();

        return new SyncHeatPacket(uuid, heat, maxHeat, overheated, overheatTicks, maxOverheatTicks);
    }

    public static void handle(SyncHeatPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SyncVars.syncHeat(packet.heat, packet.maxHeat, packet.overheated, packet.overheatTicks, packet.maxOverheatTicks);
        });
        ctx.get().setPacketHandled(true);
    }
}

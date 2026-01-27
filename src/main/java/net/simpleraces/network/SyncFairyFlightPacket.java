package net.simpleraces.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.simpleraces.client.SyncVars;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncFairyFlightPacket {
    private final UUID playerUUID;
    private final int flightBar;
    private final int maxFlight;
    private final boolean isRecovering;  // Новый флаг фазы

    public SyncFairyFlightPacket(UUID playerUUID, int flightBar, int maxFlight, boolean isRecovering) {
        this.playerUUID = playerUUID;
        this.flightBar = flightBar;
        this.maxFlight = maxFlight;
        this.isRecovering = isRecovering;
    }

    public static void encode(SyncFairyFlightPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeInt(msg.flightBar);
        buf.writeInt(msg.maxFlight);
        buf.writeBoolean(msg.isRecovering);
    }

    public static SyncFairyFlightPacket decode(FriendlyByteBuf buf) {
        return new SyncFairyFlightPacket(buf.readUUID(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(SyncFairyFlightPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getUUID().equals(msg.playerUUID)) {
                SyncVars.fairyFlightBar = msg.flightBar;
                SyncVars.maxFairyFlight = msg.maxFlight;
                SyncVars.isFairyRecovering = msg.isRecovering;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
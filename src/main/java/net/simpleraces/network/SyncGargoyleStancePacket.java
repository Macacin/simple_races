package net.simpleraces.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.client.SyncVars;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncGargoyleStancePacket {
    private final UUID playerUUID;
    private final int stance;
    private final int petrification;
    private final int stanceTime;
    private final int maxStanceTime;

    public SyncGargoyleStancePacket(UUID playerUUID, int stance, int petrification, int stanceTime, int maxStanceTime) {
        this.playerUUID = playerUUID;
        this.stance = stance;
        this.petrification = petrification;
        this.stanceTime = stanceTime;
        this.maxStanceTime = maxStanceTime;
    }

    public static void encode(SyncGargoyleStancePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeInt(msg.stance);
        buf.writeInt(msg.petrification);
        buf.writeInt(msg.stanceTime);
        buf.writeInt(msg.maxStanceTime);
    }

    public static SyncGargoyleStancePacket decode(FriendlyByteBuf buf) {
        return new SyncGargoyleStancePacket(buf.readUUID(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(SyncGargoyleStancePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getUUID().equals(msg.playerUUID)) {
                SyncVars.syncGargoyle(msg.stance, msg.petrification, msg.stanceTime, msg.maxStanceTime);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}






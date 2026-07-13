package net.simpleraces.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.client.SyncVars;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncAmbientEffectsPacket {
    private final UUID playerUUID;
    private final boolean elfNatureActive;
    private final boolean orcSleepActive;

    public SyncAmbientEffectsPacket(UUID playerUUID, boolean elfNatureActive, boolean orcSleepActive) {
        this.playerUUID = playerUUID;
        this.elfNatureActive = elfNatureActive;
        this.orcSleepActive = orcSleepActive;
    }

    public static void encode(SyncAmbientEffectsPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.elfNatureActive);
        buf.writeBoolean(msg.orcSleepActive);
    }

    public static SyncAmbientEffectsPacket decode(FriendlyByteBuf buf) {
        return new SyncAmbientEffectsPacket(buf.readUUID(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(SyncAmbientEffectsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getUUID().equals(msg.playerUUID)) {
                SyncVars.syncAmbientEffects(msg.elfNatureActive, msg.orcSleepActive);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}






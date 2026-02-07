package net.simpleraces.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopEatingPacket {

    public StopEatingPacket() {}

    public static void encode(StopEatingPacket msg, FriendlyByteBuf buf) {
        // Нет данных для кодирования
    }

    public static StopEatingPacket decode(FriendlyByteBuf buf) {
        return new StopEatingPacket();
    }

    public static void handle(StopEatingPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Выполняется на клиенте
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.stopUsingItem();
                // Сбрасываем анимацию руки
                mc.gameMode.stopDestroyBlock();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
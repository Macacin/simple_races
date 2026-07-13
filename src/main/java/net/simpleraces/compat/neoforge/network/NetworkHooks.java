package net.simpleraces.compat.neoforge.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.simpleraces.SimpleracesMod;

public final class NetworkHooks {
    private NetworkHooks() {
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] server openScreen title={} player={} pos={}", provider.getDisplayName().getString(), player.getGameProfile().getName(), pos);
        player.openMenu(provider, (RegistryFriendlyByteBuf buffer) -> buffer.writeBlockPos(pos));
    }
}





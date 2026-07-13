package net.simpleraces.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.procedures.race.GargoyleRaceMechanics;

@EventBusSubscriber
public class GargoyleSpirewingRocketBlockProcedure {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (shouldBlockSpecialFlightItem(
                event.getEntity(),
                event.getItemStack().getItem() instanceof FireworkRocketItem,
                event.getItemStack().getItem()
        )) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (shouldBlockRocket(player, event.getItem().getItem() instanceof FireworkRocketItem)) {
            event.setCanceled(true);
        }
    }

    private static boolean shouldBlockRocket(Player player, boolean usingFireworkRocket) {
        return shouldBlockSpecialFlightItem(player, usingFireworkRocket, null);
    }

    private static boolean shouldBlockSpecialFlightItem(Player player, boolean usingFireworkRocket, Item itemOverride) {
        Item item = itemOverride;
        if (item == null && usingFireworkRocket) {
            // no-op, firework is identified by type check
        }

        boolean isBlockedItem = usingFireworkRocket;
        if (!isBlockedItem && item != null) {
            var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            isBlockedItem = key != null
                    && "enigmaticlegacy".equals(key.getNamespace())
                    && "angel_blessing".equals(key.getPath());
        }

        if (!isBlockedItem) {
            return false;
        }

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);

        return vars.gargoyle
                && GargoyleRaceMechanics.getStance(player) == GargoyleRaceMechanics.SPIREWING
                && player.isFallFlying();
    }
}






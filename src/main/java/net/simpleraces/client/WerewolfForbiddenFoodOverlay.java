package net.simpleraces.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.data.WerewolfForbiddenItems;
import net.simpleraces.network.SimpleracesModVariables;

@EventBusSubscriber(modid = SimpleracesMod.MODID, value = Dist.CLIENT)
public class WerewolfForbiddenFoodOverlay {
    private static final Component BAD_FOOD_MESSAGE = Component.translatable("message.simpleraces.werewolf.bad_food")
            .withStyle(ChatFormatting.RED);

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (shouldShowBadFoodMessage(player)) {
            player.displayClientMessage(BAD_FOOD_MESSAGE, true);
        }
    }

    private static boolean shouldShowBadFoodMessage(Player player) {
        boolean isWerewolfRace = SimpleracesModVariables.getPlayerVariables(player).werewolf;

        if (!isWerewolfRace || !SyncVars.werewolf) return false;

        return isForbiddenFood(player.getMainHandItem()) || isForbiddenFood(player.getOffhandItem());
    }

    private static boolean isForbiddenFood(ItemStack stack) {
        if (stack.isEmpty() || stack.getFoodProperties(null) == null) return false;

        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && WerewolfForbiddenItems.isForbidden(itemId);
    }
}






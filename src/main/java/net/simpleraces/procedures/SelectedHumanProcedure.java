package net.simpleraces.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.network.SimpleracesModVariables;

public class SelectedHumanProcedure {
    public static void execute(Entity entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof Player player) {
            player.closeContainer();
        }
        if (!SimpleracesModVariables.getPlayerVariables(entity).selected) {
            SimpleracesModVariables.updatePlayerVariables(entity, capability -> {
                capability.human = true;
                capability.selected = true;
            });
            if (entity instanceof Player player) {
                AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealthAttr != null) {
                    double newMax = 20.0;
                    maxHealthAttr.setBaseValue(newMax);
                    player.setHealth((float) newMax);
                }
            }
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.simpleraces.race_selected.human").withStyle(ChatFormatting.YELLOW), true);
            }
        } else if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("message.simpleraces.class_previously_set").withStyle(ChatFormatting.DARK_RED), true);
        }
    }
}






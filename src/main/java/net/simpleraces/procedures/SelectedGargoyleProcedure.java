package net.simpleraces.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.network.SimpleracesModVariables;

public class SelectedGargoyleProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player player)
			player.closeContainer();
		if (!SimpleracesModVariables.getPlayerVariables((entity)).selected) {
			SimpleracesModVariables.updatePlayerVariables(entity, capability -> {
				capability.gargoyle = true;
				capability.selected = true;
			});
			if (entity instanceof Player player) {
				AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
				if (maxHealthAttr != null) {
					double newMax = 16.0;
					maxHealthAttr.setBaseValue(newMax);
					player.setHealth((float) newMax);
				}
			}
			entity.getPersistentData().putInt("simpleraces_gargoyle_stance", 1);
			entity.getPersistentData().putInt("simpleraces_gargoyle_stance_ticks", 0);
			entity.getPersistentData().putInt("simpleraces_gargoyle_still_ticks", 0);
			if (entity instanceof Player player && !player.level().isClientSide())
				player.displayClientMessage(Component.translatable("message.simpleraces.race_selected.gargoyle").withStyle(ChatFormatting.YELLOW), true);
		} else if (entity instanceof Player player && !player.level().isClientSide()) {
			player.displayClientMessage(Component.translatable("message.simpleraces.class_previously_set").withStyle(ChatFormatting.DARK_RED), true);
		}
	}
}






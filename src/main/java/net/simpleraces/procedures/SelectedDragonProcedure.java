package net.simpleraces.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.network.SimpleracesModVariables;

public class SelectedDragonProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player)
			_player.closeContainer();

		AttributeInstance maxHealthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
		if (maxHealthAttr != null) {
			double newMax = 20;
			maxHealthAttr.setBaseValue(newMax);
			((Player) entity).setHealth((float) newMax);
		}
		if (!SimpleracesModVariables.getPlayerVariables((entity)).selected) {
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.dragon = true); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.selected = true); }
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.translatable("message.simpleraces.race_selected.drakonid").withStyle(ChatFormatting.YELLOW), true);
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.translatable("message.simpleraces.class_previously_set").withStyle(ChatFormatting.DARK_RED), true);
		}
	}
}






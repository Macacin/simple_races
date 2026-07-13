package net.simpleraces.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;

public class SelectedFairyProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player)
			_player.closeContainer();
		if (!SimpleracesModVariables.getPlayerVariables((entity)).selected) {
			SimpleracesModVariables.updatePlayerVariables(entity, capability -> {
				capability.fairy = true;
				capability.selected = true;
			});
			entity.getPersistentData().putInt("fairy_flight_ticks", 0);
			AttributeInstance maxHealthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
			if (maxHealthAttr != null) {
				double newMax = SimpleRPGRacesConfiguration.FAIRY_MAX_HEALTH.get();
				maxHealthAttr.setBaseValue(newMax);
				((Player) entity).setHealth((float) newMax);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide()) {
				_player.displayClientMessage(Component.translatable("message.simpleraces.race_selected.fairy").withStyle(ChatFormatting.YELLOW), true);
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.translatable("message.simpleraces.class_previously_set").withStyle(ChatFormatting.DARK_RED), true);
		}
	}
}






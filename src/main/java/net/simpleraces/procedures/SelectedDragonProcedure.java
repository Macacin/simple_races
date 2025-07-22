package net.simpleraces.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

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
		if (!(entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).selected) {
			{
				boolean _setval = true;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.dragon = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = true;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.selected = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A7e Selected Drakonid"), true);
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A74 Class Previously Set"), true);
		}
	}
}

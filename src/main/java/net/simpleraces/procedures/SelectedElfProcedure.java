package net.simpleraces.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class SelectedElfProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player)
			_player.closeContainer();
		if (!(entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).selected) {
			{
				boolean _setval = true;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.elf = _setval;
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
			AttributeInstance maxHealthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
			if (maxHealthAttr != null) {
				double newMax = SimpleRPGRacesConfiguration.ELF_MAX_HEALTH.get();
				maxHealthAttr.setBaseValue(newMax);
				((Player) entity).setHealth((float) newMax);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A7e Selected Elf"), true);
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= 20) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 10, false, false));
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A74 Class Previously Set"), true);
		}
	}
}

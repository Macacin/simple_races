package net.simpleraces.procedures;

import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.api.ScaleOperations;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class DeselectProcedure {
	public static void execute(Entity entity) {
		execute(entity, false);
	}

	public static void execute(Entity entity, boolean bypassRestrictions) {
		if (entity == null)
			return;
		if (bypassRestrictions || new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null;
				}
				return false;
			}
		}.checkGamemode(entity) || SimpleRPGRacesConfiguration.DESELECT.get()) {
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.dwarf = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.elf = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.orc = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.merfolk = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.dragon = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.selected = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.fairy = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.serpentin = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.werewolf = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.halfdead = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.aracha = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.gargoyle = false); }
			{ SimpleracesModVariables.updatePlayerVariables(entity, capability -> capability.human = false); }
			ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 1));
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 1));
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(MobEffects.NIGHT_VISION);
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= 20) {
				if (entity instanceof LivingEntity _entity)
					_entity.setHealth(20);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide()) {
				_player.displayClientMessage(Component.translatable("message.simpleraces.class_reset"), false);
				boolean canKeepVanillaFlight = _player.getAbilities().instabuild || _player.isSpectator();
				_player.getAbilities().flying = canKeepVanillaFlight && _player.getAbilities().flying;
				_player.getAbilities().mayfly = canKeepVanillaFlight;
				_player.onUpdateAbilities();
				entity.getPersistentData().putInt("fairy_flight_ticks", 0);
				entity.getPersistentData().putInt("fairy_falling_ticks", 0);
				entity.getPersistentData().remove("pst_fairy_extra_spent_ticks");
				entity.getPersistentData().remove("pst_fairy_wind_wings");
				entity.getPersistentData().remove("simpleraces_fairy_exhausted");
				entity.getPersistentData().remove("simpleraces_fairy_landed_after_exhaustion");
				SimpleracesModVariables.getHeat(_player).setOverheatTicks(0);
			}
		} else {
		}
	}
}





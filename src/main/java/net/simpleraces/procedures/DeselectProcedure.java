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
		if (entity == null)
			return;
		if (new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null;
				}
				return false;
			}
		}.checkGamemode(entity) || SimpleRPGRacesConfiguration.DESELECT.get()) {
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.dwarf = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.elf = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.orc = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.merfolk = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.dragon = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.selected = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.fairy = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.Serpentin = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.werewolf = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.halfdead = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = false;
				entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.aracha = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 1));
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 1));
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(MobEffects.NIGHT_VISION);
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= 20) {
				if (entity instanceof LivingEntity _entity)
					_entity.setHealth(20);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide()) {
				_player.displayClientMessage(Component.literal("Class Reset"), false);
				_player.getAbilities().flying = false;
				_player.getAbilities().mayfly = false;
				_player.onUpdateAbilities();
				_player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
					data.setOverheatTicks(0);
				});
			}
		} else {
		}
	}
}

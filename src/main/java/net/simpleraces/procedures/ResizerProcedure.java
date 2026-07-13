package net.simpleraces.procedures;

import net.minecraft.world.entity.player.Player;
import net.simpleraces.client.SyncVars;
import net.simpleraces.entity.WerewolfState;
import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.api.ScaleOperations;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ResizerProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (SimpleracesModVariables.getPlayerVariables(entity).orc && SimpleRPGRacesConfiguration.ORC_RESIZE.get()) {
			ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 1.2));
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 1.1));
		} else if (SimpleracesModVariables.getPlayerVariables(entity).elf && SimpleRPGRacesConfiguration.ELF_RESIZE.get()) {
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 0.9));
		} else if (SimpleracesModVariables.getPlayerVariables(entity).dwarf && SimpleRPGRacesConfiguration.DWARF_RESIZE.get()) {
			ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 0.8));
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 0.8));
		} else if (SimpleracesModVariables.getPlayerVariables(entity).fairy){
			ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 0.5));
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 0.5));
		} else if (SimpleracesModVariables.getPlayerVariables(entity).aracha){
			ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 0.75));
			ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 0.75));
		} else if (SimpleracesModVariables.getPlayerVariables(entity).werewolf){
			if(isWerewolfBeastForm(entity)){
				ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 1.25));
				ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 1.2));
			} else {
				ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale(), 1.0));
				ScaleTypes.WIDTH.getScaleData(entity).setTargetScale((float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(entity).getTargetScale(), 1.0));
			}
		}
	}

	private static boolean isWerewolfBeastForm(Entity entity) {
		if (!(entity instanceof Player player)) {
			return false;
		}

		if (entity.level().isClientSide()) {
			return SyncVars.werewolf;
		}

		return WerewolfState.isBeast(player);
	}
}






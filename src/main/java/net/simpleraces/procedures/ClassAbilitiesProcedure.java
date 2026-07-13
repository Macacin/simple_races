package net.simpleraces.procedures;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.procedures.race.DwarfRaceMechanics;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import javax.annotation.Nullable;

@EventBusSubscriber
public class ClassAbilitiesProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Entity player = event.getEntity();
		execute(event, player.level(), player.getX(), player.getY(), player.getZ(), player);
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (SimpleracesModVariables.getPlayerVariables(entity).dwarf && DwarfRaceMechanics.isUnderground(world, entity)
				&& SimpleRPGRacesConfiguration.DWARF_HASTE.get()) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()){
				_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 0, false, false));
				if(_entity.getY() <= 100) {
                    _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, false, false));
                }
			}
		} else if (SimpleracesModVariables.getPlayerVariables(entity).merfolk && entity.isInWater() && SimpleRPGRacesConfiguration.MERFOLK_CONDUIT_EFFECT.get()) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 20, 1, false, false));
		} else if (SimpleracesModVariables.getPlayerVariables(entity).dragon && SimpleRPGRacesConfiguration.DRAK_FIRE_RES.get()) {
		}
		if (SimpleracesModVariables.getPlayerVariables(entity).dwarf
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getDisplayName().getString()).contains("Bow") && SimpleRPGRacesConfiguration.DWARF_BOW_RESTRICT.get()) {
		} else if (SimpleracesModVariables.getPlayerVariables(entity).dwarf
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getDisplayName().getString()).contains("Bow") && SimpleRPGRacesConfiguration.DWARF_BOW_RESTRICT.get()) {
		}
	}
}






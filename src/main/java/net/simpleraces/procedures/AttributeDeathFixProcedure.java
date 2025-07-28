package net.simpleraces.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class AttributeDeathFixProcedure {
	@SubscribeEvent
	public static void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		Player player = (Player) entity;
		if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf) {
			AttributeInstance maxHealthAttr = ((Player)entity).getAttribute(Attributes.MAX_HEALTH);
			if (maxHealthAttr != null) {
				double newMax = SimpleRPGRacesConfiguration.DWARF_MAX_HEALTH.get();
				maxHealthAttr.setBaseValue(newMax);
				((Player)entity).setHealth((float) newMax);
			}
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).elf) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 10, false, false));
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).orc) {
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dragon) {
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).merfolk) {
			AttributeInstance healthAttr = ((Player)entity).getAttribute(Attributes.MAX_HEALTH);
			if (healthAttr != null) {
				double newMaxHealth = 18;
				healthAttr.setBaseValue(newMaxHealth);
				((Player)entity).setHealth((float) newMaxHealth);
			}
		} else if((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).aracha){
			AttributeInstance healthAttr = ((Player)entity).getAttribute(Attributes.MAX_HEALTH);
			if (healthAttr != null) {
				double newMaxHealth = 16;
				healthAttr.setBaseValue(newMaxHealth);
				((Player)entity).setHealth((float) newMaxHealth);
			}
		} else if((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).halfdead){
			AttributeInstance healthAttr = ((Player)entity).getAttribute(Attributes.MAX_HEALTH);
			if (healthAttr != null) {
				double newMaxHealth = 23;
				healthAttr.setBaseValue(newMaxHealth);
				((Player)entity).setHealth((float) newMaxHealth);
			}
		} else if((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).fairy){
			AttributeInstance healthAttr = ((Player)entity).getAttribute(Attributes.MAX_HEALTH);
			if (healthAttr != null) {
				double newMaxHealth = 8;
				healthAttr.setBaseValue(newMaxHealth);
				player.setHealth((float) newMaxHealth);
			}
			player.getAbilities().mayfly = true;
			player.getAbilities().setFlyingSpeed(0.025f);
			player.onUpdateAbilities();
		}
	}
}

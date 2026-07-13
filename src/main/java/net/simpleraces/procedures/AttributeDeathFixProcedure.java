package net.simpleraces.procedures;

import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import java.util.UUID;

@EventBusSubscriber
public class AttributeDeathFixProcedure {

    private static final UUID WEREWOLF_BEAST_HEALTH_UUID = UUID.fromString("9809562a-faa3-45f9-83a7-4eb9228b9c5b");
    private static final UUID WEREWOLF_HUMAN_HEALTH_UUID = UUID.fromString("c561d21a-47fd-40ed-ab8b-8d457f4c6557");

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
        if (SimpleracesModVariables.getPlayerVariables(entity).dwarf) {
            AttributeInstance maxHealthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double newMax = SimpleRPGRacesConfiguration.DWARF_MAX_HEALTH.get();
                maxHealthAttr.setBaseValue(newMax);
                ((Player) entity).setHealth((float) newMax);
            }
        } else if (SimpleracesModVariables.getPlayerVariables(entity).elf) {
            AttributeInstance maxHealthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double newMax = SimpleRPGRacesConfiguration.ELF_MAX_HEALTH.get();
                maxHealthAttr.setBaseValue(newMax);
                ((Player) entity).setHealth((float) newMax);
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 10, false, false));
        } else if (SimpleracesModVariables.getPlayerVariables(entity).orc) {
        } else if (SimpleracesModVariables.getPlayerVariables(entity).dragon) {
        } else if (SimpleracesModVariables.getPlayerVariables(entity).merfolk) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.MERFOLK_SURFACE_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if (SimpleracesModVariables.getPlayerVariables(entity).serpentin) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.SERPENTIN_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if (SimpleracesModVariables.getPlayerVariables(entity).aracha) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.ARACHA_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if (SimpleracesModVariables.getPlayerVariables(entity).halfdead) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.HALFDEAD_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if (SimpleracesModVariables.getPlayerVariables(entity).fairy) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.FAIRY_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                player.setHealth((float) newMaxHealth);
            }
            player.getAbilities().mayfly = true;
            player.getAbilities().setFlyingSpeed(0.025f * SimpleRPGRacesConfiguration.FAIRY_FLY_SPEED_MULTIPLY.get().floatValue());
            player.onUpdateAbilities();
        } else if (SimpleracesModVariables.getPlayerVariables(entity).gargoyle) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                healthAttr.setBaseValue(16.0);
                player.setHealth((float) healthAttr.getValue());
            }
        } else if (SimpleracesModVariables.getPlayerVariables(entity).werewolf) {
            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (WerewolfState.isBeast(player)) {
                if (healthAttr != null) {
                    net.simpleraces.util.AttributeCompat.removeModifier(healthAttr, WEREWOLF_HUMAN_HEALTH_UUID);
                    if (!net.simpleraces.util.AttributeCompat.hasModifier(healthAttr, WEREWOLF_BEAST_HEALTH_UUID)) {
                        healthAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_BEAST_HEALTH_UUID), SimpleRPGRacesConfiguration.WEREWOLF_BEAST_HEALTH_BONUS.get(), AttributeModifier.Operation.ADD_VALUE));
                    }
                    player.setHealth((float) healthAttr.getValue());
                }
            } else {
                if (healthAttr != null) {
                    net.simpleraces.util.AttributeCompat.removeModifier(healthAttr, WEREWOLF_BEAST_HEALTH_UUID);
                    if (!net.simpleraces.util.AttributeCompat.hasModifier(healthAttr, WEREWOLF_HUMAN_HEALTH_UUID)) {
                        healthAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_HUMAN_HEALTH_UUID), SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_HEALTH_PENALTY.get(), AttributeModifier.Operation.ADD_VALUE));
                    }
                    player.setHealth((float) healthAttr.getValue());
                }
            }
        }
    }
}







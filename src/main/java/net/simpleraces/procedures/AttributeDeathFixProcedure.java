package net.simpleraces.procedures;

import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.simpleraces.entity.WerewolfState;
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
import java.util.UUID;

@Mod.EventBusSubscriber
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
        if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf) {
            AttributeInstance maxHealthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double newMax = SimpleRPGRacesConfiguration.DWARF_MAX_HEALTH.get();
                maxHealthAttr.setBaseValue(newMax);
                ((Player) entity).setHealth((float) newMax);
            }
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).elf) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 10, false, false));
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).orc) {
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dragon) {
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).merfolk) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.MERFOLK_SURFACE_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).serpentin) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.SERPENTIN_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).aracha) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.ARACHA_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).halfdead) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.HALFDEAD_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                ((Player) entity).setHealth((float) newMaxHealth);
            }
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).fairy) {
            AttributeInstance healthAttr = ((Player) entity).getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                double newMaxHealth = SimpleRPGRacesConfiguration.FAIRY_MAX_HEALTH.get();
                healthAttr.setBaseValue(newMaxHealth);
                player.setHealth((float) newMaxHealth);
            }
            player.getAbilities().mayfly = true;
            player.getAbilities().setFlyingSpeed(0.025f * SimpleRPGRacesConfiguration.FAIRY_FLY_SPEED_MULTIPLY.get());
            player.onUpdateAbilities();
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).werewolf) {
            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (WerewolfState.isBeast(player)) {
                if (healthAttr != null) {
                    healthAttr.removeModifier(WEREWOLF_HUMAN_HEALTH_UUID);
                    if (healthAttr.getModifier(WEREWOLF_BEAST_HEALTH_UUID) == null) {
                        healthAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_BEAST_HEALTH_UUID,
                                "Werewolf beast health bonus",
                                SimpleRPGRacesConfiguration.WEREWOLF_BEAST_HEALTH_BONUS.get(),
                                AttributeModifier.Operation.ADDITION
                        ));
                    }
                    player.setHealth((float) healthAttr.getValue());
                }
            } else {
                if (healthAttr != null) {
                    healthAttr.removeModifier(WEREWOLF_BEAST_HEALTH_UUID);
                    if (healthAttr.getModifier(WEREWOLF_HUMAN_HEALTH_UUID) == null) {
                        healthAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_HUMAN_HEALTH_UUID,
                                "Werewolf human health penalty",
                                SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_HEALTH_PENALTY.get(),
                                AttributeModifier.Operation.ADDITION
                        ));
                    }
                    player.setHealth((float) healthAttr.getValue());
                }
            }
        }
    }
}
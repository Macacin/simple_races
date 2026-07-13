package net.simpleraces.procedures.race;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.network.SimpleracesModVariables;

import java.util.List;
import java.util.UUID;

public final class OrcRaceMechanics {
    private static final UUID ORC_DAMAGE_PENALTY_UUID = UUID.fromString("b4b4c6d8-7e9f-4a12-b3c4-556677889900");
    private static final UUID ORC_MAGIC_PENALTY_UUID = UUID.fromString("c5d4e6f8-2d3e-4f5a-b6c7-d8e9f0a1b2c3");
    private static final UUID ORC_LOW_HEALTH_BOOST_UUID = UUID.fromString("d6e7f8a9-b0c1-d2e3-f4e5-d6a728b9a0b1");

    private OrcRaceMechanics() {
    }

    public static void applyTick(Player player, SimpleracesModVariables.PlayerVariables vars) {
        AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(dmgAttr, ORC_DAMAGE_PENALTY_UUID);
            dmgAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(ORC_DAMAGE_PENALTY_UUID), SimpleRPGRacesConfiguration.ORC_DAMAGE_PENALTY.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        CompoundTag data = player.getPersistentData();
        long gameTime = player.level().getGameTime();
        boolean shamanRage = data.getLong("pst_orc_shaman_rage_until") >= gameTime;

        for (String attrName : new String[]{
                "spell_power", "fire_spell_power", "ice_spell_power", "lightning_spell_power", "holy_spell_power",
                "ender_spell_power", "blood_spell_power", "evocation_spell_power", "nature_spell_power", "eldritch_spell_power"
        }) {
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", attrName));
            if (attribute == null) {
                continue;
            }

            AttributeInstance instance = player.getAttributes().getInstance(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute));
            if (instance == null) {
                continue;
            }

            net.simpleraces.util.AttributeCompat.removeModifier(instance, ORC_MAGIC_PENALTY_UUID);
            if (!shamanRage) {
                instance.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(ORC_MAGIC_PENALTY_UUID), SimpleRPGRacesConfiguration.ORC_MAGIC_DAMAGE_PENALTY.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }

        if (dmgAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(dmgAttr, ORC_LOW_HEALTH_BOOST_UUID);
            if (player.getHealth() < 8.0f) {
                dmgAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(ORC_LOW_HEALTH_BOOST_UUID), SimpleRPGRacesConfiguration.ORC_RAGE.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }

        player.getFoodData().setExhaustion((float) (player.getFoodData().getExhaustionLevel()
                * SimpleRPGRacesConfiguration.ORC_EXHAUSTION_MULTIPLIER.get()));

        int currentHunger = player.getFoodData().getFoodLevel();
        if (currentHunger == 20 && vars.previousFoodLevel < 20) {
            triggerRage(player);
        }

        vars.previousFoodLevel = currentHunger;
        vars.syncPlayerVariables(player);

        boolean hasFervorNow = player.hasEffect(ModEffects.FERVOR);
        if (!hasFervorNow && vars.fervorStacks > 0) {
            boolean skipStupor = data.getBoolean("pst_orc_skip_stupor");
            if (!skipStupor) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        SimpleRPGRacesConfiguration.ORC_FERVOR_DEBUFF_DURATION.get(),
                        SimpleRPGRacesConfiguration.ORC_FERVOR_SLOWDOWN_LEVEL.get(),
                        false,
                        true
                ));
                data.putInt("orc_fervor_debuff_ticks", SimpleRPGRacesConfiguration.ORC_FERVOR_DEBUFF_DURATION.get());
            }

            vars.fervorStacks = 0;
            vars.syncPlayerVariables(player);

            if (!skipStupor) {
                player.playSound(SimpleracesMod.ORC_EXHAUSTION.get(), 1.0F, 1.0F);
            }
            data.remove("pst_orc_skip_stupor");
        }

        int debuffTicks = data.getInt("orc_fervor_debuff_ticks");
        if (debuffTicks > 0) {
            if (hasFervorNow) {
                data.putInt("orc_fervor_debuff_ticks", 0);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            } else {
                data.putInt("orc_fervor_debuff_ticks", debuffTicks - 1);
            }
        }
    }

    private static void triggerRage(Player player) {
        player.level().playSound(null, player.blockPosition(), SimpleracesMod.ORC_ROAR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.level().playSound(null, player.blockPosition(), SimpleracesMod.ORC_RAGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        List<LivingEntity> enemies = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(10),
                e -> e != player && e.isAlive() && RaceTargetingHelper.canApplyNegativeRaceEffect(player, e)
        );

        CompoundTag data = player.getPersistentData();
        boolean dumbPlan = data.getLong("pst_orc_dumb_plan_until") >= player.level().getGameTime();
        int stunDuration = 60 + (dumbPlan ? 40 : 0);

        for (LivingEntity entity : enemies) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, stunDuration, 255, false, false));
        }

        if (dumbPlan && player.getRandom().nextFloat() < 0.20f) {
            List<Player> allies = player.level().getEntitiesOfClass(
                    Player.class,
                    player.getBoundingBox().inflate(10),
                    ally -> ally != player && ally.isAlive()
            );
            for (Player ally : allies) {
                ally.getActiveEffects().stream()
                        .filter(effect -> !effect.getEffect().value().isBeneficial())
                        .map(MobEffectInstance::getEffect)
                        .toList()
                        .forEach(ally::removeEffect);
            }
        }

        player.getActiveEffects().stream()
                .filter(effect -> !effect.getEffect().value().isBeneficial())
                .forEach(effect -> player.removeEffect(effect.getEffect()));
        player.getPersistentData().putBoolean("orc_rage_strike", true);
    }
}







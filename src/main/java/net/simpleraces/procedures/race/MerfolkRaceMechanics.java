package net.simpleraces.procedures.race;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import java.util.UUID;

public final class MerfolkRaceMechanics {
    private static final UUID MERFOLK_LAND_DAMAGE_UUID = UUID.fromString("e7e3c8a0-1d2b-4c9f-a1be-123456789abc");
    private static final UUID MERFOLK_WATER_DAMAGE_UUID = UUID.fromString("d9f6b0c2-3e97-4a84-8f45-abcdef012345");
    private static final UUID MERFOLK_SWIM_SPEED_BOOST_UUID = UUID.fromString("a1a3b1e3-b0c9-d4e2-f3e2-d3a7abb3a0b1");
    private static final UUID MERFOLK_WATER_HEALTH_BOOST_UUID = UUID.fromString("a4a4b1e3-b0c4-d4e2-f3e2-d3a7a4b3a0b1");
    private static final UUID MERFOLK_LAND_SPEED_PENALTY_UUID = UUID.fromString("a1a1b1e9-b0c9-d4e2-f5e2-d3a7abb9a0b1");

    private MerfolkRaceMechanics() {
    }

    public static void apply(Player player) {
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimSpeedAttr = player.getAttribute(NeoForgeMod.SWIM_SPEED);

        if (healthAttr == null) {
            return;
        }

        if (player.isInWater()) {
            applyWaterHealthModifierWithRatioPreservation(player, healthAttr, true);

            if (dmgAttr != null) {
                net.simpleraces.util.AttributeCompat.removeModifier(dmgAttr, MERFOLK_LAND_DAMAGE_UUID);
                if (!net.simpleraces.util.AttributeCompat.hasModifier(dmgAttr, MERFOLK_WATER_DAMAGE_UUID)) {
                    dmgAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(MERFOLK_WATER_DAMAGE_UUID), SimpleRPGRacesConfiguration.MERFOLK_ATTACK_DAMAGE_WATER.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }

            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 0, false, false));

            if (swimSpeedAttr != null) {
                net.simpleraces.util.AttributeCompat.removeModifier(swimSpeedAttr, MERFOLK_SWIM_SPEED_BOOST_UUID);
                swimSpeedAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(MERFOLK_SWIM_SPEED_BOOST_UUID), SimpleRPGRacesConfiguration.MERFOLK_SWIM_SPEED.get() - 1.0, AttributeModifier.Operation.ADD_VALUE));
            }

            player.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SPEED,
                    20,
                    SimpleRPGRacesConfiguration.MERFOLK_DIG_SPEED_WATER.get(),
                    false,
                    false
            ));
            return;
        }

        applyWaterHealthModifierWithRatioPreservation(player, healthAttr, false);

        if (dmgAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(dmgAttr, MERFOLK_WATER_DAMAGE_UUID);
            if (!net.simpleraces.util.AttributeCompat.hasModifier(dmgAttr, MERFOLK_LAND_DAMAGE_UUID)) {
                dmgAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(MERFOLK_LAND_DAMAGE_UUID), SimpleRPGRacesConfiguration.MERFOLK_DAMAGE_PENALTY_SURFACE.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }

        if (speedAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(speedAttr, MERFOLK_LAND_SPEED_PENALTY_UUID);
            speedAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(MERFOLK_LAND_SPEED_PENALTY_UUID), SimpleRPGRacesConfiguration.MERFOLK_SURFACE_SPEED_PENALTY.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        if (swimSpeedAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(swimSpeedAttr, MERFOLK_SWIM_SPEED_BOOST_UUID);
        }
    }

    private static void applyWaterHealthModifierWithRatioPreservation(Player player, AttributeInstance healthAttr, boolean inWater) {
        boolean hasWaterBoost = net.simpleraces.util.AttributeCompat.hasModifier(healthAttr, MERFOLK_WATER_HEALTH_BOOST_UUID);
        if (inWater == hasWaterBoost) {
            return;
        }

        double oldMax = healthAttr.getValue();
        float oldHealth = player.getHealth();

        if (inWater) {
            healthAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(MERFOLK_WATER_HEALTH_BOOST_UUID), SimpleRPGRacesConfiguration.MERFOLK_WATER_HEALTH_BOOST.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else {
            net.simpleraces.util.AttributeCompat.removeModifier(healthAttr, MERFOLK_WATER_HEALTH_BOOST_UUID);
        }

        double newMax = healthAttr.getValue();
        if (oldMax <= 0.0D || newMax <= 0.0D) {
            if (player.getHealth() > newMax) {
                player.setHealth((float) newMax);
            }
            return;
        }

        float healthRatio = oldHealth / (float) oldMax;
        float newHealth = (float) Math.max(0.0D, Math.min(newMax, healthRatio * newMax));
        player.setHealth(newHealth);
    }
}







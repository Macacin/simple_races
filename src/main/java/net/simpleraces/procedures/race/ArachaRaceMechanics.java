package net.simpleraces.procedures.race;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;

import java.util.UUID;

public final class ArachaRaceMechanics {
    private static final UUID ARACHA_ATTACK_SPEED_BOOST_UUID = UUID.fromString("76557286-c292-421d-8c2e-f7b7bc77abd9");

    private ArachaRaceMechanics() {
    }

    public static void applyTick(Player player) {
        if (player.isInWater()) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    SimpleRPGRacesConfiguration.ARACHA_WATER_SLOWDOWN_DURATION.get(),
                    SimpleRPGRacesConfiguration.ARACHA_WATER_SLOWDOWN_AMPLIFIER.get(),
                    false,
                    false
            ));
        }

        AttributeInstance attackSpeedAttr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttr == null) {
            return;
        }

        net.simpleraces.util.AttributeCompat.removeModifier(attackSpeedAttr, ARACHA_ATTACK_SPEED_BOOST_UUID);
        attackSpeedAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(ARACHA_ATTACK_SPEED_BOOST_UUID), SimpleRPGRacesConfiguration.ARACHA_ATTACK_SPEED_BONUS.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    public static void onArmorWeightCalculation(Object event) {
        try {
            Class<?> eventClass = event.getClass();
            Object playerObj = eventClass.getMethod("getPlayer").invoke(event);
            if (!(playerObj instanceof Player player) || !SimpleracesModVariables.getPlayerVariables(player).aracha) {
                return;
            }

            int currentWeight = (int) eventClass.getMethod("getWeight").invoke(event);
            int modifiedWeight = Math.max(0, (int) Math.round(currentWeight * SimpleRPGRacesConfiguration.ARACHA_WEIGHT_MULTIPLIER.get()));
            eventClass.getMethod("setWeight", int.class).invoke(event, modifiedWeight);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}






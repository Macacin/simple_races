package net.simpleraces.procedures.race;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.entity.WerewolfState;

import java.util.UUID;

public final class WerewolfRaceMechanics {
    private static final UUID WEREWOLF_BEAST_DAMAGE_UUID = UUID.fromString("90e188f6-6f25-4b3f-b2bc-4f020d4e4bf7");
    private static final UUID WEREWOLF_HUMAN_DAMAGE_UUID = UUID.fromString("776ac817-8d9e-4ce3-8727-1ab84e7373fe");
    private static final UUID WEREWOLF_BEAST_SPEED_UUID = UUID.fromString("ca8e2dbb-0054-431b-96b5-d37a59397f75");
    private static final UUID WEREWOLF_HUMAN_SPEED_UUID = UUID.fromString("99044fb5-f130-41ea-9124-dc3d9a773586");
    private static final UUID WEREWOLF_BEAST_HEALTH_UUID = UUID.fromString("9809562a-faa3-45f9-83a7-4eb9228b9c5b");
    private static final UUID WEREWOLF_HUMAN_HEALTH_UUID = UUID.fromString("c561d21a-47fd-40ed-ab8b-8d457f4c6557");

    private WerewolfRaceMechanics() {
    }

    public static void applyTick(Player player) {
        AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);

        if (WerewolfState.isBeast(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 0, false, true));

            if (dmgAttr != null) {
                net.simpleraces.util.AttributeCompat.removeModifier(dmgAttr, WEREWOLF_HUMAN_DAMAGE_UUID);
                if (!net.simpleraces.util.AttributeCompat.hasModifier(dmgAttr, WEREWOLF_BEAST_DAMAGE_UUID)) {
                    dmgAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_BEAST_DAMAGE_UUID), SimpleRPGRacesConfiguration.WEREWOLF_BEAST_DAMAGE_BONUS.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }

            if (speedAttr != null) {
                net.simpleraces.util.AttributeCompat.removeModifier(speedAttr, WEREWOLF_HUMAN_SPEED_UUID);
                if (!net.simpleraces.util.AttributeCompat.hasModifier(speedAttr, WEREWOLF_BEAST_SPEED_UUID)) {
                    speedAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_BEAST_SPEED_UUID), SimpleRPGRacesConfiguration.WEREWOLF_BEAST_SPEED_BONUS.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }

            if (healthAttr != null) {
                net.simpleraces.util.AttributeCompat.removeModifier(healthAttr, WEREWOLF_HUMAN_HEALTH_UUID);
                if (!net.simpleraces.util.AttributeCompat.hasModifier(healthAttr, WEREWOLF_BEAST_HEALTH_UUID)) {
                    healthAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_BEAST_HEALTH_UUID), SimpleRPGRacesConfiguration.WEREWOLF_BEAST_HEALTH_BONUS.get(), AttributeModifier.Operation.ADD_VALUE));
                }
                player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
            }
            return;
        }

        if (dmgAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(dmgAttr, WEREWOLF_BEAST_DAMAGE_UUID);
            if (!net.simpleraces.util.AttributeCompat.hasModifier(dmgAttr, WEREWOLF_HUMAN_DAMAGE_UUID)) {
                dmgAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_HUMAN_DAMAGE_UUID), SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_DAMAGE_PENALTY.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }

        if (speedAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(speedAttr, WEREWOLF_BEAST_SPEED_UUID);
            if (!net.simpleraces.util.AttributeCompat.hasModifier(speedAttr, WEREWOLF_HUMAN_SPEED_UUID)) {
                speedAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_HUMAN_SPEED_UUID), SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_SPEED_PENALTY.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }

        if (healthAttr != null) {
            net.simpleraces.util.AttributeCompat.removeModifier(healthAttr, WEREWOLF_BEAST_HEALTH_UUID);
            if (!net.simpleraces.util.AttributeCompat.hasModifier(healthAttr, WEREWOLF_HUMAN_HEALTH_UUID)) {
                healthAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(WEREWOLF_HUMAN_HEALTH_UUID), SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_HEALTH_PENALTY.get(), AttributeModifier.Operation.ADD_VALUE));
            }
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }
    }
}






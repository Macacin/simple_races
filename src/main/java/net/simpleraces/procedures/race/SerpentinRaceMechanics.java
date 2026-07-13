package net.simpleraces.procedures.race;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;

public final class SerpentinRaceMechanics {
    private SerpentinRaceMechanics() {
    }

    public static void applyTick(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        for (MobEffectInstance inst : new ArrayList<>(player.getActiveEffects())) {
            String effectKey = "serpentin_shortened_" + BuiltInRegistries.MOB_EFFECT.getKey(inst.getEffect().value());
            if (!inst.getEffect().value().isBeneficial() && inst.getEffect().value() != MobEffects.POISON && inst.getEffect().value() != MobEffects.WITHER) {
                if (!persistentData.getBoolean(effectKey)) {
                    int newDuration = inst.getDuration() / 2;
                    if (newDuration > 0) {
                        player.removeEffect(inst.getEffect());
                        player.addEffect(new MobEffectInstance(inst.getEffect(), newDuration, inst.getAmplifier(), inst.isAmbient(), inst.isVisible(), inst.showIcon()));
                    }
                    persistentData.putBoolean(effectKey, true);
                }
            } else {
                persistentData.remove(effectKey);
            }
        }
    }
}






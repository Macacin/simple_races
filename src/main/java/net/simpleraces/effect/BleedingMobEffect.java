package net.simpleraces.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingMobEffect extends MobEffect {
    public BleedingMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        float dot = entity.getPersistentData().getFloat("simpleraces_bleed_dot");
        if (dot <= 0f) {
            dot = 0.5f + amplifier;
        }
        entity.hurt(entity.damageSources().magic(), dot);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}




package net.simpleraces.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingMobEffect extends MobEffect {
    public BleedingMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        float dot = entity.getPersistentData().getFloat("simpleraces_bleed_dot");

        // запасной вариант, если по какой-то причине не записалось
        if (dot <= 0f) dot = 0.5f + amplifier;

        entity.hurt(entity.damageSources().magic(), dot);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0; // раз в 2 секунды, как и было
    }

}
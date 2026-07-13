package net.simpleraces.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.entity.WerewolfState;

public class WerewolfTransformationEffect extends MobEffect {
    public WerewolfTransformationEffect() {
        super(MobEffectCategory.HARMFUL, 0x555555);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide) {
            WerewolfState.transformToBeast(player);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == 1200;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        if (entity instanceof Player player && !player.level().isClientSide) {
            WerewolfState.transformToHuman(player);
        }
    }
}




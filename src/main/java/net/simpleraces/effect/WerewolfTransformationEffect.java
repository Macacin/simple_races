package net.simpleraces.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.SimpleracesModVariables;

public class WerewolfTransformationEffect extends MobEffect {
    public WerewolfTransformationEffect() {
        super(MobEffectCategory.HARMFUL, 0x555555);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (!player.level().isClientSide) {
                WerewolfState.transformToBeast(player);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 1200;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity p_19469_, AttributeMap p_19470_, int p_19471_) {
        super.removeAttributeModifiers(p_19469_, p_19470_, p_19471_);
        if (p_19469_ instanceof Player player) {
            if (!player.level().isClientSide) {
                WerewolfState.transformToHuman(player);
            }
        }
    }
}

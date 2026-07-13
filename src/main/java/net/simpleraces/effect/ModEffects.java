package net.simpleraces.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.util.AttributeCompat;

import java.util.UUID;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SimpleracesMod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", BleedingMobEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> WEREWOLF_TRANSFORMATION = MOB_EFFECTS.register("werwolf_transformation", WerewolfTransformationEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> DEATH_MARK = MOB_EFFECTS.register("death_mark", DeathMarkEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> QUARRY_MARK = MOB_EFFECTS.register("quarry_mark", QuarryMarkEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> FERVOR = MOB_EFFECTS.register("fervor", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000) {
        private static final UUID ATTACK_SPEED_UUID = UUID.fromString("1f6c1b94-2d7d-42f8-bbdc-a4a58cebede3");

        @Override
        public void onEffectStarted(LivingEntity entity, int amplifier) {
            AttributeInstance speed = entity.getAttribute(Attributes.ATTACK_SPEED);
            AttributeCompat.removeModifier(speed, ATTACK_SPEED_UUID);
            AttributeCompat.addTransientModifier(speed, ATTACK_SPEED_UUID, (amplifier + 1) * 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }

        @Override
        public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
            AttributeCompat.removeModifier(entity.getAttribute(Attributes.ATTACK_SPEED), ATTACK_SPEED_UUID);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return false;
        }
    });

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }
}




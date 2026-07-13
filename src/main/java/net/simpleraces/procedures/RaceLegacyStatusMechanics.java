package net.simpleraces.procedures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.SimpleracesModVariables;

import java.util.List;

final class RaceLegacyStatusMechanics {
    private RaceLegacyStatusMechanics() {
    }

    static void onPotionUsed(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        LivingEntity entity = event.getEntity();
        if (!SimpleracesModVariables.getPlayerVariables(entity).serpentin) {
            return;
        }

        if (!(stack.getItem() instanceof PotionItem)) {
            return;
        }

        PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        List<MobEffectInstance> originalEffects = new java.util.ArrayList<>();
        potionContents.forEachEffect(originalEffects::add);
        for (MobEffectInstance effect : originalEffects) {
            if (!effect.getEffect().value().isBeneficial()) {
                continue;
            }

            var type = effect.getEffect();
            MobEffect typeValue = type.value();
            int amplifier = effect.getAmplifier();
            int duration = (int) (effect.getDuration() * SimpleRPGRacesConfiguration.SERPENTIN_POTION_DURATION_MULTIPLIER.get());

            if (typeValue == MobEffects.DAMAGE_BOOST) {
                amplifier += SimpleRPGRacesConfiguration.SERPENTIN_STRENGTH_AMPLIFIER_BONUS.get();
            } else if (typeValue == MobEffects.MOVEMENT_SPEED) {
                amplifier += SimpleRPGRacesConfiguration.SERPENTIN_SPEED_AMPLIFIER_BONUS.get();
            } else if (typeValue == MobEffects.JUMP) {
                amplifier += SimpleRPGRacesConfiguration.SERPENTIN_JUMP_AMPLIFIER_BONUS.get();
            }

            entity.addEffect(new MobEffectInstance(type, duration, amplifier, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
        }
    }

    static void onLivingUpdate(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (!(entity instanceof Player player) || player.level().isClientSide) {
            return;
        }

        if (SimpleracesModVariables.getPlayerVariables(player).serpentin) {
            if (entity.hasEffect(MobEffects.POISON)) {
                entity.removeEffect(MobEffects.POISON);
            }
            if (entity.hasEffect(MobEffects.WITHER)) {
                entity.removeEffect(MobEffects.WITHER);
            }
        } else if (SimpleracesModVariables.getPlayerVariables(player).werewolf) {
            if (player.level().isNight() && WerewolfState.isHuman(player)) {
                WerewolfState.transformToBeast(player);
            } else if (!player.level().isNight() && WerewolfState.isBeast(player)) {
                WerewolfState.transformToHuman(player);
            }
        }
    }

    static void enforceDragonExtinguish(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (!(entity instanceof Player player) || player.level().isClientSide) {
            return;
        }

        boolean isDragon = SimpleracesModVariables.getPlayerVariables(player).dragon;
        if (!isDragon) {
            return;
        }

        var data = SimpleracesModVariables.getHeat(player);
        int postOverheatTicks = data.getPostOverheatExtinguishTicks();
        if (postOverheatTicks <= 0) {
            return;
        }

        if (player.isOnFire()) {
            player.clearFire();
        }
        data.setPostOverheatExtinguishTicks(postOverheatTicks - 1);
    }

    static void onSerpentinEffectRemoved(MobEffectEvent.Remove event) {
        clearShortenedSerpentinEffect(event.getEntity(), event.getEffectInstance());
    }

    static void onSerpentinEffectExpired(MobEffectEvent.Expired event) {
        clearShortenedSerpentinEffect(event.getEntity(), event.getEffectInstance());
    }

    static void onClone(PlayerEvent.Clone event) {
        var oldStore = SimpleracesModVariables.getHeat(event.getOriginal());
        var newStore = SimpleracesModVariables.getHeat(event.getEntity());
        newStore.setHeat(oldStore.getHeat());
        newStore.setOverheated(oldStore.isOverheated());
        newStore.setOverheatTicks(oldStore.getOverheatTicks());
        newStore.setPostOverheatExtinguishTicks(oldStore.getPostOverheatExtinguishTicks());
    }

    private static void clearShortenedSerpentinEffect(LivingEntity entity, MobEffectInstance instance) {
        if (!(entity instanceof Player player) || player.level().isClientSide) {
            return;
        }

        boolean serpentin = SimpleracesModVariables.getPlayerVariables(player).serpentin;
        if (!serpentin || instance == null) {
            return;
        }

        var key = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());
        if (key != null) {
            player.getPersistentData().remove("serpentin_shortened_" + key);
        }
    }
}






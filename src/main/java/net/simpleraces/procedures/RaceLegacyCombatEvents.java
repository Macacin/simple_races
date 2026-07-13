package net.simpleraces.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.SyncHeatPacket;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.procedures.race.FairyRaceMechanics;
import net.simpleraces.procedures.race.DragonRaceMechanics;
import net.simpleraces.procedures.race.GargoyleRaceMechanics;
import net.simpleraces.procedures.race.HumanRaceMechanics;
import net.simpleraces.procedures.race.RaceTargetingHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class RaceLegacyCombatEvents {
    private static final Map<UUID, Map<MobEffect, Integer>> PRE_ATTACK_DEBUFFS = new HashMap<>();
    private static final String ARACHA_BITE_COUNT_TAG = "bite_count";
    private static final String ARACHA_BITE_COOLDOWN_UNTIL_TAG = "simpleraces_aracha_bite_cooldown_until";
    private static final String ARACHA_BITE_INTERNAL_DAMAGE_TAG = "simpleraces_aracha_bite_internal_damage";
    private static final int ARACHA_BITE_INTERNAL_COOLDOWN_TICKS = 20 * 4;

    private RaceLegacyCombatEvents() {
    }

    static void onLivingHurt(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (event.getSource().getEntity() instanceof Player attacker
                && event.getSource().getDirectEntity() == attacker
                && attacker != target
                && !attacker.getPersistentData().getBoolean(ARACHA_BITE_INTERNAL_DAMAGE_TAG)) {
            SimpleracesModVariables.PlayerVariables attackerVars = SimpleracesModVariables.getPlayerVariables(attacker);
            if (attackerVars.aracha) {
                handleArachaAttack(attacker, target);
            }
            if (attackerVars.human) {
                float humanBonus = HumanRaceMechanics.getMeleeDamageBonus(attacker);
                HumanRaceMechanics.onSuccessfulMeleeHit(attacker);
                event.setNewDamage(event.getNewDamage() * (1.0f + humanBonus));
            }
        }

        if (event.getSource().getEntity() instanceof Player attacker
                && SimpleracesModVariables.getPlayerVariables(attacker).serpentin) {
            boolean hasDebuff = false;
            for (MobEffectInstance effect : target.getActiveEffects()) {
                if (!effect.getEffect().value().isBeneficial()) {
                    hasDebuff = true;
                    break;
                }
            }
            float damage = event.getNewDamage();
            damage *= hasDebuff
                    ? SimpleRPGRacesConfiguration.SERPENTIN_DAMAGE_BONUS_WITH_DEBUFF.get()
                    : SimpleRPGRacesConfiguration.SERPENTIN_DAMAGE_PENALTY_WITHOUT_DEBUFF.get();
            event.setNewDamage(damage);
        } else if (event.getEntity() instanceof Player player
                && SimpleracesModVariables.getPlayerVariables(player).aracha) {
            DamageSource source = event.getSource();
            if (source.typeHolder().is(DamageTypes.IN_FIRE)) {
                event.setNewDamage((float) (event.getNewDamage() * SimpleRPGRacesConfiguration.ARACHA_FIRE_DAMAGE_MULTIPLIER.get()));
            }
        } else if (event.getEntity() instanceof Player player
                && SimpleracesModVariables.getPlayerVariables(player).werewolf) {
            if (player.getRandom().nextInt(20) == 0) {
                player.addEffect(new MobEffectInstance(
                        ModEffects.WEREWOLF_TRANSFORMATION,
                        SimpleRPGRacesConfiguration.WEREWOLF_BEAST_DURATION.get(),
                        0,
                        true,
                        false,
                        false
                ));
            }
        } else if (event.getEntity() instanceof Player player
                && SimpleracesModVariables.getPlayerVariables(player).dragon) {
            if (isDragonFireOrLavaDamage(event.getSource())) {
                event.setNewDamage(event.getNewDamage() * 0.5f);
            }
            var data = SimpleracesModVariables.getHeat(player);
            if (!data.isOverheated()) {
                data.setHeat(data.getHeat() + 2);
            }
        }

        if (event.getEntity() instanceof Player player) {
            SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);

            if (vars.gargoyle) {
                GargoyleRaceMechanics.onPlayerHurt(event, player);
                if (event.getSource().typeHolder().is(DamageTypes.FALL) && player.getPersistentData().getLong("simpleraces_gargoyle_no_fall_until") >= player.level().getGameTime()) {
                    event.setNewDamage(0.0f);
                    player.fallDistance = 0.0f;
                }
            }

            if (vars.orc) {
                CompoundTag persistentData = player.getPersistentData();
                int debuffTicks = persistentData.getInt("orc_fervor_debuff_ticks");
                if (debuffTicks > 0) {
                    event.setNewDamage((float) (event.getNewDamage() * SimpleRPGRacesConfiguration.ORC_FERVOR_INCOMING_DAMAGE_MULTIPLIER.get()));
                }
                if (event.getSource().typeHolder().is(DamageTypes.MAGIC)) {
                    float amount = (float) (event.getNewDamage() * SimpleRPGRacesConfiguration.ORC_INCOMING_MAGIC_DAMAGE_PENALTY.get());
                    long gameTime = player.level().getGameTime();
                    if (persistentData.getLong("pst_orc_shaman_rage_until") >= gameTime) {
                        amount *= 0.7f;
                    }
                    event.setNewDamage(amount);
                }
            }

            if (vars.halfdead && player.getRandom().nextFloat() < 0.15f) {
                float drainAmount = event.getNewDamage() * 0.3f;
                Entity sourceEntity = event.getSource().getEntity();
                if (sourceEntity instanceof LivingEntity attacker) {
                    if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, attacker)) {
                        return;
                    }
                    attacker.hurt(player.damageSources().magic(), drainAmount);
                    player.heal(drainAmount * 10);
                }
            }
        }
    }

    static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);
        if (player.level().isClientSide() || !(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            return;
        }

        if (vars.fairy) {
            FairyRaceMechanics.onAttackEntity(player, target);
        }
        if (vars.dragon) {
            DragonRaceMechanics.onAttackEntity(player, target);
        }
        if (vars.gargoyle) {
            GargoyleRaceMechanics.onAttackEntity(player, target);
        }

        if (vars.serpentin) {
            Map<MobEffect, Integer> currentDebuffs = new HashMap<>();
            for (MobEffectInstance inst : target.getActiveEffects()) {
                if (!inst.getEffect().value().isBeneficial()) {
                    currentDebuffs.put(inst.getEffect().value(), inst.getDuration());
                }
            }
            PRE_ATTACK_DEBUFFS.put(target.getUUID(), currentDebuffs);
            SimpleracesMod.queueServerWork(1, () -> {
                adjustDebuffDurations(target);
                PRE_ATTACK_DEBUFFS.remove(target.getUUID());
            });
        }
        if (vars.orc) {
            handleOrcAttack(player, target, vars);
        }
    }

    static void onLivingAttack(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        DamageSource source = event.getSource();
        if (source.typeHolder().is(DamageTypes.FALL) || source.typeHolder().is(DamageTypes.DROWN) || source.typeHolder().is(DamageTypes.IN_FIRE)) {
            return;
        }

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);

        if (vars.elf && vars.forestSpirits > 0) {
            double dodgeChance = SimpleRPGRacesConfiguration.ELF_DODGE_CHANCE_PER_SPIRIT.get() * vars.forestSpirits;
            RandomSource random = player.getRandom();
            if (random.nextDouble() < dodgeChance) {
                event.setCanceled(true);
                for (int i = 0; i < SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get(); i++) {
                    if (vars.spiritCooldowns[i] == 0) {
                        vars.spiritCooldowns[i] = SimpleRPGRacesConfiguration.ELF_SPIRIT_COOLDOWN_SECONDS.get() * 20;
                        vars.forestSpirits = Math.max(0, vars.forestSpirits - 1);
                        break;
                    }
                }
                player.level().playSound(null, player.blockPosition(), SimpleracesMod.ELF_DODGE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                vars.syncPlayerVariables(player);
            }
        }
    }

    static void onHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (SimpleracesModVariables.getPlayerVariables(player).halfdead) {
                event.setAmount(event.getAmount() * SimpleRPGRacesConfiguration.HALFDEAD_HEAL_MULTIPLIER.get().floatValue());
            }
        }
    }

    static void onLivingHurtForBleed(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide() || !(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        var vars = SimpleracesModVariables.getPlayerVariables(player);
        if (!vars.werewolf || player.getRandom().nextInt(10) != 0) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            return;
        }
        float bleedDot = event.getNewDamage() * 0.15f;
        target.getPersistentData().putFloat("simpleraces_bleed_dot", bleedDot);
        target.addEffect(new MobEffectInstance(
                ModEffects.BLEEDING,
                SimpleRPGRacesConfiguration.WEREWOLF_BLEEDING_DURATION.get(),
                0,
                true,
                true
        ));
    }

    static void onBleedRemoved(MobEffectEvent.Remove event) {
        if (!event.getEntity().level().isClientSide() && event.getEffectInstance() != null
                && event.getEffectInstance().getEffect().value() == ModEffects.BLEEDING.get()) {
            event.getEntity().getPersistentData().remove("simpleraces_bleed_dot");
        }
    }

    static void onBleedExpired(MobEffectEvent.Expired event) {
        if (!event.getEntity().level().isClientSide() && event.getEffectInstance() != null
                && event.getEffectInstance().getEffect().value() == ModEffects.BLEEDING.get()) {
            event.getEntity().getPersistentData().remove("simpleraces_bleed_dot");
        }
    }

    static void onMobKilled(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide() || !(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }

        SimpleracesModVariables.updatePlayerVariables(killer, vars -> {
            if (!vars.halfdead) {
                return;
            }

            addHealthDirect(killer, event.getEntity().getMaxHealth() * 0.10f);

            Level level = killer.level();
            double radius = SimpleRPGRacesConfiguration.HALFDEAD_DEATH_MARK_RADIUS.get();
            List<LivingEntity> nearby = level.getEntitiesOfClass(
                    LivingEntity.class,
                    event.getEntity().getBoundingBox().inflate(radius),
                    e -> e.isAlive() && e != event.getEntity() && e != killer && !(e instanceof Player)
            );

            LivingEntity target = nearby.stream()
                    .filter(entity -> RaceTargetingHelper.canApplyNegativeRaceEffect(killer, entity))
                    .findFirst()
                    .orElse(null);

            if (target != null) {
                target.addEffect(new MobEffectInstance(
                        ModEffects.DEATH_MARK,
                        SimpleRPGRacesConfiguration.HALFDEAD_DEATH_MARK_DURATION.get()
                ));
                level.playSound(null, target.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1f);
            }
        });
    }

    static void onAttack(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player player && isRangedDamage(source)) {
            float multiplier = getRangedDamageMultiplier(player, event.getEntity());
            if (multiplier != 1.0f) {
                event.setNewDamage(event.getNewDamage() * multiplier);
            }
        }

        if (event.getSource().getEntity() instanceof Player player
                && SimpleracesModVariables.getPlayerVariables(player).dragon) {
            var data = SimpleracesModVariables.getHeat(player);
            if (data.isOverheated() && event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (RaceTargetingHelper.canApplyNegativeRaceEffect(player, attacker)) {
                    attacker.igniteForSeconds(4.0F);
                }
            }
            applyHeat(player);
        }

        if (event.getSource().getEntity() instanceof LivingEntity living && living.hasEffect(ModEffects.DEATH_MARK)) {
            event.setNewDamage((float) (event.getNewDamage() * SimpleRPGRacesConfiguration.HALFDEAD_DAMAGE_REDUCTION_FROM_MARK.get()));
        }
        if (event.getEntity().hasEffect(ModEffects.DEATH_MARK)) {
            event.setNewDamage((float) (event.getNewDamage() * SimpleRPGRacesConfiguration.HALFDEAD_DAMAGE_INCREASE_TO_MARKED.get()));
        }
        if (event.getEntity().getType().getCategory().equals(MobCategory.MONSTER)
                && event.getSource().getEntity() instanceof Player player
                && event.getEntity().getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
            if (SimpleracesModVariables.getPlayerVariables(player).halfdead) {
                event.setNewDamage((float) (event.getNewDamage() * SimpleRPGRacesConfiguration.HALFDEAD_DAMAGE_BONUS_VS_UNDEAD.get()));
            }
        }

        if (event.getSource().getEntity() instanceof Player player) {
            SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);
            if (vars.gargoyle) {
                GargoyleRaceMechanics.onDealtDamage(event, player);
            }
        }
    }

    private static boolean isRangedDamage(DamageSource source) {
        // Damage-type tags cover vanilla and correctly tagged modded ranged
        // weapons. The Projectile fallback also supports mods whose custom
        // projectile forgot to add that tag.
        return source.is(DamageTypeTags.IS_PROJECTILE)
                || source.getDirectEntity() instanceof Projectile;
    }

    private static float getRangedDamageMultiplier(Player player, LivingEntity target) {
        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            return 1.0f;
        }

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);

        if (vars.elf) {
            return 1.0f;
        }

        return 0.7f;
    }

    private static void handleArachaAttack(Player player, LivingEntity target) {
        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            return;
        }

        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = persistentData.contains(Player.PERSISTED_NBT_TAG)
                ? persistentData.getCompound(Player.PERSISTED_NBT_TAG)
                : new CompoundTag();
        if (!persistentData.contains(Player.PERSISTED_NBT_TAG)) {
            persistentData.put(Player.PERSISTED_NBT_TAG, data);
        }

        long gameTime = player.level().getGameTime();
        if (data.getLong(ARACHA_BITE_COOLDOWN_UNTIL_TAG) > gameTime) {
            return;
        }

        int count = data.getInt(ARACHA_BITE_COUNT_TAG) + 1;
        int threshold = persistentData.getBoolean("pst_aracha_mandible_cunning")
                ? 2
                : SimpleRPGRacesConfiguration.ARACHA_BITE_COUNT_THRESHOLD.get();

        if (count < threshold) {
            data.putInt(ARACHA_BITE_COUNT_TAG, count);
            return;
        }

        data.putInt(ARACHA_BITE_COUNT_TAG, 0);
        data.putLong(ARACHA_BITE_COOLDOWN_UNTIL_TAG, gameTime + ARACHA_BITE_INTERNAL_COOLDOWN_TICKS);
        spawnFangs(player.level(), target.getX(), target.getY(), target.getZ(), player.getYRot(), player);

        int poisonDuration = Math.round(SimpleRPGRacesConfiguration.ARACHA_POISON_DURATION.get() * 1.5f);
        int poisonAmplifier = SimpleRPGRacesConfiguration.ARACHA_POISON_AMPLIFIER.get();
        if (persistentData.getBoolean("pst_aracha_venom_fangs")) {
            poisonDuration = Math.round(poisonDuration * 1.5f);
            float extraDamage = (float) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.15f);
            if (extraDamage > 0.0f) {
                player.getPersistentData().putBoolean(ARACHA_BITE_INTERNAL_DAMAGE_TAG, true);
                try {
                    target.hurt(player.damageSources().playerAttack(player), extraDamage);
                } finally {
                    player.getPersistentData().remove(ARACHA_BITE_INTERNAL_DAMAGE_TAG);
                }
            }
        }

        target.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, poisonAmplifier));
        if (persistentData.getBoolean("pst_aracha_spider_magic")) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 6, 1));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 6, 1));
        }

        target.level().playSound(null, target.blockPosition(), SoundEvents.BEE_STING, SoundSource.PLAYERS, 1.0f, 1.0f);
        if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
        }
    }

    private static boolean isDragonFireOrLavaDamage(DamageSource source) {
        return source.typeHolder().is(DamageTypes.IN_FIRE)
                || source.typeHolder().is(DamageTypes.ON_FIRE)
                || source.typeHolder().is(DamageTypes.LAVA)
                || source.typeHolder().is(DamageTypes.HOT_FLOOR);
    }

    private static void handleOrcAttack(Player player, LivingEntity target, SimpleracesModVariables.PlayerVariables vars) {
        if (player.getPersistentData().getBoolean("orc_rage_strike")) {
            player.getPersistentData().remove("orc_rage_strike");
            float rageDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0f;
            List<LivingEntity> aoeTargets = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(3),
                    e -> e != player && RaceTargetingHelper.canApplyNegativeRaceEffect(player, e)
            );
            for (LivingEntity e : aoeTargets) {
                e.hurt(player.damageSources().playerAttack(player), rageDamage);
            }
        }

        UUID currentTarget = target.getUUID();
        if (vars.lastTarget == null || !vars.lastTarget.equals(currentTarget)) {
            vars.fervorStacks = 0;
            player.removeEffect(ModEffects.FERVOR);
        } else {
            CompoundTag persistentData = player.getPersistentData();
            long gameTime = player.level().getGameTime();
            int maxFervor = persistentData.getLong("pst_orc_berserk_power_until") >= gameTime ? 13 : 10;
            vars.fervorStacks = Math.min(maxFervor, vars.fervorStacks + 1);
        }
        vars.lastTarget = currentTarget;

        if (vars.fervorStacks > 0) {
            player.addEffect(new MobEffectInstance(ModEffects.FERVOR, 200, Math.min(vars.fervorStacks - 1, 9), false, false));
        }
        vars.syncPlayerVariables(player);
    }

    private static void spawnFangs(Level level, double x, double y, double z, float yaw, Player owner) {
        if (!level.isClientSide()) {
            level.addFreshEntity(new EvokerFangs(level, x, y, z, yaw, 0, owner));
        }
    }

    private static void addHealthDirect(ServerPlayer player, float amount) {
        if (amount <= 0f) {
            return;
        }
        player.setHealth(Math.min(player.getHealth() + amount, player.getMaxHealth()));
    }

    private static void applyHeat(Player player) {
        var data = SimpleracesModVariables.getHeat(player);
        if (data.isOverheated()) {
            return;
        }

        data.setHeat(data.getHeat() + SimpleRPGRacesConfiguration.DRAKONID_HEAT_PER_ATTACK.get());
        if (data.getHeat() >= SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get()) {
            data.setHeat(SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get());
            data.setOverheated(true);
            data.setOverheatTicks(SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_TIME.get() * 20);

            player.playSound(SimpleracesMod.DRAGON_OVERHEAT.get(), 1.0F, 1.0F);

            int maxHeat = SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get();
            int maxOverheatTicks = SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_TIME.get() * 20;
            ModMessages.sendToPlayer((ServerPlayer) player,
                    new SyncHeatPacket(player.getUUID(), data.getHeat(), maxHeat, data.isOverheated(), data.getOverheatTicks(), maxOverheatTicks));
        }
    }

    private static void adjustDebuffDurations(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return;
        }

        Map<MobEffect, Integer> pre = PRE_ATTACK_DEBUFFS.get(target.getUUID());
        if (pre == null) {
            return;
        }

        for (MobEffectInstance inst : new ArrayList<>(target.getActiveEffects())) {
            if (inst.getEffect().value().isBeneficial()) {
                continue;
            }

            MobEffect effect = inst.getEffect().value();
            int preDuration = pre.getOrDefault(effect, 0);
            int currentDuration = inst.getDuration();
            if (currentDuration > preDuration) {
                int newDuration = preDuration + (currentDuration - preDuration) * 2;
                target.removeEffect(inst.getEffect());
                target.addEffect(new MobEffectInstance(inst.getEffect(), newDuration, inst.getAmplifier(), inst.isAmbient(), inst.isVisible(), inst.showIcon()));
            }
        }
    }
}






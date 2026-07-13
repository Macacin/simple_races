package net.simpleraces.procedures.race;

import net.simpleraces.SimpleracesMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SyncGargoyleStancePacket;

import java.lang.reflect.Method;
import java.util.UUID;

public final class GargoyleRaceMechanics {
    public static final int STONEGUARD = 1;
    public static final int TALON = 2;
    public static final int SPIREWING = 3;
    public static final int GROTESQUE = 4;

    private static final String STANCE_TAG = "simpleraces_gargoyle_stance";
    private static final String STANCE_TICKS_TAG = "simpleraces_gargoyle_stance_ticks";
    private static final String STILL_TICKS_TAG = "simpleraces_gargoyle_still_ticks";
    private static final String CRACK_UNTIL_TAG = "simpleraces_gargoyle_crack_until";
    private static final String TRANSITION_TARGET_TAG = "simpleraces_gargoyle_transition_target";
    private static final String TRANSITION_UNTIL_TAG = "simpleraces_gargoyle_transition_until";
    private static final String LAST_ATTACK_TICK_TAG = "simpleraces_gargoyle_last_attack_tick";
    private static final String NO_FALL_UNTIL_TAG = "simpleraces_gargoyle_no_fall_until";
    private static final String GLIDE_UNTIL_GROUND_TAG = "simpleraces_gargoyle_glide_active";
    private static final String PROVOKED_TARGET_TAG = "simpleraces_gargoyle_provoked_target";
    private static final String PROVOKED_UNTIL_TAG = "simpleraces_gargoyle_provoked_until";
    private static final String RIPOSTE_UNTIL_TAG = "simpleraces_gargoyle_riposte_until";
    private static final String MARK_OWNER_TAG = "simpleraces_gargoyle_mark_owner";
    private static final String MARK_LEVEL_TAG = "simpleraces_gargoyle_mark_level";
    private static final String MARK_UNTIL_TAG = "simpleraces_gargoyle_mark_until";
    private static final String MARK_APPLIED_TICK_TAG = "simpleraces_gargoyle_mark_applied_tick";
    private static final String TALON_PRIMARY_TARGET_TAG = "simpleraces_gargoyle_talon_primary_target";
    private static final String LAST_PETRIFICATION_SOUND_STAGE_TAG = "simpleraces_gargoyle_last_petrification_sound_stage";
    private static final String STONEGUARD_REENTRY_UNTIL_TAG = "simpleraces_gargoyle_stoneguard_reentry_until";
    private static final String LAST_X_TAG = "simpleraces_gargoyle_last_x";
    private static final String LAST_Z_TAG = "simpleraces_gargoyle_last_z";
    private static final String STONEGUARD_ENTER_TICK_TAG = "simpleraces_gargoyle_stoneguard_enter_tick";

    private static final int OAKENING_I_TICKS = 20 * 20;
    private static final int OAKENING_II_TICKS = 20 * 40;
    private static final int STONEGUARD_TRANSITION_TICKS = 20 * 3;
    private static final int STONEGUARD_ENTRY_TICKS = 20 * 6;
    private static final int STONEGUARD_REENTRY_DELAY_TICKS = 20 * 3;

    private static final UUID BASE_SPEED_UUID = UUID.fromString("1a97eb7c-b611-41ab-a391-63f7c4a8b801");
    private static final UUID BASE_DAMAGE_UUID = UUID.fromString("81b92255-8572-4304-8e3d-03289960c3b1");
    private static final UUID STANCE_SPEED_UUID = UUID.fromString("fe96b706-850d-498c-8396-79338ec9b41a");
    private static final UUID STANCE_DAMAGE_UUID = UUID.fromString("740470f3-5ce8-4cba-83fe-24afb99698c0");
    private static final UUID STANCE_HEALTH_UUID = UUID.fromString("0bd0ce01-9c14-4ec3-894d-f55476d40ef6");
    private static final UUID STANCE_ATTACK_SPEED_UUID = UUID.fromString("687530ec-670e-4ec0-bcb0-8b2adfa1a6f8");
    private static Method enigmaticTransientGetMethod;
    private static Method enigmaticSetBoostingMethod;
    private static Method enigmaticSyncToAllClientsMethod;
    private static boolean enigmaticChecked;

    private GargoyleRaceMechanics() {
    }

    public static void applyTick(Player player) {
        var data = player.getPersistentData();
        boolean hasCreativeFlight = hasCreativeFlightPrivileges(player);
        int stance = getStance(player);
        if (stance == 0) {
            setStance(player, STONEGUARD);
            stance = STONEGUARD;
        }

        double lastX = data.contains(LAST_X_TAG) ? data.getDouble(LAST_X_TAG) : player.getX();
        double lastZ = data.contains(LAST_Z_TAG) ? data.getDouble(LAST_Z_TAG) : player.getZ();
        double movedX = Math.abs(player.getX() - lastX);
        double movedZ = Math.abs(player.getZ() - lastZ);
        data.putDouble(LAST_X_TAG, player.getX());
        data.putDouble(LAST_Z_TAG, player.getZ());

        boolean still = player.onGround()
                && !player.isSprinting()
                && movedX < 1.0e-4
                && movedZ < 1.0e-4
                && Math.abs(player.getDeltaMovement().x) < 0.0015
                && Math.abs(player.getDeltaMovement().z) < 0.0015
                && Math.abs(player.zza) < 1.0e-4f
                && Math.abs(player.xxa) < 1.0e-4f;

        int stillTicks = still ? data.getInt(STILL_TICKS_TAG) + 1 : 0;
        data.putInt(STILL_TICKS_TAG, stillTicks);

        if (stance != STONEGUARD
                && !hasTransition(player)
                && data.getLong(STONEGUARD_REENTRY_UNTIL_TAG) < player.level().getGameTime()
                && stillTicks >= STONEGUARD_ENTRY_TICKS) {
            setStance(player, STONEGUARD);
            stance = STONEGUARD;
        }

        if (isTransitionPending(player)) {
            if (player.level().getGameTime() >= data.getLong(TRANSITION_UNTIL_TAG)) {
                int targetStance = Math.max(STANCEGUARD_FALLBACK(), data.getInt(TRANSITION_TARGET_TAG));
                clearTransition(player);
                setStance(player, targetStance);
                stance = targetStance;
            }
        }

        if (stance == STONEGUARD) {
            if (hasTransition(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 2, false, false));
            } else if (shouldStartStoneguardBreak(player, still)) {
                startTransition(player, TALON);
            }
        }

        if (stance == GROTESQUE && hasProvokedTarget(player) && isRiposteExpired(player)) {
            applyCrackedVisage(player);
            clearRiposte(player);
            clearProvokedTarget(player);
            setStance(player, SPIREWING);
            stance = SPIREWING;
        }

        if (stance == SPIREWING && !hasCreativeFlight) {
            player.fallDistance = 0.0f;
            disableEnigmaticAngelBlessingBoost(player);
            if (data.getBoolean(GLIDE_UNTIL_GROUND_TAG) && !player.onGround() && !player.isInWater() && !player.isPassenger()) {
                player.startFallFlying();
            }
        } else {
            data.remove(GLIDE_UNTIL_GROUND_TAG);
        }

        if (player.onGround()) {
            data.remove(GLIDE_UNTIL_GROUND_TAG);
            if (!hasCreativeFlight && player.isFallFlying()) {
                player.stopFallFlying();
            }
            if (data.getLong(NO_FALL_UNTIL_TAG) > 0L && data.getLong(NO_FALL_UNTIL_TAG) < player.level().getGameTime()) {
                data.remove(NO_FALL_UNTIL_TAG);
            }
        }

        int stanceTicks = data.getInt(STANCE_TICKS_TAG) + 1;
        data.putInt(STANCE_TICKS_TAG, stanceTicks);
        int petrification = getPetrificationStage(stanceTicks);
        int lastPetrificationSoundStage = data.getInt(LAST_PETRIFICATION_SOUND_STAGE_TAG);
        if (petrification > lastPetrificationSoundStage) {
            if (petrification == 1) {
                playGargoyleSound(player, SimpleracesMod.GARGOYLE_PETRIFY_STAGE_1.get(), 1.0f, 0.9f);
            } else if (petrification == 2) {
                playGargoyleSound(player, SimpleracesMod.GARGOYLE_PETRIFY_STAGE_2.get(), 1.1f, 0.8f);
            }
            data.putInt(LAST_PETRIFICATION_SOUND_STAGE_TAG, petrification);
        } else if (petrification < lastPetrificationSoundStage) {
            data.putInt(LAST_PETRIFICATION_SOUND_STAGE_TAG, petrification);
        }
        if (petrification >= 1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, false, false));
        }
        if (petrification >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 0, false, false));
        }

        applyAttributeModifiers(player, stance);

        if (player instanceof ServerPlayer serverPlayer && player.tickCount % 5 == 0) {
            ModMessages.sendToPlayer(serverPlayer,
                    new SyncGargoyleStancePacket(player.getUUID(), stance, petrification, Math.min(stanceTicks, OAKENING_II_TICKS), OAKENING_II_TICKS));
        }
    }

    public static void onAttackEntity(Player player, LivingEntity target) {
        var data = player.getPersistentData();
        data.putInt(LAST_ATTACK_TICK_TAG, player.tickCount);

        if (isSprintAttack(player)) {
            if (getStance(player) == STONEGUARD) {
                if (!hasTransition(player)) {
                    startTransition(player, TALON);
                }
            } else {
                setStance(player, TALON);
            }
        } else if (!player.onGround()) {
            setStance(player, SPIREWING);
        }

        int stance = getStance(player);
        if (stance == TALON) {
            if (RaceTargetingHelper.canApplyNegativeRaceEffect(player, target) && shouldApplyTalonMark(player, target)) {
                applyMark(target, player, 1, 8 * 20);
            }
        } else if (stance == GROTESQUE
                && RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)
                && isProvokedTarget(player, target)
                && isRiposteOpen(player)) {
            triggerStoneMaw(player, target);
        }
    }

    public static void onPlayerHurt(LivingDamageEvent.Pre event, Player player) {
        int stance = getStance(player);
        if (stance == STONEGUARD) {
            event.setNewDamage(event.getNewDamage() * 0.6f);
        } else if (stance == TALON) {
            event.setNewDamage(event.getNewDamage() * 1.15f);
        } else if (stance == SPIREWING) {
            event.setNewDamage(event.getNewDamage() * 1.3f);
        }

        if (stance == TALON && event.getSource().getEntity() instanceof LivingEntity attacker && !hasMark(attacker, player)) {
            event.setNewDamage(event.getNewDamage() * 1.2f);
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (player.isCrouching() && player.tickCount - player.getPersistentData().getInt(LAST_ATTACK_TICK_TAG) > 30) {
            setStance(player, GROTESQUE);
            setProvokedTarget(player, attacker);
        }

        if (getStance(player) == GROTESQUE && isProvokedTarget(player, attacker) && !isRiposteOpen(player)) {
            event.setNewDamage(event.getNewDamage() * 0.6f);
            openRiposte(player);
        }
    }

    public static void onDealtDamage(LivingDamageEvent.Pre event, Player player) {
        LivingEntity target = event.getEntity();

        int stance = getStance(player);
        float amount = event.getNewDamage();
        int petrification = getPetrificationStage(player.getPersistentData().getInt(STANCE_TICKS_TAG));

        if (stance == STONEGUARD) {
            amount *= 0.6f;
        } else if (stance == TALON) {
            amount *= getTalonDamageMultiplier(player, target);
        } else if (stance == SPIREWING) {
            amount *= (!player.onGround() && player.getDeltaMovement().y < -0.08) ? 1.25f : 0.8f;
        } else if (stance == GROTESQUE) {
            amount *= 0.85f;
        }

        if (petrification >= 2) {
            amount *= 0.85f;
        }

        event.setNewDamage(amount);
    }

    private static void applyAttributeModifiers(Player player, int stance) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);

        double oldMax = health != null ? health.getValue() : player.getMaxHealth();
        float oldHealth = player.getHealth();

        removeModifier(speed, BASE_SPEED_UUID);
        removeModifier(damage, BASE_DAMAGE_UUID);
        removeModifier(speed, STANCE_SPEED_UUID);
        removeModifier(damage, STANCE_DAMAGE_UUID);
        removeModifier(health, STANCE_HEALTH_UUID);
        removeModifier(attackSpeed, STANCE_ATTACK_SPEED_UUID);

        addModifier(speed, BASE_SPEED_UUID, "Gargoyle base speed", -0.05);
        addModifier(damage, BASE_DAMAGE_UUID, "Gargoyle base damage", -0.10);

        switch (stance) {
            case STONEGUARD -> {
                addModifier(speed, STANCE_SPEED_UUID, "Gargoyle stoneguard speed", -0.63);
                addModifier(damage, STANCE_DAMAGE_UUID, "Gargoyle stoneguard damage", -0.4);
                addModifier(health, STANCE_HEALTH_UUID, "Gargoyle stoneguard health", 4.0, AttributeModifier.Operation.ADD_VALUE);
                addModifier(attackSpeed, STANCE_ATTACK_SPEED_UUID, "Gargoyle stoneguard attack speed", -0.2);
            }
            case TALON -> {
                addModifier(speed, STANCE_SPEED_UUID, "Gargoyle talon speed", 0.15);
                addModifier(damage, STANCE_DAMAGE_UUID, "Gargoyle talon damage", 0.0);
                addModifier(attackSpeed, STANCE_ATTACK_SPEED_UUID, "Gargoyle talon attack speed", 0.1);
            }
            case SPIREWING -> {
                addModifier(speed, STANCE_SPEED_UUID, "Gargoyle spirewing speed", 0.45);
                addModifier(damage, STANCE_DAMAGE_UUID, "Gargoyle spirewing damage", -0.1);
                addModifier(health, STANCE_HEALTH_UUID, "Gargoyle spirewing health", -6.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case GROTESQUE -> {
                addModifier(speed, STANCE_SPEED_UUID, "Gargoyle grotesque speed", -0.2);
                addModifier(damage, STANCE_DAMAGE_UUID, "Gargoyle grotesque damage", -0.05);
                addModifier(health, STANCE_HEALTH_UUID, "Gargoyle grotesque health", 2.0, AttributeModifier.Operation.ADD_VALUE);
            }
            default -> {
            }
        }

        if (health != null) {
            double newMax = health.getValue();
            if (Math.abs(newMax - oldMax) > 0.01) {
                float ratio = oldMax <= 0.0 ? 1.0f : oldHealth / (float) oldMax;
                player.setHealth((float) Math.max(1.0, Math.min(newMax, ratio * newMax)));
            }
        }
    }

    private static float getTalonDamageMultiplier(Player player, LivingEntity target) {
        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            return 1.0f;
        }
        if (!hasMark(target, player)) {
            return 1.0f;
        }
        if (wasMarkAppliedThisTick(target)) {
            return 1.0f;
        }

        int level = getMarkLevel(target, player);
        float distance = player.distanceTo(target);
        if (level >= 2) {
            if (distance <= 5.0f) {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, 0, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false));
                applyMark(target, player, 1, 8 * 20);
                launchRakeBounce(player, target);
                playTalonHitSound(player, true);
                return 1.3f;
            }
            return 1.0f;
        }

        if (distance < 1.0f) {
            return 0.75f;
        }
        if (distance <= 2.5f) {
            return 1.0f;
        }
        if (distance <= 4.5f) {
            applyMark(target, player, 1, 8 * 20);
            launchRakeBounce(player, target);
            playTalonHitSound(player, true);
            return 1.3f;
        }

        clearMark(target);
        return 1.0f;
    }

    private static void triggerStoneMaw(Player player, LivingEntity target) {
        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            clearRiposte(player);
            clearProvokedTarget(player);
            return;
        }
        clearRiposte(player);
        clearProvokedTarget(player);
        playGargoyleSound(player, SimpleracesMod.GARGOYLE_PARRY.get(), 1.15f, 1.0f);
        if (target instanceof PathfinderMob mob) {
            mob.setTarget(null);
            Vec3 away = target.position().subtract(player.position()).normalize().scale(6);
            mob.getNavigation().moveTo(target.getX() + away.x, target.getY(), target.getZ() + away.z, 1.3);
        }
        target.knockback(1.2f, player.getX() - target.getX(), player.getZ() - target.getZ());
        applyMark(target, player, 2, 10 * 20);
        setStance(player, TALON);
    }

    private static void applyCrackedVisage(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, false));
    }

    public static int getStance(Player player) {
        return player.getPersistentData().getInt(STANCE_TAG);
    }

    public static void setStance(Player player, int stance) {
        var data = player.getPersistentData();
        if (data.getInt(STANCE_TAG) != stance) {
            data.putInt(STANCE_TAG, stance);
            data.putInt(STANCE_TICKS_TAG, 0);
            data.putInt(STILL_TICKS_TAG, 0);
            data.putDouble(LAST_X_TAG, player.getX());
            data.putDouble(LAST_Z_TAG, player.getZ());
            data.remove(CRACK_UNTIL_TAG);
            data.remove(TALON_PRIMARY_TARGET_TAG);
            data.putInt(LAST_PETRIFICATION_SOUND_STAGE_TAG, 0);
            if (stance == STONEGUARD) {
                data.putLong(STONEGUARD_ENTER_TICK_TAG, player.level().getGameTime());
            } else {
                data.remove(STONEGUARD_ENTER_TICK_TAG);
            }
            if (stance != STONEGUARD) {
                data.putLong(STONEGUARD_REENTRY_UNTIL_TAG, player.level().getGameTime() + STONEGUARD_REENTRY_DELAY_TICKS);
            }
            clearTransition(player);
            playStanceEnterSound(player, stance);
        } else if (data.getInt(STANCE_TAG) == 0) {
            data.putInt(STANCE_TAG, stance);
            data.putInt(LAST_PETRIFICATION_SOUND_STAGE_TAG, 0);
            if (stance == STONEGUARD) {
                data.putLong(STONEGUARD_ENTER_TICK_TAG, player.level().getGameTime());
            }
            playStanceEnterSound(player, stance);
        }
    }

    private static int getPetrificationStage(int stanceTicks) {
        if (stanceTicks >= OAKENING_II_TICKS) {
            return 2;
        }
        if (stanceTicks >= OAKENING_I_TICKS) {
            return 1;
        }
        return 0;
    }

    private static void setProvokedTarget(Player player, LivingEntity attacker) {
        var data = player.getPersistentData();
        data.putUUID(PROVOKED_TARGET_TAG, attacker.getUUID());
        data.putLong(PROVOKED_UNTIL_TAG, player.level().getGameTime() + 100);
        data.remove(RIPOSTE_UNTIL_TAG);
    }

    private static boolean hasProvokedTarget(Player player) {
        var data = player.getPersistentData();
        return data.hasUUID(PROVOKED_TARGET_TAG) && data.getLong(PROVOKED_UNTIL_TAG) >= player.level().getGameTime();
    }

    private static boolean isProvokedTarget(Player player, LivingEntity entity) {
        var data = player.getPersistentData();
        return data.hasUUID(PROVOKED_TARGET_TAG)
                && data.getUUID(PROVOKED_TARGET_TAG).equals(entity.getUUID())
                && data.getLong(PROVOKED_UNTIL_TAG) >= player.level().getGameTime();
    }

    private static void clearProvokedTarget(Player player) {
        var data = player.getPersistentData();
        data.remove(PROVOKED_TARGET_TAG);
        data.remove(PROVOKED_UNTIL_TAG);
    }

    private static void openRiposte(Player player) {
        player.getPersistentData().putLong(RIPOSTE_UNTIL_TAG, player.level().getGameTime() + 20);
    }

    private static boolean isRiposteOpen(Player player) {
        return player.getPersistentData().getLong(RIPOSTE_UNTIL_TAG) >= player.level().getGameTime();
    }

    private static boolean isRiposteExpired(Player player) {
        long riposteUntil = player.getPersistentData().getLong(RIPOSTE_UNTIL_TAG);
        return riposteUntil > 0L && riposteUntil < player.level().getGameTime();
    }

    private static void clearRiposte(Player player) {
        player.getPersistentData().remove(RIPOSTE_UNTIL_TAG);
    }

    private static void applyMark(LivingEntity target, Player player, int level, int duration) {
        if (!RaceTargetingHelper.canApplyNegativeRaceEffect(player, target)) {
            return;
        }
        var data = target.getPersistentData();
        data.putUUID(MARK_OWNER_TAG, player.getUUID());
        data.putInt(MARK_LEVEL_TAG, level);
        data.putLong(MARK_UNTIL_TAG, target.level().getGameTime() + duration);
        data.putLong(MARK_APPLIED_TICK_TAG, target.level().getGameTime());
        target.addEffect(new MobEffectInstance(ModEffects.QUARRY_MARK, duration, Math.max(0, level - 1), false, true, true));
    }

    private static boolean hasMark(LivingEntity target, Player player) {
        var data = target.getPersistentData();
        return data.hasUUID(MARK_OWNER_TAG)
                && data.getUUID(MARK_OWNER_TAG).equals(player.getUUID())
                && data.getLong(MARK_UNTIL_TAG) >= target.level().getGameTime()
                && target.hasEffect(ModEffects.QUARRY_MARK);
    }

    private static int getMarkLevel(LivingEntity target, Player player) {
        return hasMark(target, player) ? Math.max(1, target.getPersistentData().getInt(MARK_LEVEL_TAG)) : 0;
    }

    private static boolean wasMarkAppliedThisTick(LivingEntity target) {
        return target.getPersistentData().getLong(MARK_APPLIED_TICK_TAG) >= target.level().getGameTime();
    }

    private static void clearMark(LivingEntity target) {
        var data = target.getPersistentData();
        data.remove(MARK_OWNER_TAG);
        data.remove(MARK_LEVEL_TAG);
        data.remove(MARK_UNTIL_TAG);
        data.remove(MARK_APPLIED_TICK_TAG);
        target.removeEffect(ModEffects.QUARRY_MARK);
    }

    private static boolean shouldApplyTalonMark(Player player, LivingEntity target) {
        var data = player.getPersistentData();
        if (!data.hasUUID(TALON_PRIMARY_TARGET_TAG)) {
            data.putUUID(TALON_PRIMARY_TARGET_TAG, target.getUUID());
            return !hasMark(target, player);
        }

        if (data.getUUID(TALON_PRIMARY_TARGET_TAG).equals(target.getUUID())) {
            return !hasMark(target, player);
        }

        return !hasMark(target, player) && player.getRandom().nextFloat() < 0.13f;
    }

    private static boolean isSprintAttack(Player player) {
        return player.isSprinting() || player.getDeltaMovement().horizontalDistanceSqr() > 0.03;
    }

    private static boolean shouldStartStoneguardBreak(Player player, boolean still) {
        long enterTick = player.getPersistentData().getLong(STONEGUARD_ENTER_TICK_TAG);
        if (enterTick > 0L && player.level().getGameTime() - enterTick <= 2L) {
            return false;
        }
        return !still || player.isSprinting();
    }

    private static void startTransition(Player player, int targetStance) {
        var data = player.getPersistentData();
        data.putInt(TRANSITION_TARGET_TAG, targetStance);
        data.putLong(TRANSITION_UNTIL_TAG, player.level().getGameTime() + STONEGUARD_TRANSITION_TICKS);
        data.putLong(CRACK_UNTIL_TAG, data.getLong(TRANSITION_UNTIL_TAG));
        data.putInt(STILL_TICKS_TAG, 0);
    }

    private static boolean hasTransition(Player player) {
        return isTransitionPending(player) && player.getPersistentData().getLong(TRANSITION_UNTIL_TAG) > player.level().getGameTime();
    }

    private static boolean isTransitionPending(Player player) {
        return player.getPersistentData().contains(TRANSITION_TARGET_TAG);
    }

    private static void clearTransition(Player player) {
        var data = player.getPersistentData();
        data.remove(TRANSITION_TARGET_TAG);
        data.remove(TRANSITION_UNTIL_TAG);
    }

    private static int STANCEGUARD_FALLBACK() {
        return TALON;
    }

    private static void knockPlayerBack(Player player, LivingEntity target, double strength) {
        Vec3 away = player.position().subtract(target.position());
        if (away.lengthSqr() < 1.0e-4) {
            return;
        }
        away = away.normalize().scale(strength);
        player.setDeltaMovement(player.getDeltaMovement().add(away.x, 0.08, away.z));
        player.hurtMarked = true;
    }

    private static void launchRakeBounce(Player player, LivingEntity target) {
        Vec3 away = player.position().subtract(target.position());
        if (away.lengthSqr() < 1.0e-4) {
            away = new Vec3(0.0, 0.0, 1.0);
        }
        away = away.normalize().scale(0.27);
        player.setDeltaMovement(away.x, 0.82, away.z);
        player.fallDistance = 0.0f;
        player.hurtMarked = true;
        player.getPersistentData().putLong(NO_FALL_UNTIL_TAG, player.level().getGameTime() + 200);
    }

    private static void playTalonHitSound(Player player, boolean correct) {
        if (!correct) {
            return;
        }
        var sound = SimpleracesMod.GARGOYLE_TALON_CORRECT.get();
        float volume = 1.35f;
        float pitch = 1.0f;
        playGargoyleSound(player, sound, volume, pitch);
    }

    private static void playStanceEnterSound(Player player, int stance) {
        switch (stance) {
            case STONEGUARD -> playGargoyleSound(player, SimpleracesMod.GARGOYLE_STONEGUARD_ENTER.get(), 1.05f, 0.75f);
            case TALON -> playGargoyleSound(player, SimpleracesMod.GARGOYLE_TALON_ENTER.get(), 1.1f, 1.0f);
            case SPIREWING -> playGargoyleSound(player, SimpleracesMod.GARGOYLE_SPIREWING_ENTER.get(), 0.9f, 1.15f);
            case GROTESQUE -> playGargoyleSound(player, SimpleracesMod.GARGOYLE_GROTESQUE_ENTER.get(), 1.0f, 0.85f);
            default -> {
            }
        }
    }

    private static void playGargoyleSound(Player player, SoundEvent sound, float volume, float pitch) {
        player.playSound(sound, volume, pitch);
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                sound,
                SoundSource.PLAYERS,
                volume,
                pitch
        );
    }

    public static void onJump(Player player) {
        if (getStance(player) != STONEGUARD) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, Math.min(movement.y * 0.28, 0.12), movement.z);
        player.hurtMarked = true;
    }

    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (getStance(player) == SPIREWING || player.getPersistentData().getLong(NO_FALL_UNTIL_TAG) >= player.level().getGameTime()) {
            event.setDistance(0.0f);
            event.setDamageMultiplier(0.0f);
            event.setCanceled(true);
            player.fallDistance = 0.0f;
            if (player.onGround()) {
                player.getPersistentData().remove(NO_FALL_UNTIL_TAG);
            }
        }
    }

    public static void startManualGlide(Player player) {
        if (hasCreativeFlightPrivileges(player)
                || getStance(player) != SPIREWING
                || player.onGround()
                || player.isInWater()
                || player.isPassenger()) {
            return;
        }
        player.getPersistentData().putBoolean(GLIDE_UNTIL_GROUND_TAG, true);
        player.startFallFlying();
        player.fallDistance = 0.0f;
    }

    private static boolean hasCreativeFlightPrivileges(Player player) {
        return player.getAbilities().instabuild || player.isSpectator();
    }

    private static void disableEnigmaticAngelBlessingBoost(Player player) {
        if (!resolveEnigmaticMethods()) {
            return;
        }

        try {
            Object transientData = enigmaticTransientGetMethod.invoke(null, player);
            if (transientData == null) {
                return;
            }

            enigmaticSetBoostingMethod.invoke(transientData, false);
            if (player instanceof ServerPlayer) {
                enigmaticSyncToAllClientsMethod.invoke(transientData);
            }
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static boolean resolveEnigmaticMethods() {
        if (enigmaticChecked) {
            return enigmaticTransientGetMethod != null
                    && enigmaticSetBoostingMethod != null
                    && enigmaticSyncToAllClientsMethod != null;
        }

        enigmaticChecked = true;
        try {
            Class<?> transientDataClass = Class.forName("com.aizistral.enigmaticlegacy.objects.TransientPlayerData");
            enigmaticTransientGetMethod = transientDataClass.getMethod("get", Player.class);
            enigmaticSetBoostingMethod = transientDataClass.getMethod("setElytraBoosting", boolean.class);
            enigmaticSyncToAllClientsMethod = transientDataClass.getMethod("syncToAllClients");
        } catch (ReflectiveOperationException ignored) {
            enigmaticTransientGetMethod = null;
            enigmaticSetBoostingMethod = null;
            enigmaticSyncToAllClientsMethod = null;
        }

        return enigmaticTransientGetMethod != null
                && enigmaticSetBoostingMethod != null
                && enigmaticSyncToAllClientsMethod != null;
    }

    private static void removeModifier(AttributeInstance attribute, UUID uuid) {
        net.simpleraces.util.AttributeCompat.removeModifier(attribute, uuid);
    }

    private static void addModifier(AttributeInstance attribute, UUID uuid, String name, double amount) {
        addModifier(attribute, uuid, name, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static void addModifier(AttributeInstance attribute, UUID uuid, String name, double amount, AttributeModifier.Operation operation) {
        if (attribute != null && !net.simpleraces.util.AttributeCompat.hasModifier(attribute, uuid)) {
            attribute.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(uuid), amount, operation));
        }
    }
}






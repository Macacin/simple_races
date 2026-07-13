package net.simpleraces.procedures.race;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.init.SimpleracesDamageTypes;

import java.util.List;

public final class HalfdeadRaceMechanics {
    private HalfdeadRaceMechanics() {
    }

    public static void applyTick(Player player, Level level) {
        if (player.tickCount % 100 != 0) {
            return;
        }

        AABB aabb = new AABB(player.getX() - 5, player.getY() - 5, player.getZ() - 5, player.getX() + 5, player.getY() + 5, player.getZ() + 5);
        List<LivingEntity> nearbyMobs = level.getEntitiesOfClass(
                LivingEntity.class,
                aabb,
                e -> e != player
                        && e.isAlive()
                        && !(e instanceof Player)
                        && isHostileMob(e)
                        && RaceTargetingHelper.canApplyNegativeRaceEffect(player, e)
        );

        for (LivingEntity mob : nearbyMobs) {
            float damage = SimpleRPGRacesConfiguration.HALFDEAD_AURA_DAMAGE.get().floatValue();
            if (!hasClearAuraPath(level, player, mob)) {
                damage *= 0.2f;
            }

            mob.hurt(SimpleracesDamageTypes.halfdeadAuraDirect(level, player), damage);
            if (!mob.isAlive()) {
                applyAuraKillBenefits(player, level, mob);
            }

            double dx = mob.getX() - player.getX();
            double dz = mob.getZ() - player.getZ();
            double d2 = dx * dx + dz * dz;

            if (d2 < 1.0e-6) {
                var look = player.getLookAngle();
                mob.knockback(0.1f, look.x, look.z);
            } else {
                double invLen = 1.0 / Math.sqrt(d2);
                mob.knockback(0.25f, dx * invLen, dz * invLen);
            }
        }
    }

    private static boolean isHostileMob(LivingEntity entity) {
        return entity.getType().getCategory() == MobCategory.MONSTER || entity instanceof Enemy;
    }

    private static boolean hasClearAuraPath(Level level, Player player, LivingEntity mob) {
        if (player.hasLineOfSight(mob) || mob.hasLineOfSight(player)) {
            return true;
        }

        Vec3 from = player.getEyePosition();
        Vec3 to = mob.getEyePosition();
        BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.MISS;
    }

    private static void applyAuraKillBenefits(Player player, Level level, LivingEntity killedEntity) {
        player.heal(killedEntity.getMaxHealth() * 0.10f);

        double radius = SimpleRPGRacesConfiguration.HALFDEAD_DEATH_MARK_RADIUS.get();
        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class,
                killedEntity.getBoundingBox().inflate(radius),
                e -> e.isAlive() && e != killedEntity && e != player && !(e instanceof Player)
        );

        LivingEntity target = nearby.stream()
                .filter(entity -> RaceTargetingHelper.canApplyNegativeRaceEffect(player, entity))
                .findFirst()
                .orElse(null);

        if (target != null) {
            target.addEffect(new MobEffectInstance(
                    ModEffects.DEATH_MARK,
                    SimpleRPGRacesConfiguration.HALFDEAD_DEATH_MARK_DURATION.get()
            ));
            level.playSound(null, target.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1f);
        }
    }
}





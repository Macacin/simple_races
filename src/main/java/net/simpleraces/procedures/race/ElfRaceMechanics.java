package net.simpleraces.procedures.race;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;
import java.util.UUID;

public final class ElfRaceMechanics {
    private static final String ELF_FOREST_GRACE_TICKS = "simpleraces.elfForestGraceTicks";
    private static final UUID ELF_SPEED_UUID = UUID.fromString("f2b4c6d8-7e9f-4a12-b3c4-556677889901");
    private static final int FOREST_NEARBY_RADIUS = 5;
    private static final int FOREST_SAMPLE_STEP = 5;
    private static final int FOREST_BUFF_GRACE_TICKS = 20;
    private static final int FOREST_SCAN_RADIUS = 5;
    private static final int FOREST_SCAN_DEPTH = 1;
    private static final int FOREST_SCAN_HEIGHT = 8;
    private static final int MIN_LOG_BLOCKS = 6;
    private static final int MIN_LEAF_BLOCKS = 20;

    private ElfRaceMechanics() {
    }

    public static void applyTick(Player player, Level level, SimpleracesModVariables.PlayerVariables vars) {
        BlockPos pos = player.getOnPos();
        boolean nearForest = isForestNearby(level, pos);
        int forestGraceTicks = updateForestGraceTicks(player, nearForest);
        boolean forestBuffActive = forestGraceTicks > 0;

        if (level.getBrightness(LightLayer.SKY, pos.above(2)) > 0 && forestBuffActive) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, FOREST_BUFF_GRACE_TICKS, 0, false, true));
            AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null && !net.simpleraces.util.AttributeCompat.hasModifier(speedAttr, ELF_SPEED_UUID)) {
                speedAttr.addTransientModifier(new AttributeModifier(net.simpleraces.util.AttributeCompat.id(ELF_SPEED_UUID), SimpleRPGRacesConfiguration.ELF_FOREST_SPEED_BUFF.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        } else {
            AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                net.simpleraces.util.AttributeCompat.removeModifier(speedAttr, ELF_SPEED_UUID);
            }
        }

        double cooldownStep = forestBuffActive ? SimpleRPGRacesConfiguration.ELF_FOREST_COOLDOWN_MULTIPLIER.get() : 1;
        for (int i = 0; i < SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get(); i++) {
            if (vars.spiritCooldowns[i] > 0) {
                vars.spiritCooldowns[i] -= (int) cooldownStep;
                if (vars.spiritCooldowns[i] <= 0) {
                    vars.spiritCooldowns[i] = 0;
                    vars.forestSpirits = Math.min(SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get(), vars.forestSpirits + 1);
                    player.playSound(SimpleracesMod.FAIRY_RECOVER.get(), 0.5F, 1.0F);
                }
            }
        }

        vars.syncPlayerVariables(player);
    }

    public static void execute(Entity entity) {
        // Bow damage is handled centrally on projectile hit so it is consistent
        // for vanilla and modded arrows.
    }

    private static int updateForestGraceTicks(Player player, boolean nearForest) {
        if (nearForest) {
            player.getPersistentData().putInt(ELF_FOREST_GRACE_TICKS, FOREST_BUFF_GRACE_TICKS);
            return FOREST_BUFF_GRACE_TICKS;
        }

        int remainingTicks = Math.max(0, player.getPersistentData().getInt(ELF_FOREST_GRACE_TICKS) - 1);
        player.getPersistentData().putInt(ELF_FOREST_GRACE_TICKS, remainingTicks);
        return remainingTicks;
    }

    private static boolean isForestNearby(Level level, BlockPos pos) {
        for (int dx = -FOREST_NEARBY_RADIUS; dx <= FOREST_NEARBY_RADIUS; dx += FOREST_SAMPLE_STEP) {
            for (int dz = -FOREST_NEARBY_RADIUS; dz <= FOREST_NEARBY_RADIUS; dz += FOREST_SAMPLE_STEP) {
                if (hasForestDensity(level, pos.offset(dx, 0, dz))) {
                    return true;
                }
            }
        }

        return hasForestDensity(level, pos);
    }

    private static boolean hasForestDensity(Level level, BlockPos pos) {
        int leafBlocks = 0;
        int logBlocks = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -FOREST_SCAN_RADIUS; dx <= FOREST_SCAN_RADIUS; dx++) {
            for (int dz = -FOREST_SCAN_RADIUS; dz <= FOREST_SCAN_RADIUS; dz++) {
                for (int dy = -FOREST_SCAN_DEPTH; dy <= FOREST_SCAN_HEIGHT; dy++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    var state = level.getBlockState(cursor);

                    if (state.is(BlockTags.LEAVES)) {
                        leafBlocks++;
                    } else if (state.is(BlockTags.LOGS)) {
                        logBlocks++;
                    }

                    if (leafBlocks >= MIN_LEAF_BLOCKS && logBlocks >= MIN_LOG_BLOCKS) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void sendParticlesToOthers(ServerLevel level, ServerPlayer source, ParticleOptions particle, int count,
                                             double offsetX, double offsetY, double offsetZ, double speed) {
        for (ServerPlayer other : level.players()) {
            if (other == source || other.distanceToSqr(source) > 1024.0D) {
                continue;
            }

            level.sendParticles(other, particle, false,
                    source.getX(), source.getY() + 1, source.getZ(),
                    count, offsetX, offsetY, offsetZ, speed);
        }
    }
}






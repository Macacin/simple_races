package net.simpleraces.procedures.race;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;

public final class DwarfRaceMechanics {
    private static final int UNDERGROUND_SAMPLE_RADIUS = 2;
    private static final int MIN_COVER_DEPTH = 4;
    private static final int MIN_AVERAGE_DEPTH = 6;
    private static final int MIN_COVERED_COLUMNS = 15;

    private DwarfRaceMechanics() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }

        boolean underground = isUnderground(world, living);

        if (underground && y < 0) {
            living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20, 0, false, false));
        }

        living.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                20,
                (int) (double) SimpleRPGRacesConfiguration.DWARF_RES_LEVEL.get(),
                false,
                false
        ));

        if (underground
                && y < 0
                && SimpleRPGRacesConfiguration.DWARF_SUBZERO_EFFECTS.get()) {
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1, false, false));
        }
    }

    public static boolean isUnderground(LevelAccessor world, Entity entity) {
        if (world == null || entity == null) {
            return false;
        }

        BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
        if (world.canSeeSky(eyePos)) {
            return false;
        }

        int coveredColumns = 0;
        int totalDepth = 0;
        int sampledColumns = 0;

        for (int dx = -UNDERGROUND_SAMPLE_RADIUS; dx <= UNDERGROUND_SAMPLE_RADIUS; dx++) {
            for (int dz = -UNDERGROUND_SAMPLE_RADIUS; dz <= UNDERGROUND_SAMPLE_RADIUS; dz++) {
                int sampleX = eyePos.getX() + dx;
                int sampleZ = eyePos.getZ() + dz;
                int surfaceY = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sampleX, sampleZ);
                int depth = Math.max(0, surfaceY - eyePos.getY());

                totalDepth += depth;
                sampledColumns++;

                if (depth >= MIN_COVER_DEPTH) {
                    coveredColumns++;
                }
            }
        }

        return coveredColumns >= MIN_COVERED_COLUMNS
                && totalDepth >= sampledColumns * MIN_AVERAGE_DEPTH;
    }

    public static void onShoot(ArrowLooseEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide() || !isDwarf(player)) {
            return;
        }

        event.setCharge((int) (event.getCharge() * 0.3f));
        player.getPersistentData().putBoolean("dwarf_shot_arrow", true);
        player.getPersistentData().putInt("dwarf_arrow_tick", player.tickCount);
    }

    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player player) || player.level().isClientSide() || !isDwarf(player)) {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        if (!playerData.getBoolean("dwarf_shot_arrow")) {
            return;
        }

        int shotTick = playerData.getInt("dwarf_arrow_tick");
        if (player.tickCount - shotTick > 5) {
            playerData.remove("dwarf_shot_arrow");
            playerData.remove("dwarf_arrow_tick");
            return;
        }

        playerData.remove("dwarf_shot_arrow");
        playerData.remove("dwarf_arrow_tick");

        double spread = 0.35D;
        Vec3 motion = arrow.getDeltaMovement();
        arrow.setDeltaMovement(
                motion.x + (player.getRandom().nextDouble() - 0.5) * spread,
                motion.y + (player.getRandom().nextDouble() - 0.5) * spread * 0.7,
                motion.z + (player.getRandom().nextDouble() - 0.5) * spread
        );
    }

    public static void onCrossbowShoot(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player player) || player.level().isClientSide() || !isDwarf(player)) {
            return;
        }

        Vec3 velocity = arrow.getDeltaMovement();
        if (velocity.length() <= 2.5) {
            return;
        }

        arrow.setDeltaMovement(velocity.scale(0.3));
        Vec3 motion = arrow.getDeltaMovement();
        double spread = 0.45D;
        arrow.setDeltaMovement(
                motion.x + (player.getRandom().nextDouble() - 0.5) * spread,
                motion.y + (player.getRandom().nextDouble() - 0.5) * spread * 0.6,
                motion.z + (player.getRandom().nextDouble() - 0.5) * spread
        );
    }

    public static void onAiming(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (!isDwarf(player) || !player.isUsingItem()) {
            return;
        }

        ItemStack using = player.getUseItem();
        if (!(using.getItem() instanceof BowItem || using.getItem() instanceof CrossbowItem)) {
            return;
        }

        if (player.tickCount % 5 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, false, false));
        }

        if (player.tickCount % 20 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, false, false));
        }

        if (player.tickCount % 3 == 0) {
            float yawJitter = (player.getRandom().nextFloat() - 0.5f) * 8.0f;
            float pitchJitter = (player.getRandom().nextFloat() - 0.5f) * 5.0f;
            player.setYRot(player.getYRot() + yawJitter);
            player.setXRot(net.minecraft.util.Mth.clamp(player.getXRot() + pitchJitter, -90, 90));
        }
    }

    private static boolean isDwarf(Player player) {
        return SimpleracesModVariables.getPlayerVariables(player).dwarf;
    }
}






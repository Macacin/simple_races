package net.simpleraces.procedures.race;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.SyncFairyFlightPacket;

import java.util.Locale;

public final class FairyRaceMechanics {
    private static final String FAIRY_TARGET_RESETTING_TAG = "simpleraces_fairy_target_resetting";
    private static final String FAIRY_ATTACKER_TAG = "simpleraces_fairy_attacker";
    private static final String FAIRY_ATTACKER_EXPIRES_TAG = "simpleraces_fairy_attacker_expires";
    private static final String FAIRY_EXHAUSTED_TAG = "simpleraces_fairy_exhausted";
    private static final String FAIRY_LANDED_AFTER_EXHAUSTION_TAG = "simpleraces_fairy_landed_after_exhaustion";
    private static final int FAIRY_ATTACKER_MEMORY_TICKS = 20 * 15;

    private FairyRaceMechanics() {
    }

    public static void applyTick(Player player) {
        var data = player.getPersistentData();
        int flightTime = data.getInt("fairy_flight_ticks");
        int fallingTime = data.getInt("fairy_falling_ticks");
        int baseMax = SimpleRPGRacesConfiguration.FAIRY_MAX_FLYING_TIME.get() * 20;
        boolean hasCreativeFlight = hasCreativeFlightPrivileges(player);

        final String tagWindWings = "pst_fairy_wind_wings";
        final String tagExtraSpent = "pst_fairy_extra_spent_ticks";
        final int totalMaxTicks = 30 * 20;

        boolean windWingsEnabled = data.getBoolean(tagWindWings);
        int extraCap = Math.max(0, totalMaxTicks - baseMax);
        int effectiveMax = windWingsEnabled ? baseMax + extraCap : baseMax;

        int extraSpent = Math.max(0, Math.min(data.getInt(tagExtraSpent), extraCap));

        if (player.getAbilities().flying && !hasCreativeFlight) {
            if (flightTime < baseMax) {
                flightTime++;
            } else if (windWingsEnabled && extraSpent < extraCap) {
                extraSpent++;
                data.putInt(tagExtraSpent, extraSpent);
            }
        }

        int spent = Math.min(flightTime, baseMax) + extraSpent;
        boolean isRecovering = data.getBoolean(FAIRY_EXHAUSTED_TAG) || spent >= effectiveMax;
        if (spent >= effectiveMax) {
            data.putBoolean(FAIRY_EXHAUSTED_TAG, true);
        }
        int hudBar = isRecovering ? fallingTime : spent;
        int hudMax = isRecovering ? baseMax : effectiveMax;

        if (hasCreativeFlight) {
            player.getAbilities().mayfly = true;
            data.putBoolean(FAIRY_LANDED_AFTER_EXHAUSTION_TAG, false);
            data.putBoolean(FAIRY_EXHAUSTED_TAG, false);
        } else if (isRecovering) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();

            boolean landedAfterExhaustion = data.getBoolean(FAIRY_LANDED_AFTER_EXHAUSTION_TAG);
            if (!landedAfterExhaustion) {
                if (player.onGround()) {
                    landedAfterExhaustion = true;
                    data.putBoolean(FAIRY_LANDED_AFTER_EXHAUSTION_TAG, true);
                } else {
                    // Keep the landing protected across tick boundaries. A two-tick
                    // effect could expire immediately before the ground collision was
                    // processed, turning the exhausted fairy's gentle descent into a
                    // full fall-damage hit.
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 0, false, true));
                    player.fallDistance = 0.0F;
                }
            }

            if (landedAfterExhaustion) {
                fallingTime++;
            }

            if (fallingTime >= baseMax) {
                flightTime = 0;
                fallingTime = 0;
                data.putInt(tagExtraSpent, 0);
                data.putBoolean(FAIRY_EXHAUSTED_TAG, false);
                data.putBoolean(FAIRY_LANDED_AFTER_EXHAUSTION_TAG, false);
                player.playSound(SimpleracesMod.FAIRY_RECOVER.get(), 1.0F, 1.0F);
            }
        } else {
            player.getAbilities().mayfly = true;
            data.putBoolean(FAIRY_LANDED_AFTER_EXHAUSTION_TAG, false);
            data.putBoolean(FAIRY_EXHAUSTED_TAG, false);
            player.getAbilities().setFlyingSpeed(0.025f * SimpleRPGRacesConfiguration.FAIRY_FLY_SPEED_MULTIPLY.get().floatValue());
            player.onUpdateAbilities();
        }

        data.putInt("fairy_flight_ticks", flightTime);
        data.putInt("fairy_falling_ticks", fallingTime);

        ModMessages.sendToPlayer((ServerPlayer) player,
                new SyncFairyFlightPacket(player.getUUID(), hudBar, hudMax, isRecovering));
    }

    private static boolean hasCreativeFlightPrivileges(Player player) {
        return player.getAbilities().instabuild || player.isSpectator();
    }

    public static void onFall(LivingFallEvent event) {
        CompoundTag data = event.getEntity().getPersistentData();
        if (data.getBoolean(FAIRY_EXHAUSTED_TAG)
                && !data.getBoolean(FAIRY_LANDED_AFTER_EXHAUSTION_TAG)) {
            event.setCanceled(true);
            return;
        }
        event.setDamageMultiplier(SimpleRPGRacesConfiguration.FAIRY_FALL_MULTIPLY.get().floatValue());
    }

    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Animal mob)) {
            return;
        }
        if (shouldIgnoreFairyPacification(mob)) {
            return;
        }
        if (mob.getPersistentData().getBoolean(FAIRY_TARGET_RESETTING_TAG)) {
            return;
        }
        if (!(event.getNewAboutToBeSetTarget() instanceof Player player)) {
            return;
        }

        boolean isFairy = SimpleracesModVariables.getPlayerVariables(player).fairy;
        if (!isFairy) {
            return;
        }
        if (wasRecentlyAttackedByFairy(mob, player)) {
            return;
        }

        event.setCanceled(true);
        mob.getPersistentData().putBoolean(FAIRY_TARGET_RESETTING_TAG, true);
        try {
            mob.setTarget(null);
        } finally {
            mob.getPersistentData().remove(FAIRY_TARGET_RESETTING_TAG);
        }
	}

	private static boolean shouldIgnoreFairyPacification(Animal mob) {
		ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
		if (entityId != null
				&& "iceandfire".equals(entityId.getNamespace())
				&& entityId.getPath().contains("dragon")) {
            return true;
        }

        String className = mob.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains("iceandfire") && className.contains("dragon");
    }

    public static void onAttackEntity(Player player, LivingEntity target) {
        boolean isFairy = SimpleracesModVariables.getPlayerVariables(player).fairy;
        if (!isFairy) {
            return;
        }

        CompoundTag data = target.getPersistentData();
        data.putUUID(FAIRY_ATTACKER_TAG, player.getUUID());
        data.putLong(FAIRY_ATTACKER_EXPIRES_TAG, target.level().getGameTime() + FAIRY_ATTACKER_MEMORY_TICKS);

        if (target instanceof Mob mob) {
            mob.setTarget(player);
        }
    }

    private static boolean wasRecentlyAttackedByFairy(LivingEntity mob, Player player) {
        CompoundTag data = mob.getPersistentData();
		return data.hasUUID(FAIRY_ATTACKER_TAG)
				&& data.getUUID(FAIRY_ATTACKER_TAG).equals(player.getUUID())
				&& data.getLong(FAIRY_ATTACKER_EXPIRES_TAG) >= mob.level().getGameTime();
	}

	public static void onServerTick(ServerTickEvent.Post event) {
		MinecraftServer server = event.getServer();
		final int radiusXZ = 12;
		final int yDown = 4;
		final int yUp = 4;
        final int period = 24;
        final boolean useCircle = true;
        final int extraRandomTicks = 1;

        for (ServerLevel level : server.getAllLevels()) {
            RandomSource rand = level.random;

            for (Player player : level.players()) {
                boolean isFairy = SimpleracesModVariables.getPlayerVariables(player).fairy;
                if (!isFairy || (level.getGameTime() + player.getId()) % period != 0) {
                    continue;
                }

                BlockPos center = player.blockPosition().below(2);
                int cx = center.getX();
                int cy = center.getY();
                int cz = center.getZ();
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                for (int dx = -radiusXZ; dx <= radiusXZ; dx++) {
                    for (int dz = -radiusXZ; dz <= radiusXZ; dz++) {
                        if (useCircle && (dx * dx + dz * dz) > radiusXZ * radiusXZ) {
                            continue;
                        }

                        int x = cx + dx;
                        int z = cz + dz;
                        if (!level.hasChunkAt(new BlockPos(x, cy, z))) {
                            continue;
                        }

                        for (int dy = -yDown; dy <= yUp; dy++) {
                            pos.set(x, cy + dy, z);
                            BlockState state = level.getBlockState(pos);

                            if (!isFairyFoodPlant(state)) {
                                continue;
                            }

                            if (state.isRandomlyTicking()) {
                                for (int t = 0; t < extraRandomTicks; t++) {
                                    state.randomTick(level, pos, rand);
                                }
                                continue;
                            }

                            if (state.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock grow) {
                                for (int t = 0; t < extraRandomTicks; t++) {
                                    if (grow.isValidBonemealTarget(level, pos, state)
                                            && grow.isBonemealSuccess(level, rand, pos, state)) {
                                        grow.performBonemeal(level, rand, pos, state);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

	private static boolean isFairyFoodPlant(BlockState state) {
		Block block = state.getBlock();
		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
		if (blockId != null && "farmersdelight".equals(blockId.getNamespace())
				&& (block instanceof CropBlock || block instanceof BonemealableBlock || state.isRandomlyTicking())) {
			return true;
        }

        return state.is(Blocks.WHEAT)
                || state.is(Blocks.CARROTS)
                || state.is(Blocks.POTATOES)
                || state.is(Blocks.BEETROOTS)
                || state.is(Blocks.PUMPKIN_STEM)
                || state.is(Blocks.ATTACHED_PUMPKIN_STEM)
                || state.is(Blocks.MELON_STEM)
                || state.is(Blocks.ATTACHED_MELON_STEM)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.COCOA)
                || state.is(Blocks.NETHER_WART)
                || state.is(Blocks.SUGAR_CANE)
                || state.is(Blocks.CAVE_VINES)
                || state.is(Blocks.CAVE_VINES_PLANT);
    }
}





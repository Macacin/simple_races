package net.simpleraces.procedures.race;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.init.SimpleracesModItems;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.SyncHeatPacket;

public final class DragonRaceMechanics {
    private static final int POST_OVERHEAT_EXTINGUISH_TICKS = 5;
    private static final String WATER_DAMAGE_UNTIL_TAG = "simpleraces_dragon_water_damage_until";
    private static final String IAF_ATTACKER_TAG = "simpleraces_dragon_iaf_attacker";
    private static final String IAF_ATTACKER_UNTIL_TAG = "simpleraces_dragon_iaf_attacker_until";
    private static final int IAF_RETALIATION_MEMORY_TICKS = 20 * 15;

    private DragonRaceMechanics() {
    }

    public static void applyTick(Player player, Level level) {
        if (player.isInWater() && SimpleRPGRacesConfiguration.DRAKONID_WATER_HURT.get()) {
            long gameTime = level.getGameTime();
            long waterDamageUntil = player.getPersistentData().getLong(WATER_DAMAGE_UNTIL_TAG);
            if (gameTime >= waterDamageUntil) {
                player.hurt(player.damageSources().generic(), 1.0F);
                player.getPersistentData().putLong(WATER_DAMAGE_UNTIL_TAG, gameTime + 20L);
            }
        } else {
            player.getPersistentData().remove(WATER_DAMAGE_UNTIL_TAG);
        }

        var data = SimpleracesModVariables.getHeat(player);
        int maxHeat = SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get();
        int maxOverheatTicks = SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_TIME.get() * 20;

        ModMessages.sendToPlayer((ServerPlayer) player,
                new SyncHeatPacket(player.getUUID(), data.getHeat(), maxHeat, data.isOverheated(), data.getOverheatTicks(), maxOverheatTicks));

        if (!data.isOverheated()) {
            return;
        }

        int ticks = data.getOverheatTicks() - 1;
        data.setOverheatTicks(ticks);

        if (ticks % 10 == 0) {
            float stepAmount = maxHeat / 40f;
            data.setHeat((int) (data.getHeat() - stepAmount));
            if (data.getHeat() < 0) {
                data.setHeat(0);
            }
        }

        if (ticks % 10 == 0 || ticks <= 0) {
            ModMessages.sendToPlayer((ServerPlayer) player,
                    new SyncHeatPacket(player.getUUID(), data.getHeat(), maxHeat, data.isOverheated(), data.getOverheatTicks(), maxOverheatTicks));
        }

        if (ticks % 100 == 0) {
            player.hurt(player.damageSources().onFire(), 1.0F);
        }
        if (ticks <= 0) {
            data.setOverheated(false);
            data.setHeat(0);
            data.setPostOverheatExtinguishTicks(POST_OVERHEAT_EXTINGUISH_TICKS);
            player.clearFire();
            ModMessages.sendToPlayer((ServerPlayer) player,
                    new SyncHeatPacket(player.getUUID(), data.getHeat(), maxHeat, data.isOverheated(), data.getOverheatTicks(), maxOverheatTicks));
        }
    }

    public static void executeArmorMelt(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!SimpleRPGRacesConfiguration.DRAKONID_ARMOR_MELT.get()) {
            return;
        }

        meltArmorPiece(world, x, y, z, entity, EquipmentSlot.FEET, 0);
        meltArmorPiece(world, x, y, z, entity, EquipmentSlot.LEGS, 1);
        meltArmorPiece(world, x, y, z, entity, EquipmentSlot.CHEST, 2);
        meltArmorPiece(world, x, y, z, entity, EquipmentSlot.HEAD, 3);

        if (entity instanceof Player player && player.getInventory().contains(new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()))) {
            SimpleracesMod.queueServerWork(3, () -> {
                if (!entity.level().isClientSide() && entity.getServer() != null) {
                    entity.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(
                            CommandSource.NULL,
                            entity.position(),
                            entity.getRotationVector(),
                            entity.level() instanceof ServerLevel serverLevel ? serverLevel : null,
                            4,
                            entity.getName().getString(),
                            entity.getDisplayName(),
                            entity.level().getServer(),
                            entity
                    ), "/clear @s simpleraces:itempopeffect");
                }
            });
        }
    }

    public static void onFall(LivingFallEvent event) {
        if (event.getDistance() <= 4.0f) {
            event.setCanceled(true);
            return;
        }

        event.setDamageMultiplier(SimpleRPGRacesConfiguration.DRAKONID_FALL_MULTIPLY.get().floatValue());
    }

    public static void onJump(Player player) {
        Vec3 velocity = player.getDeltaMovement();
        player.setDeltaMovement(velocity.x, 0.57, velocity.z);
    }

    public static void onAttackEntity(Player player, LivingEntity target) {
        if (!isIceAndFireDragon(target)) {
            return;
        }

        target.getPersistentData().putUUID(IAF_ATTACKER_TAG, player.getUUID());
        target.getPersistentData().putLong(IAF_ATTACKER_UNTIL_TAG, target.level().getGameTime() + IAF_RETALIATION_MEMORY_TICKS);
        if (target instanceof Mob mob) {
            mob.setTarget(player);
        }
    }

    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !isIceAndFireDragon(mob)) {
            return;
        }
        if (!(event.getNewAboutToBeSetTarget() instanceof Player player) || !isDragonoid(player)) {
            return;
        }
        if (wasRecentlyAttackedByDragonoid(mob, player)) {
            return;
        }

        event.setCanceled(true);
        mob.setTarget(null);
    }

    private static void meltArmorPiece(LevelAccessor world, double x, double y, double z, Entity entity, EquipmentSlot slot, int armorIndex) {
        ItemStack stack = entity instanceof LivingEntity living ? living.getItemBySlot(slot) : ItemStack.EMPTY;
        if (!stack.is(ItemTags.create(ResourceLocation.parse("minecraft:melt")))) {
            return;
        }

        if (entity instanceof Player player) {
            player.getInventory().armor.set(armorIndex, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
            player.getInventory().setChanged();
        } else if (entity instanceof LivingEntity living) {
            living.setItemSlot(slot, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
        }

        if (world instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1);
            } else {
                level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1, false);
            }
        }

        SimpleracesMod.queueServerWork(3, () -> {
            if (entity instanceof Player player) {
                player.getInventory().armor.set(armorIndex, new ItemStack(Blocks.AIR));
                player.getInventory().setChanged();
            } else if (entity instanceof LivingEntity living) {
                living.setItemSlot(slot, new ItemStack(Blocks.AIR));
            }
        });
    }

    private static boolean isDragonoid(Player player) {
        return SimpleracesModVariables.getPlayerVariables(player).dragon;
    }

    private static boolean wasRecentlyAttackedByDragonoid(LivingEntity dragon, Player player) {
        return dragon.getPersistentData().hasUUID(IAF_ATTACKER_TAG)
                && dragon.getPersistentData().getUUID(IAF_ATTACKER_TAG).equals(player.getUUID())
                && dragon.getPersistentData().getLong(IAF_ATTACKER_UNTIL_TAG) >= dragon.level().getGameTime();
    }

    private static boolean isIceAndFireDragon(Entity entity) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (entityId != null
                && "iceandfire".equals(entityId.getNamespace())
                && entityId.getPath().contains("dragon")) {
            return true;
        }

        String className = entity.getClass().getName().toLowerCase(java.util.Locale.ROOT);
        return className.contains("iceandfire") && className.contains("dragon");
    }
}






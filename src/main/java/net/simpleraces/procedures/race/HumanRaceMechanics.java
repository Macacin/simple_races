package net.simpleraces.procedures.race;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.simpleraces.network.SimpleracesModVariables;

public final class HumanRaceMechanics {
    private static final String LAST_EXHAUSTION_TAG = "simpleraces_human_last_exhaustion";
    private static final float MAX_MINING_BONUS = 0.03f;
    private static final float MAX_MELEE_BONUS = 0.04f;
    private static final float MAX_RANGED_VELOCITY_BONUS = 0.05f;
    private static final float MINING_PROGRESS_PER_ACTION = 0.0000025f;
    private static final float MELEE_PROGRESS_PER_ACTION = 0.00001f;
    private static final float RANGED_PROGRESS_PER_ACTION = 0.000015f;
    private static final int SYNC_INTERVAL = 20;

    private HumanRaceMechanics() {
    }

    public static void applyTick(Player player) {
        FoodData foodData = player.getFoodData();
        float currentExhaustion = foodData.getExhaustionLevel();
        float previousExhaustion = player.getPersistentData().getFloat(LAST_EXHAUSTION_TAG);

        if (player.isSprinting() && currentExhaustion > previousExhaustion) {
            foodData.setExhaustion(previousExhaustion);
            currentExhaustion = previousExhaustion;
        }

        player.getPersistentData().putFloat(LAST_EXHAUSTION_TAG, currentExhaustion);
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!isHuman(player)) {
            return;
        }
        float bonus = getMiningBonus(player);
        if (bonus <= 0.0f) {
            return;
        }
        event.setNewSpeed(event.getNewSpeed() * (1.0f + bonus));
    }

    public static void onJump(Player player) {
        if (!isHuman(player) || !player.isSprinting()) {
            return;
        }

        FoodData foodData = player.getFoodData();
        float previousExhaustion = player.getPersistentData().getFloat(LAST_EXHAUSTION_TAG);
        foodData.setExhaustion(previousExhaustion);
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide()) {
            return;
        }
        if (player.isCreative() || !isHuman(player)) {
            return;
        }

        BlockState state = event.getState();
        if (state.getDestroySpeed(player.level(), event.getPos()) <= 0.0f) {
            return;
        }

        SimpleracesModVariables.updatePlayerVariables(player, vars -> {
            vars.humanMiningActions++;
            syncIfNeeded(player, vars.humanMiningActions, vars);
        });
    }

    public static float getMeleeDamageBonus(Player player) {
        if (!isHuman(player)) {
            return 0.0f;
        }
        String category = getWeaponCategory(player.getMainHandItem());
        if (category == null) {
            return 0.0f;
        }
        return Math.min(MAX_MELEE_BONUS, getWeaponActions(getHumanVariables(player), category) * MELEE_PROGRESS_PER_ACTION);
    }

    public static void onSuccessfulMeleeHit(Player player) {
        if (!isHuman(player)) {
            return;
        }
        String category = getWeaponCategory(player.getMainHandItem());
        if (category == null) {
            return;
        }

        SimpleracesModVariables.updatePlayerVariables(player, vars -> {
            addWeaponAction(vars, category);
            syncIfNeeded(player, getWeaponActions(vars, category), vars);
        });
    }

    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow)) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if (!isHuman(player)) {
            return;
        }

        SimpleracesModVariables.PlayerVariables vars = getHumanVariables(player);
        vars.humanRangedActions++;
        syncIfNeeded(player, vars.humanRangedActions, vars);

        float bonus = Math.min(MAX_RANGED_VELOCITY_BONUS, vars.humanRangedActions * RANGED_PROGRESS_PER_ACTION);
        if (bonus <= 0.0f) {
            return;
        }

        Vec3 velocity = arrow.getDeltaMovement();
        if (velocity.lengthSqr() > 0.0) {
            arrow.setDeltaMovement(velocity.scale(1.0 + bonus));
        }
    }

    private static float getMiningBonus(Player player) {
        return Math.min(MAX_MINING_BONUS, getHumanVariables(player).humanMiningActions * MINING_PROGRESS_PER_ACTION);
    }

    private static boolean isHuman(Player player) {
        return SimpleracesModVariables.getPlayerVariables(player).human;
    }

    private static SimpleracesModVariables.PlayerVariables getHumanVariables(Player player) {
        return SimpleracesModVariables.getPlayerVariables(player);
    }

    private static int getWeaponActions(SimpleracesModVariables.PlayerVariables vars, String category) {
        return switch (category) {
            case "sword" -> vars.humanSwordActions;
            case "axe" -> vars.humanAxeActions;
            case "trident" -> vars.humanTridentActions;
            case "pickaxe" -> vars.humanPickaxeActions;
            case "shovel" -> vars.humanShovelActions;
            case "hoe" -> vars.humanHoeActions;
            case "dagger" -> vars.humanDaggerActions;
            case "polearm" -> vars.humanPolearmActions;
            case "blunt" -> vars.humanBluntActions;
            default -> vars.humanGenericMeleeActions;
        };
    }

    private static void addWeaponAction(SimpleracesModVariables.PlayerVariables vars, String category) {
        switch (category) {
            case "sword" -> vars.humanSwordActions++;
            case "axe" -> vars.humanAxeActions++;
            case "trident" -> vars.humanTridentActions++;
            case "pickaxe" -> vars.humanPickaxeActions++;
            case "shovel" -> vars.humanShovelActions++;
            case "hoe" -> vars.humanHoeActions++;
            case "dagger" -> vars.humanDaggerActions++;
            case "polearm" -> vars.humanPolearmActions++;
            case "blunt" -> vars.humanBluntActions++;
            default -> vars.humanGenericMeleeActions++;
        }
    }

    private static void syncIfNeeded(Player player, int value, SimpleracesModVariables.PlayerVariables vars) {
        if (!player.level().isClientSide() && value % SYNC_INTERVAL == 0) {
            vars.syncPlayerVariables(player);
        }
    }

    private static String getWeaponCategory(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();
        if (item instanceof SwordItem) {
            return "sword";
        }
        if (item instanceof AxeItem) {
            return "axe";
        }
        if (item instanceof TridentItem) {
            return "trident";
        }
        if (item instanceof PickaxeItem) {
            return "pickaxe";
        }
        if (item instanceof ShovelItem) {
            return "shovel";
        }
        if (item instanceof HoeItem) {
            return "hoe";
        }

        String className = item.getClass().getSimpleName().toLowerCase();
        if (className.contains("dagger")) {
            return "dagger";
        }
        if (className.contains("spear") || className.contains("lance") || className.contains("halberd")) {
            return "polearm";
        }
        if (className.contains("mace") || className.contains("hammer") || className.contains("club")) {
            return "blunt";
        }

        return stack.getUseAnimation() == net.minecraft.world.item.UseAnim.NONE ? "melee" : null;
    }
}






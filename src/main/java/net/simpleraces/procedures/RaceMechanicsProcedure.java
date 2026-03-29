package net.simpleraces.procedures;

import com.jabroni.weightmod.event.ArmorWeightCalculationEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.procedures.race.ArachaRaceMechanics;
import net.simpleraces.procedures.race.DwarfRaceMechanics;
import net.simpleraces.procedures.race.FairyRaceMechanics;
import net.simpleraces.procedures.race.RaceDelegateDispatcher;

@Mod.EventBusSubscriber
public class RaceMechanicsProcedure {
    private RaceMechanicsProcedure() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        Player player = event.player;
        Level level = player.level();
        execute(level, player.getX(), player.getY(), player.getZ(), player);

        SimpleracesModVariables.PlayerVariables vars = player.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());
        RaceDelegateDispatcher.handlePlayerTick(player, level, vars);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        SimpleracesModVariables.PlayerVariables vars = entity.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());
        RaceDelegateDispatcher.handleLivingFall(event, vars);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ForgeMod.ENTITY_REACH.get());
    }

    @SubscribeEvent
    public static void onTargetSet(LivingChangeTargetEvent event) {
        FairyRaceMechanics.onTargetSet(event);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        FairyRaceMechanics.onServerTick(event);
    }

    @SubscribeEvent
    public static void onPotionUsed(LivingEntityUseItemEvent.Finish event) {
        RaceLegacyStatusMechanics.onPotionUsed(event);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        RaceLegacyStatusMechanics.onLivingUpdate(event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        RaceCombatMechanics.handleLivingHurt(event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        RaceCombatMechanics.handleAttackEntity(event);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        RaceCombatMechanics.handleLivingAttack(event);
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        RaceCombatMechanics.handleHeal(event);
    }

    @SubscribeEvent
    public static void onSerpentinEffectRemoved(MobEffectEvent.Remove event) {
        RaceLegacyStatusMechanics.onSerpentinEffectRemoved(event);
    }

    @SubscribeEvent
    public static void onSerpentinEffectExpired(MobEffectEvent.Expired event) {
        RaceLegacyStatusMechanics.onSerpentinEffectExpired(event);
    }

    @SubscribeEvent
    public static void onLivingHurtForBleed(LivingHurtEvent event) {
        RaceCombatMechanics.handleLivingHurtForBleed(event);
    }

    @SubscribeEvent
    public static void onBleedRemoved(MobEffectEvent.Remove event) {
        RaceCombatMechanics.handleBleedRemoved(event);
    }

    @SubscribeEvent
    public static void onBleedExpired(MobEffectEvent.Expired event) {
        RaceCombatMechanics.handleBleedExpired(event);
    }

    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        RaceCombatMechanics.handleMobKilled(event);
    }

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        RaceCombatMechanics.handleAttack(event);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        RaceLegacyStatusMechanics.onClone(event);
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        SimpleracesModVariables.PlayerVariables vars = player.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());
        RaceDelegateDispatcher.handleJump(player, vars);
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return;
        }

        SimpleracesModVariables.PlayerVariables vars = entity.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());
        RaceDelegateDispatcher.handleEntity(world, x, y, z, entity, vars);
    }

    @SubscribeEvent
    public static void onArmorWeightCalculation(ArmorWeightCalculationEvent event) {
        ArachaRaceMechanics.onArmorWeightCalculation(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDwarfShoot(ArrowLooseEvent event) {
        DwarfRaceMechanics.onShoot(event);
    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        DwarfRaceMechanics.onArrowSpawn(event);
    }

    @SubscribeEvent
    public static void onDwarfCrossbowShoot(EntityJoinLevelEvent event) {
        DwarfRaceMechanics.onCrossbowShoot(event);
    }

    @SubscribeEvent
    public static void onDwarfAiming(TickEvent.PlayerTickEvent event) {
        DwarfRaceMechanics.onAiming(event);
    }
}

package net.simpleraces.procedures;

import com.jabroni.weightmod.event.ArmorWeightCalculationEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.procedures.race.ArachaRaceMechanics;
import net.simpleraces.procedures.race.DragonRaceMechanics;
import net.simpleraces.procedures.race.DwarfRaceMechanics;
import net.simpleraces.procedures.race.FairyRaceMechanics;
import net.simpleraces.procedures.race.HumanRaceMechanics;
import net.simpleraces.procedures.race.RaceDelegateDispatcher;

@EventBusSubscriber
public class RaceMechanicsProcedure {
	private RaceMechanicsProcedure() {
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (player.level().isClientSide()) {
			return;
		}
		Level level = player.level();
		execute(level, player.getX(), player.getY(), player.getZ(), player);

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);
        RaceDelegateDispatcher.handlePlayerTick(player, level, vars);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(entity);
        RaceDelegateDispatcher.handleLivingFall(event, vars);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, NeoForgeMod.SWIM_SPEED);
    }

    @SubscribeEvent
    public static void onTargetSet(LivingChangeTargetEvent event) {
        FairyRaceMechanics.onTargetSet(event);
        DragonRaceMechanics.onTargetSet(event);
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		FairyRaceMechanics.onServerTick(event);
	}

    @SubscribeEvent
    public static void onPotionUsed(LivingEntityUseItemEvent.Finish event) {
        RaceLegacyStatusMechanics.onPotionUsed(event);
    }

    @SubscribeEvent
    public static void onLivingUpdate(EntityTickEvent.Post event) {
        RaceLegacyStatusMechanics.onLivingUpdate(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingUpdateLate(EntityTickEvent.Post event) {
        RaceLegacyStatusMechanics.enforceDragonExtinguish(event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        RaceCombatMechanics.handleLivingHurt(event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        RaceCombatMechanics.handleAttackEntity(event);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
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
    public static void onLivingHurtForBleed(LivingDamageEvent.Pre event) {
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
    public static void onAttack(LivingDamageEvent.Pre event) {
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

        HumanRaceMechanics.onJump(player);

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);
        RaceDelegateDispatcher.handleJump(player, vars);
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return;
        }

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(entity);
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
        HumanRaceMechanics.onArrowSpawn(event);
    }

    @SubscribeEvent
    public static void onDwarfCrossbowShoot(EntityJoinLevelEvent event) {
        DwarfRaceMechanics.onCrossbowShoot(event);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        HumanRaceMechanics.onBreakSpeed(event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        HumanRaceMechanics.onBlockBreak(event);
	}

	@SubscribeEvent
	public static void onDwarfAiming(PlayerTickEvent.Post event) {
		DwarfRaceMechanics.onAiming(event);
	}
}






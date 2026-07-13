package net.simpleraces.procedures;

import com.jabroni.weightmod.event.ArmorWeightCalculationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

final class RacePlayerTickMechanics {
    private RacePlayerTickMechanics() {}

    static void handlePlayerTick(PlayerTickEvent.Post event) { RaceMechanicsProcedure.onPlayerTick(event); }
    static void handleLivingFall(LivingFallEvent event) { RaceMechanicsProcedure.onLivingFall(event); }
    static void handleEntityAttributeModification(EntityAttributeModificationEvent event) { RaceMechanicsProcedure.onEntityAttributeModification(event); }
    static void handleTargetSet(LivingChangeTargetEvent event) { RaceMechanicsProcedure.onTargetSet(event); }
    static void handleServerTick(ServerTickEvent.Post event) { RaceMechanicsProcedure.onServerTick(event); }
    static void handleClone(PlayerEvent.Clone event) { RaceLegacyStatusMechanics.onClone(event); }
    static void handleJump(LivingEvent.LivingJumpEvent event) { RaceMechanicsProcedure.onJump(event); }
    static void handleArmorWeightCalculation(ArmorWeightCalculationEvent event) { RaceMechanicsProcedure.onArmorWeightCalculation(event); }
}






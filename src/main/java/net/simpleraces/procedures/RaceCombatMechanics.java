package net.simpleraces.procedures;

import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

final class RaceCombatMechanics {
    private RaceCombatMechanics() {}

    static void handleLivingHurt(LivingDamageEvent.Pre event) { RaceLegacyCombatEvents.onLivingHurt(event); }
    static void handleAttackEntity(AttackEntityEvent event) { RaceLegacyCombatEvents.onAttackEntity(event); }
    static void handleLivingAttack(LivingIncomingDamageEvent event) { RaceLegacyCombatEvents.onLivingAttack(event); }
    static void handleHeal(LivingHealEvent event) { RaceLegacyCombatEvents.onHeal(event); }
    static void handleLivingHurtForBleed(LivingDamageEvent.Pre event) { RaceLegacyCombatEvents.onLivingHurtForBleed(event); }
    static void handleBleedRemoved(MobEffectEvent.Remove event) { RaceLegacyCombatEvents.onBleedRemoved(event); }
    static void handleBleedExpired(MobEffectEvent.Expired event) { RaceLegacyCombatEvents.onBleedExpired(event); }
    static void handleMobKilled(LivingDeathEvent event) { RaceLegacyCombatEvents.onMobKilled(event); }
    static void handleAttack(LivingDamageEvent.Pre event) { RaceLegacyCombatEvents.onAttack(event); }
}






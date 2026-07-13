package net.simpleraces.procedures.race;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.SyncAmbientEffectsPacket;

import java.util.List;
import java.util.function.Predicate;

public final class RaceDelegateDispatcher {
    private static final List<TickHandler> TICK_HANDLERS = List.of(
            new TickHandler(vars -> vars.merfolk, (player, level, vars) -> MerfolkRaceMechanics.apply(player)),
            new TickHandler(vars -> vars.dragon, (player, level, vars) -> DragonRaceMechanics.applyTick(player, level)),
            new TickHandler(vars -> vars.fairy, (player, level, vars) -> FairyRaceMechanics.applyTick(player)),
            new TickHandler(vars -> vars.aracha, (player, level, vars) -> ArachaRaceMechanics.applyTick(player)),
            new TickHandler(vars -> vars.werewolf, (player, level, vars) -> WerewolfRaceMechanics.applyTick(player)),
            new TickHandler(vars -> vars.elf, ElfRaceMechanics::applyTick),
            new TickHandler(vars -> vars.orc, (player, level, vars) -> OrcRaceMechanics.applyTick(player, vars)),
            new TickHandler(vars -> vars.serpentin, (player, level, vars) -> SerpentinRaceMechanics.applyTick(player)),
            new TickHandler(vars -> vars.halfdead, (player, level, vars) -> HalfdeadRaceMechanics.applyTick(player, level)),
            new TickHandler(vars -> vars.gargoyle, (player, level, vars) -> GargoyleRaceMechanics.applyTick(player)),
            new TickHandler(vars -> vars.human, (player, level, vars) -> HumanRaceMechanics.applyTick(player))
    );

    private RaceDelegateDispatcher() {
    }

    public static void handlePlayerTick(Player player, Level level, SimpleracesModVariables.PlayerVariables vars) {
        RaceCarryCapacityHandler.apply(player, vars);

        for (TickHandler handler : TICK_HANDLERS) {
            if (handler.matches(vars)) {
                handler.apply(player, level, vars);
                break;
            }
        }

        syncAmbientEffects(player, vars);
    }

    public static void handleLivingFall(LivingFallEvent event, SimpleracesModVariables.PlayerVariables vars) {
        if (vars.fairy) {
            FairyRaceMechanics.onFall(event);
        } else if (vars.dragon) {
            DragonRaceMechanics.onFall(event);
        } else if (vars.gargoyle) {
            GargoyleRaceMechanics.onFall(event);
        }
    }

    public static void handleJump(Player player, SimpleracesModVariables.PlayerVariables vars) {
        if (vars.dragon) {
            DragonRaceMechanics.onJump(player);
        } else if (vars.gargoyle) {
            GargoyleRaceMechanics.onJump(player);
        }
    }

    public static void handleEntity(LevelAccessor world, double x, double y, double z, Entity entity, SimpleracesModVariables.PlayerVariables vars) {
        if (vars.dwarf) {
            DwarfRaceMechanics.execute(world, x, y, z, entity);
        } else if (vars.dragon) {
            DragonRaceMechanics.executeArmorMelt(world, x, y, z, entity);
        }

        if (vars.elf) {
            ElfRaceMechanics.execute(entity);
        }
    }

    private record TickHandler(Predicate<SimpleracesModVariables.PlayerVariables> matcher, TickAction action) {
        private boolean matches(SimpleracesModVariables.PlayerVariables vars) {
            return matcher.test(vars);
        }

        private void apply(Player player, Level level, SimpleracesModVariables.PlayerVariables vars) {
            action.apply(player, level, vars);
        }
    }

    @FunctionalInterface
    private interface TickAction {
        void apply(Player player, Level level, SimpleracesModVariables.PlayerVariables vars);
    }

    private static void syncAmbientEffects(Player player, SimpleracesModVariables.PlayerVariables vars) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        boolean elfNatureActive = vars.elf && player.getPersistentData().getInt("simpleraces.elfForestGraceTicks") > 0;
        boolean orcSleepActive = vars.orc && player.isSleeping();
        ModMessages.sendToPlayer(serverPlayer,
                new SyncAmbientEffectsPacket(player.getUUID(), elfNatureActive, orcSleepActive));
    }
}






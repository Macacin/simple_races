package net.simpleraces.procedures.race;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public final class RaceCarryCapacityHandler {
    private RaceCarryCapacityHandler() {
    }

    public static void apply(Player player, SimpleracesModVariables.PlayerVariables vars) {
        applyCarryCapacity(player, vars);

        if (vars.dwarf) {
            applyDwarfAimPenalty(player);
        }
    }

    private static void applyCarryCapacity(Player player, SimpleracesModVariables.PlayerVariables vars) {
        int capacity = 10;

        if (vars.elf) {
            capacity = SimpleRPGRacesConfiguration.ELF_CARRY_CAPACITY.get();
        } else if (vars.orc) {
            capacity = SimpleRPGRacesConfiguration.ORC_CARRY_CAPACITY.get();
        } else if (vars.dwarf) {
            capacity = SimpleRPGRacesConfiguration.DWARF_CARRY_CAPACITY.get();
        } else if (vars.merfolk) {
            capacity = SimpleRPGRacesConfiguration.MERFOLK_CARRY_CAPACITY.get();
        } else if (vars.dragon) {
            capacity = SimpleRPGRacesConfiguration.DRAKONID_CARRY_CAPACITY.get();
        } else if (vars.fairy) {
            capacity = SimpleRPGRacesConfiguration.FAIRY_CARRY_CAPACITY.get();
        } else if (vars.werewolf) {
            capacity = SimpleRPGRacesConfiguration.WEREWOLF_CARRY_CAPACITY.get();
        } else if (vars.serpentin) {
            capacity = SimpleRPGRacesConfiguration.SERPENTIN_CARRY_CAPACITY.get();
        } else if (vars.aracha) {
            capacity = SimpleRPGRacesConfiguration.ARACHA_CARRY_CAPACITY.get();
        } else if (vars.halfdead) {
            capacity = SimpleRPGRacesConfiguration.HALFDEAD_CARRY_CAPACITY.get();
        }

        trySetCarryCapacity(player, capacity);
    }

    private static void applyDwarfAimPenalty(Player player) {
        if (!player.isUsingItem()) {
            return;
        }

        ItemStack using = player.getUseItem();
        if (!(using.getItem() instanceof BowItem || using.getItem() instanceof CrossbowItem)) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, false, false));
    }

    private static void trySetCarryCapacity(Player player, int capacity) {
        if (trySetNeoForgeCarryCapacity(player, capacity)) {
            return;
        }

        trySetForgeCarryCapacity(player, capacity);
    }

    private static boolean trySetNeoForgeCarryCapacity(Player player, int capacity) {
        try {
            Class<?> capabilityClass = Class.forName("net.neoforged.neoforge.capabilities.EntityCapability");
            Class<?> weightCapsClass = Class.forName("com.jabroni.weightmod.capability.WeightCapabilities");
            Object capability = weightCapsClass.getField("CAPABILITY").get(null);
            Method getCapability = player.getClass().getMethod("getCapability", capabilityClass);
            Object store = getCapability.invoke(player, capability);
            if (store != null) {
                setCapacity(store, capacity);
                return true;
            }
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException
                 | IllegalAccessException | InvocationTargetException ignored) {
        }

        return false;
    }

    private static void trySetForgeCarryCapacity(Player player, int capacity) {
        try {
            Class<?> capabilityClass = Class.forName("net.minecraftforge.common.capabilities.Capability");
            Class<?> weightCapsClass = Class.forName("com.jabroni.weightmod.capability.WeightCapabilities");
            Object capability = weightCapsClass.getField("CAPABILITY").get(null);
            Object lazyOptional = invokeGetCapability(player, capabilityClass, capability);
            if (lazyOptional == null) {
                return;
            }
            Object store = resolveLazyOptionalValue(lazyOptional);
            if (store != null) {
                setCapacity(store, capacity);
            }
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException
                 | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    private static Object invokeGetCapability(Player player, Class<?> capabilityClass, Object capability)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Method oneArg = player.getClass().getMethod("getCapability", capabilityClass);
            return oneArg.invoke(player, capability);
        } catch (NoSuchMethodException ignored) {
            Class<?> directionClass = net.minecraft.core.Direction.class;
            Method twoArg = player.getClass().getMethod("getCapability", capabilityClass, directionClass);
            return twoArg.invoke(player, capability, null);
        }
    }

    private static void setCapacity(Object store, int capacity) {
        try {
            Method setCapacity = store.getClass().getMethod("setCapacity", int.class);
            setCapacity.invoke(store, capacity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    private static Object resolveLazyOptionalValue(Object lazyOptional) {
        try {
            Method resolve = lazyOptional.getClass().getMethod("resolve");
            Object resolved = resolve.invoke(lazyOptional);
            if (resolved instanceof Optional<?> optional) {
                return optional.orElse(null);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        try {
            Method orElse = lazyOptional.getClass().getMethod("orElse", Object.class);
            return orElse.invoke(lazyOptional, new Object[] { null });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return null;
    }
}





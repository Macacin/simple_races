package net.simpleraces.client;

public class SyncVars {
    public static int heat = 0;
    public static int maxHeat = 100;
    public static boolean overheated = false;

    public static int overheatTicks = 0;
    public static int maxOverheatTicks = 1;

    public static boolean werewolf;

    public static int fairyFlightBar = 0;
    public static int maxFairyFlight = 0;
    public static boolean isFairyRecovering = false;
    public static int gargoyleStance = 1;
    public static int gargoylePetrification = 0;
    public static int gargoyleStanceTime = 0;
    public static int gargoyleMaxStanceTime = 1;
    public static boolean elfNatureEffectActive = false;
    public static boolean orcSleepEffectActive = false;

    public static void syncHeat(int heatIn, int maxHeatIn, boolean overheatedIn, int overheatTicksIn, int maxOverheatTicksIn) {
        heat = heatIn;
        maxHeat = Math.max(1, maxHeatIn);
        overheated = overheatedIn;

        overheatTicks = Math.max(0, overheatTicksIn);
        maxOverheatTicks = Math.max(1, maxOverheatTicksIn);
    }

    public static void reset() {
        heat = 0;
        overheated = false;
        overheatTicks = 0;
        maxOverheatTicks = 1;
    }

    public static void syncWerewolf(boolean isWerewolf) {
        werewolf = isWerewolf;
    }

    public static void syncGargoyle(int stance, int petrification, int stanceTime, int maxStanceTime) {
        gargoyleStance = stance;
        gargoylePetrification = petrification;
        gargoyleStanceTime = Math.max(0, stanceTime);
        gargoyleMaxStanceTime = Math.max(1, maxStanceTime);
    }

    public static void syncAmbientEffects(boolean elfNatureActive, boolean orcSleepActive) {
        elfNatureEffectActive = elfNatureActive;
        orcSleepEffectActive = orcSleepActive;
    }
}





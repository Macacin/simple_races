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
}

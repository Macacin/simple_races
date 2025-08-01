package net.simpleraces.client;

public class SyncVars {
    public static int heat = 0;
    public static boolean overheated = false;
    public static boolean werewolf;
    public static int maxHeat = 100;
    public static int fairyFlightBar = 0;
    public static int maxFairyFlight = 0;
    public static boolean isFairyRecovering = false;

    public static void reset() {
        heat = 0;
        overheated = false;
    }

    public static void syncHeat(int newHeat, int newMaxHeat, boolean newOverheated) {
        heat = newHeat;
        overheated = newOverheated;
        maxHeat = newMaxHeat;
    }

    public static void syncWerewolf(boolean isWerewolf) {
        werewolf = isWerewolf;
    }
}

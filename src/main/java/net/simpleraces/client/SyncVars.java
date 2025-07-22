package net.simpleraces.client;

public class SyncVars {
    public static int heat = 0;
    public static boolean overheated = false;
    public static boolean werewolf;

    public static void reset() {
        heat = 0;
        overheated = false;
    }

    public static void syncHeat(int newHeat, boolean newOverheated) {
        heat = newHeat;
        overheated = newOverheated;
    }

    public static void syncWerewolf(boolean isWerewolf) {
        werewolf = isWerewolf;
    }
}

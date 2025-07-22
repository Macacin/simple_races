package net.simpleraces.heat;

public class Heat implements IHeat {
    private int heat = 0;
    private boolean overheated = false;
    private int overheatTicks = 0;

    @Override public int getHeat() { return heat; }
    @Override public void setHeat(int value) { heat = value; }

    @Override public boolean isOverheated() { return overheated; }
    @Override public void setOverheated(boolean value) { overheated = value; }

    @Override public int getOverheatTicks() { return overheatTicks; }
    @Override public void setOverheatTicks(int ticks) { overheatTicks = ticks; }
}

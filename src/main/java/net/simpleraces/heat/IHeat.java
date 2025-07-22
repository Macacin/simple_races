package net.simpleraces.heat;

public interface IHeat {
    int getHeat();
    void setHeat(int value);

    boolean isOverheated();
    void setOverheated(boolean value);

    int getOverheatTicks();
    void setOverheatTicks(int ticks);
}
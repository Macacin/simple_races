package net.simpleraces.world;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRaceSelectMenu extends AbstractContainerMenu {
    public final Level world;
    public final Player entity;
    public int x, y, z;
    protected AbstractRaceSelectMenu(@Nullable MenuType<?> p_38851_, int p_38852_, Inventory inv) {
        super(p_38851_, p_38852_);
        this.world = inv.player.level();
        this.entity = inv.player;
    }
}

package net.simpleraces.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.simpleraces.world.inventory.GargoyleSelectMenu;

@OnlyIn(Dist.CLIENT)
public class GargoyleSelectScreen extends RaceSelectScreen<GargoyleSelectMenu> {
    public GargoyleSelectScreen(GargoyleSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, Component.literal("gargoyle"));
    }
}






package net.simpleraces.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.simpleraces.world.inventory.HumanSelectMenu;

@OnlyIn(Dist.CLIENT)
public class HumanSelectScreen extends RaceSelectScreen<HumanSelectMenu> {
    public HumanSelectScreen(HumanSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, Component.literal("human"));
    }
}






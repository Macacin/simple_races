package net.simpleraces.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.simpleraces.world.AbstractRaceSelectMenu;
import net.simpleraces.world.inventory.ArachaSelectMenu;

public class ArachnaSelectScreen extends RaceSelectScreen<ArachaSelectMenu>{
    public ArachnaSelectScreen(ArachaSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, Component.literal("arachna"));
    }
}

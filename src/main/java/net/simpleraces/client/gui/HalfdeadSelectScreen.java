package net.simpleraces.client.gui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.simpleraces.world.inventory.HalfdeadSelectMenu;


@OnlyIn(Dist.CLIENT)
public class HalfdeadSelectScreen extends RaceSelectScreen<HalfdeadSelectMenu> {
    public HalfdeadSelectScreen(HalfdeadSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, Component.literal("halfdead"));
    }
}
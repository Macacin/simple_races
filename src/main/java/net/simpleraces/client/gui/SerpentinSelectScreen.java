package net.simpleraces.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.simpleraces.entity.SerpentinModelEntity;
import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SerpentinSelectButtonMessage;
import net.simpleraces.world.inventory.ElfSelectMenu;
import net.simpleraces.world.inventory.SerpentinSelectMenu;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class SerpentinSelectScreen extends RaceSelectScreen<SerpentinSelectMenu> {
    public SerpentinSelectScreen(SerpentinSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, Component.literal("serpentin"));
    }
}
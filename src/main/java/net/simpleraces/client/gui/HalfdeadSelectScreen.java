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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.entity.HalfdeadModelEntity;
import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.network.ElfSelectButtonMessage;
import net.simpleraces.network.HalfdeadSelectButtonMessage;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.procedures.ElfReturnProcedure;
import net.simpleraces.world.inventory.ElfSelectMenu;
import net.simpleraces.world.inventory.HalfdeadSelectMenu;

import java.util.HashMap;


@OnlyIn(Dist.CLIENT)
public class HalfdeadSelectScreen extends RaceSelectScreen<HalfdeadSelectMenu> {
    public HalfdeadSelectScreen(HalfdeadSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, Component.literal("halfdead"));
    }
}
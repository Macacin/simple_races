package net.simpleraces.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.simpleraces.network.ModMessages;
import net.simpleraces.world.inventory.DwarfSelectMenu;
import net.simpleraces.procedures.DwarfReturnProcedure;
import net.simpleraces.network.DwarfSelectButtonMessage;
import net.simpleraces.SimpleracesMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

@OnlyIn(Dist.CLIENT)
public class DwarfSelectScreen extends RaceSelectScreen<DwarfSelectMenu> {
	public DwarfSelectScreen(DwarfSelectMenu container, Inventory inventory, Component text) {
		super(container, inventory, Component.literal("dwarf"));
	}
}
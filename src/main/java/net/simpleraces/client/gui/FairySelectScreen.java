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
import net.simpleraces.entity.FairyModelEntity;
import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.network.ElfSelectButtonMessage;
import net.simpleraces.network.FairySelectButtonMessage;
import net.simpleraces.network.ModMessages;
import net.simpleraces.procedures.ElfReturnProcedure;
import net.simpleraces.world.inventory.ElfSelectMenu;
import net.simpleraces.world.inventory.FairySelectMenu;

import java.util.HashMap;


@OnlyIn(Dist.CLIENT)
public class FairySelectScreen extends RaceSelectScreen<FairySelectMenu> {
	public FairySelectScreen(FairySelectMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, Component.literal("fairy"));
	}
}
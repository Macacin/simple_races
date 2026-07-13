package net.simpleraces.procedures;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.world.inventory.StartMenu;

import net.simpleraces.compat.neoforge.network.NetworkHooks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import io.netty.buffer.Unpooled;

public class OpenSelectOnKeyPressedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(entity);
		if (vars.selected) {
			if (vars.dwarf) {
				OpenDwarfProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.elf) {
				OpenElfProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.orc) {
				OpenOrcProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.merfolk) {
				OpenMerfolkProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.dragon) {
				OpenDragonProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.fairy) {
				OpenFairyProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.werewolf) {
				OpenWerewolfProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.serpentin) {
				OpenSerpentinProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.aracha) {
				OpenArachaProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.halfdead) {
				OpenHalfdeadProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.gargoyle) {
				OpenGargoyleProcedure.execute(world, x, y, z, entity);
				return;
			}
			if (vars.human) {
				OpenHumanProcedure.execute(world, x, y, z, entity);
				return;
			}
		}
		if (entity instanceof ServerPlayer) {
			OpenDwarfProcedure.execute(world, x, y, z, entity);
		}
	}
}






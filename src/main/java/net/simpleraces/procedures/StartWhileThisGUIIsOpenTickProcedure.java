package net.simpleraces.procedures;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.world.inventory.DwarfSelectMenu;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.network.NetworkHooks;

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

public class StartWhileThisGUIIsOpenTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		SimpleracesModVariables.PlayerVariables vars = entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new SimpleracesModVariables.PlayerVariables());

		if (vars.selected) {
			if (entity instanceof Player _player) _player.closeContainer();
			return;
		}
		if (entity == null)
			return;
		SimpleracesMod.queueServerWork(10, () -> {
			if (entity instanceof Player _player)
				_player.closeContainer();
			if (entity instanceof ServerPlayer _ent) {
				BlockPos _bpos = BlockPos.containing(x, y, z);
				NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("DwarfSelect");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new DwarfSelectMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
					}
				}, _bpos);
			}
		});
	}
}

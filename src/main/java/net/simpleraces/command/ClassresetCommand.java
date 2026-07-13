package net.simpleraces.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.simpleraces.procedures.DeselectProcedure;

import java.util.Collection;

@EventBusSubscriber
public class ClassresetCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("classreset")
				.requires(source -> source.hasPermission(2))
				.executes(context -> resetTargets(context.getSource().getPlayerOrException(), context.getSource(), true))
				.then(Commands.argument("targets", EntityArgument.players())
						.executes(context -> resetTargets(EntityArgument.getPlayers(context, "targets"), context.getSource()))));
	}

	private static int resetTargets(ServerPlayer player, net.minecraft.commands.CommandSourceStack source, boolean self) {
		DeselectProcedure.execute(player, true);
		if (!self) {
			source.sendSuccess(() -> Component.literal("Class reset applied to " + player.getName().getString()), true);
		}
		return 1;
	}

	private static int resetTargets(Collection<ServerPlayer> players, net.minecraft.commands.CommandSourceStack source) {
		int resetCount = 0;
		for (ServerPlayer player : players) {
			DeselectProcedure.execute(player, true);
			resetCount++;
		}

		int finalResetCount = resetCount;
		source.sendSuccess(() -> Component.literal("Class reset applied to " + finalResetCount + " player(s)"), true);
		return resetCount;
	}
}






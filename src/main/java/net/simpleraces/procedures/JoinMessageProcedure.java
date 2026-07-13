package net.simpleraces.procedures;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class JoinMessageProcedure {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		SimpleracesModVariables.PlayerVariables variables = SimpleracesModVariables.getPlayerVariables(entity);
		if (SimpleRPGRacesConfiguration.FORCE_PICK.get() && !variables.selected) {
			SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] JoinMessage opens DwarfSelect immediately for {}", entity.getName().getString());
			OpenDwarfProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
		}
	}
}






package net.simpleraces.procedures;

import net.minecraft.world.entity.player.Player;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class DrakonicAttackProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(Entity entity, Entity sourceentity) {
		execute(null, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof Player player))
			return;
		if ((sourceentity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dragon && SimpleRPGRacesConfiguration.DRAK_FIRE.get()) {
			player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
				if (data.isOverheated()) {
					entity.setSecondsOnFire(SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_FIRE_TIME.get());
				}
			});
		}
	}
	@SubscribeEvent
	public static void onHit(LivingAttackEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) return;
		if(!(player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)) return;
	}

}

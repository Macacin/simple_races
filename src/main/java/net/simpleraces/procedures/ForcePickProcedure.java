package net.simpleraces.procedures;

import net.simpleraces.world.AbstractRaceSelectMenu;
import net.simpleraces.world.inventory.DwarfSelectMenu;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.simpleraces.compat.neoforge.network.NetworkHooks;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
import net.simpleraces.SimpleracesMod;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.Unpooled;

@EventBusSubscriber
public class ForcePickProcedure {
	private static final int FORCED_PICK_RETRY_COOLDOWN_TICKS = 40;
	private static final Map<UUID, Integer> pendingForcedPickTicks = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> forcedPickRetryCooldowns = new ConcurrentHashMap<>();

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Entity player = event.getEntity();
		execute(event, player.level(), player.getX(), player.getY(), player.getZ(), player);
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.level().isClientSide || !(entity instanceof ServerPlayer _ent)) {
			return;
		}

		UUID playerId = entity.getUUID();
		if (!entity.isAlive() || SimpleracesModVariables.getPlayerVariables((entity)).selected || !SimpleRPGRacesConfiguration.FORCE_PICK.get()) {
			pendingForcedPickTicks.remove(playerId);
			forcedPickRetryCooldowns.remove(playerId);
			return;
		}

		if (entity instanceof Player _plr2 && _plr2.containerMenu instanceof AbstractRaceSelectMenu) {
			pendingForcedPickTicks.remove(playerId);
			return;
		}

		lockUnselectedPlayer(_ent);

		int cooldown = forcedPickRetryCooldowns.getOrDefault(playerId, 0);
		if (cooldown > 0) {
			forcedPickRetryCooldowns.put(playerId, cooldown - 1);
			return;
		}

		pendingForcedPickTicks.remove(playerId);
		forcedPickRetryCooldowns.put(playerId, FORCED_PICK_RETRY_COOLDOWN_TICKS);
		SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] ForcePick opening DwarfSelect fallback for {}", _ent.getGameProfile().getName());
		BlockPos _bpos = BlockPos.containing(x, y, z);
		NetworkHooks.openScreen(_ent, new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return Component.translatable("menu.simpleraces.dwarf_select");
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
				return new DwarfSelectMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
			}
		}, _bpos);
	}

	private static void lockUnselectedPlayer(ServerPlayer player) {
		player.setDeltaMovement(0.0, 0.0, 0.0);
		player.fallDistance = 0.0f;
		player.hurtMarked = true;
	}
}






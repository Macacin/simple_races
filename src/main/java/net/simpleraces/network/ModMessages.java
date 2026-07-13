package net.simpleraces.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.simpleraces.compat.neoforge.network.NetworkRegistry;
import net.simpleraces.compat.neoforge.network.simple.SimpleChannel;

public class ModMessages {
	private static int id = 0;
	public static SimpleChannel INSTANCE;

	private static int nextId() {
		return id++;
	}

	public static void register(IEventBus modBus) {
		INSTANCE = NetworkRegistry.newSimpleChannel(
				ResourceLocation.fromNamespaceAndPath("simpleraces", "main"),
				() -> "1.0",
				s -> true,
				s -> true
		);

		INSTANCE.registerMessage(nextId(), FairySelectButtonMessage.class, FairySelectButtonMessage::buffer, FairySelectButtonMessage::new, FairySelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), OrcSelectButtonMessage.class, OrcSelectButtonMessage::buffer, OrcSelectButtonMessage::new, OrcSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), DwarfSelectButtonMessage.class, DwarfSelectButtonMessage::buffer, DwarfSelectButtonMessage::new, DwarfSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), DragonSelectButtonMessage.class, DragonSelectButtonMessage::buffer, DragonSelectButtonMessage::new, DragonSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), ElfSelectButtonMessage.class, ElfSelectButtonMessage::buffer, ElfSelectButtonMessage::new, ElfSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), HalfdeadSelectButtonMessage.class, HalfdeadSelectButtonMessage::buffer, HalfdeadSelectButtonMessage::new, HalfdeadSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), MerfolkSelectButtonMessage.class, MerfolkSelectButtonMessage::buffer, MerfolkSelectButtonMessage::new, MerfolkSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), SerpentinSelectButtonMessage.class, SerpentinSelectButtonMessage::buffer, SerpentinSelectButtonMessage::new, SerpentinSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), WerewolfSelectButtonMessage.class, WerewolfSelectButtonMessage::buffer, WerewolfSelectButtonMessage::new, WerewolfSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), ClassDescButtonMessage.class, ClassDescButtonMessage::buffer, ClassDescButtonMessage::new, ClassDescButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), OpenSelectMessage.class, OpenSelectMessage::buffer, OpenSelectMessage::new, OpenSelectMessage::handler);
		INSTANCE.registerMessage(nextId(), SyncHeatPacket.class, SyncHeatPacket::encode, SyncHeatPacket::decode, SyncHeatPacket::handle);
		INSTANCE.registerMessage(nextId(), SyncWerewolfPacket.class, SyncWerewolfPacket::encode, buf -> new SyncWerewolfPacket(buf.readBoolean()), SyncWerewolfPacket::handle);
		INSTANCE.registerMessage(nextId(), SimpleracesModVariables.PlayerVariablesSyncMessage.class, SimpleracesModVariables.PlayerVariablesSyncMessage::buffer,
				SimpleracesModVariables.PlayerVariablesSyncMessage::new, SimpleracesModVariables.PlayerVariablesSyncMessage::handler);
		INSTANCE.registerMessage(nextId(), ArachaSelectButtonMessage.class, ArachaSelectButtonMessage::buffer, ArachaSelectButtonMessage::new, ArachaSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), SyncFairyFlightPacket.class, SyncFairyFlightPacket::encode, SyncFairyFlightPacket::decode, SyncFairyFlightPacket::handle);
		INSTANCE.registerMessage(nextId(), SyncAmbientEffectsPacket.class, SyncAmbientEffectsPacket::encode, SyncAmbientEffectsPacket::decode, SyncAmbientEffectsPacket::handle);
		INSTANCE.registerMessage(nextId(), SyncGargoyleStancePacket.class, SyncGargoyleStancePacket::encode, SyncGargoyleStancePacket::decode, SyncGargoyleStancePacket::handle);
		INSTANCE.registerMessage(nextId(), StartGargoyleGlidePacket.class, StartGargoyleGlidePacket::encode, StartGargoyleGlidePacket::decode, StartGargoyleGlidePacket::handle);
		INSTANCE.registerMessage(nextId(), GargoyleSelectButtonMessage.class, GargoyleSelectButtonMessage::buffer, GargoyleSelectButtonMessage::new, GargoyleSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), HumanSelectButtonMessage.class, HumanSelectButtonMessage::buffer, HumanSelectButtonMessage::new, HumanSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), RandomRaceButtonMessage.class, RandomRaceButtonMessage::buffer, RandomRaceButtonMessage::new, RandomRaceButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), StopEatingPacket.class, StopEatingPacket::encode, StopEatingPacket::decode, StopEatingPacket::handle);
		INSTANCE.register(modBus);
	}

	public static void sendToPlayer(ServerPlayer player, Object message) {
		INSTANCE.sendToPlayer(player, message);
	}

	public static void sendToDimension(ServerLevel level, Object message) {
		INSTANCE.sendToDimension(level, message);
	}
}





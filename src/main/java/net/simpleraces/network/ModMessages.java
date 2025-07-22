package net.simpleraces.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
	private static int id = 0;
	public static SimpleChannel INSTANCE;

	private static int nextId() {
		return id++;
	}

	public static void register() {
		INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("simpleraces", "main"),
			() -> "1.0",
			s -> true,
			s -> true
		);

		// Централизованная регистрация всех пакетов:
		INSTANCE.registerMessage(nextId(), FairySelectButtonMessage.class, FairySelectButtonMessage::buffer, FairySelectButtonMessage::new, FairySelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), OrcSelectButtonMessage.class, OrcSelectButtonMessage::buffer, OrcSelectButtonMessage::new, OrcSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), DwarfSelectButtonMessage.class, DwarfSelectButtonMessage::buffer, DwarfSelectButtonMessage::new, DwarfSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), DragonSelectButtonMessage.class, DragonSelectButtonMessage::buffer, DragonSelectButtonMessage::new, DragonSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), ElfSelectButtonMessage.class, ElfSelectButtonMessage::buffer, ElfSelectButtonMessage::new, ElfSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), HalfdeadSelectButtonMessage.class, HalfdeadSelectButtonMessage::buffer, HalfdeadSelectButtonMessage::new, HalfdeadSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), MerfolkSelectButtonMessage.class, MerfolkSelectButtonMessage::buffer, MerfolkSelectButtonMessage::new, MerfolkSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), WitchSelectButtonMessage.class, WitchSelectButtonMessage::buffer, WitchSelectButtonMessage::new, WitchSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), WerewolfSelectButtonMessage.class, WerewolfSelectButtonMessage::buffer, WerewolfSelectButtonMessage::new, WerewolfSelectButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), ClassDescButtonMessage.class, ClassDescButtonMessage::buffer, ClassDescButtonMessage::new, ClassDescButtonMessage::handler);
		INSTANCE.registerMessage(nextId(), OpenSelectMessage.class, OpenSelectMessage::buffer, OpenSelectMessage::new, OpenSelectMessage::handler);
		INSTANCE.registerMessage(nextId(), SyncHeatPacket.class, SyncHeatPacket::encode, buf -> new SyncHeatPacket(buf.readUUID(), buf.readInt(), buf.readBoolean()), SyncHeatPacket::handle);
		INSTANCE.registerMessage(nextId(), SyncWerewolfPacket.class, SyncWerewolfPacket::encode, buf -> new SyncWerewolfPacket(buf.readBoolean()), SyncWerewolfPacket::handle);
		INSTANCE.registerMessage(nextId(), SimpleracesModVariables.PlayerVariablesSyncMessage.class, SimpleracesModVariables.PlayerVariablesSyncMessage::buffer,
				SimpleracesModVariables.PlayerVariablesSyncMessage::new, SimpleracesModVariables.PlayerVariablesSyncMessage::handler);
		INSTANCE.registerMessage(nextId(), ArachaSelectButtonMessage.class, ArachaSelectButtonMessage::buffer, ArachaSelectButtonMessage::new, ArachaSelectButtonMessage::handler);
	}
}
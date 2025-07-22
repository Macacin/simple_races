package net.simpleraces;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SyncHeatPacket;
import net.simpleraces.network.SyncWerewolfPacket;
import net.simpleraces.network.WerewolfSelectButtonMessage;
import net.simpleraces.procedures.AttributeDeathFixProcedure;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.simpleraces.init.SimpleracesModTabs;
import net.simpleraces.init.SimpleracesModMenus;
import net.simpleraces.init.SimpleracesModItems;
import net.simpleraces.init.SimpleracesModEntities;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("simpleraces")
public class SimpleracesMod {
	public static final Logger LOGGER = LogManager.getLogger(SimpleracesMod.class);
	public static final String MODID = "simpleraces";

	public static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
	public SimpleracesMod() {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(AttributeDeathFixProcedure.class);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		SimpleracesModItems.REGISTRY.register(bus);
		SimpleracesModEntities.REGISTRY.register(bus);

		SimpleracesModTabs.REGISTRY.register(bus);

		SimpleracesModMenus.REGISTRY.register(bus);

		ModEffects.register(bus);
		ModMessages.register();

		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}

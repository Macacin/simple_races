package net.simpleraces.init;

import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SimpleracesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SimpleracesModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SimpleRPGRacesConfiguration.SPEC, "Simple RPG Races.toml");
		});
	}
}

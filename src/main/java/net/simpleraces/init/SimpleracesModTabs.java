
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.SimpleracesMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class SimpleracesModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimpleracesMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(SimpleracesModItems.DWARF_MODEL_SPAWN_EGG.get());
			tabData.accept(SimpleracesModItems.ELF_MODEL_SPAWN_EGG.get());
			tabData.accept(SimpleracesModItems.ORC_MODEL_SPAWN_EGG.get());
			tabData.accept(SimpleracesModItems.DRAGON_MODEL_SPAWN_EGG.get());
			tabData.accept(SimpleracesModItems.MERFOLK_MODEL_SPAWN_EGG.get());
		}
	}
}






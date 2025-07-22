
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.client.gui.*;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;
import net.simpleraces.world.inventory.FairySelectMenu;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SimpleracesModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(SimpleracesModMenus.DWARF_SELECT.get(), DwarfSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.START.get(), StartScreen::new);
			MenuScreens.register(SimpleracesModMenus.START_2.get(), Start2Screen::new);
			MenuScreens.register(SimpleracesModMenus.ELF_SELECT.get(), ElfSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.MERFOLK_SELECT.get(), MerfolkSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.ORC_SELECT.get(), OrcSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.DRAGON_SELECT.get(), DragonSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.CLASS_DESC.get(), ClassDescScreen::new);
			MenuScreens.register(SimpleracesModMenus.FAIRY_SELECT.get(), FairySelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.WITCH_SELECT.get(), WitchSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.WEREWOLF_SELECT.get(), WerewolfSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.HALFDEAD_SELECT.get(), HalfdeadSelectScreen::new);
			MenuScreens.register(SimpleracesModMenus.ARACHNA_SELECT.get(), ArachnaSelectScreen::new);
		});
	}
}

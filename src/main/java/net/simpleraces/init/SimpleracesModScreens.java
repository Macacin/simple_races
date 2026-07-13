
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.client.gui.*;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SimpleracesModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(SimpleracesModMenus.DWARF_SELECT.get(), DwarfSelectScreen::new);
		event.register(SimpleracesModMenus.START.get(), StartScreen::new);
		event.register(SimpleracesModMenus.ELF_SELECT.get(), ElfSelectScreen::new);
		event.register(SimpleracesModMenus.MERFOLK_SELECT.get(), MerfolkSelectScreen::new);
		event.register(SimpleracesModMenus.ORC_SELECT.get(), OrcSelectScreen::new);
		event.register(SimpleracesModMenus.DRAGON_SELECT.get(), DragonSelectScreen::new);
		event.register(SimpleracesModMenus.CLASS_DESC.get(), ClassDescScreen::new);
		event.register(SimpleracesModMenus.FAIRY_SELECT.get(), FairySelectScreen::new);
		event.register(SimpleracesModMenus.SERPENTIN_SELECT.get(), SerpentinSelectScreen::new);
		event.register(SimpleracesModMenus.WEREWOLF_SELECT.get(), WerewolfSelectScreen::new);
		event.register(SimpleracesModMenus.HALFDEAD_SELECT.get(), HalfdeadSelectScreen::new);
		event.register(SimpleracesModMenus.ARACHNA_SELECT.get(), ArachnaSelectScreen::new);
		event.register(SimpleracesModMenus.GARGOYLE_SELECT.get(), GargoyleSelectScreen::new);
		event.register(SimpleracesModMenus.HUMAN_SELECT.get(), HumanSelectScreen::new);
	}
}






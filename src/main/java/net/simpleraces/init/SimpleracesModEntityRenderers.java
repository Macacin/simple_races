
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.client.renderer.*;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SimpleracesModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SimpleracesModEntities.DWARF_MODEL.get(), DwarfModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.ELF_MODEL.get(), ElfModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.ORC_MODEL.get(), OrcModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.DRAGON_MODEL.get(), DragonModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.MERFOLK_MODEL.get(), MerfolkModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.FAIRY_MODEL.get(), FairyModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.WITCH_MODEL.get(), WitchModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.WEREWOLF_MODEL.get(), WerewolfModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.HALFDEAD_MODEL.get(), HalfdeadModelRenderer::new);
		event.registerEntityRenderer(SimpleracesModEntities.ARACHA_MODEL.get(), ArachaModelRenderer::new);
	}
}

package net.simpleraces.procedures;

import net.minecraft.world.entity.player.Player;
import net.simpleraces.client.SyncVars;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

@EventBusSubscriber(value = {Dist.CLIENT})
public class KleidersTestingProcedure {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onEventTriggered(RenderLivingEvent.Pre event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		RenderLivingEvent.Pre _evt = (RenderLivingEvent.Pre) event;
		Minecraft mc = Minecraft.getInstance();
		EntityRenderDispatcher dis = Minecraft.getInstance().getEntityRenderDispatcher();
		EntityRendererProvider.Context context = new EntityRendererProvider.Context(dis, mc.getItemRenderer(), mc.getBlockRenderer(), dis.getItemInHandRenderer(), mc.getResourceManager(), mc.getEntityModels(), mc.font);
		Entity _evtEntity = _evt.getEntity();
		PlayerRenderer _pr = null;
		PoseStack poseStack = _evt.getPoseStack();
		if (_evt.getRenderer() instanceof PlayerRenderer && !(_evt.getRenderer() instanceof com.kleiders.kleidersplayerrenderer.KleidersIgnoreCancel)) {
			ResourceLocation _texture = ResourceLocation.parse("kleiders_custom_renderer:textures/entities/empty.png");
			com.kleiders.kleidersplayerrenderer.KleidersSkinRenderer emptyRenderer = new com.kleiders.kleidersplayerrenderer.KleidersSkinRenderer(context,
					(_evtEntity instanceof AbstractClientPlayer clientPlayer ? clientPlayer.getSkin().model() == PlayerSkin.Model.SLIM : false), _texture);
			_pr = emptyRenderer;
			emptyRenderer.clearLayers();
			emptyRenderer.render((AbstractClientPlayer) _evt.getEntity(), _evt.getEntity().getYRot(), _evt.getPartialTick(), _evt.getPoseStack(), _evt.getMultiBufferSource(), _evt.getPackedLight());
		}
		if (SimpleracesModVariables.getPlayerVariables(entity).orc && SimpleRPGRacesConfiguration.ORC_TUSKS.get()) {
			if (_evt.getRenderer() instanceof PlayerRenderer && !(_evt.getRenderer() instanceof com.kleiders.kleidersplayerrenderer.KleidersIgnoreCancel)) {
				ResourceLocation _texture = ResourceLocation.parse("kleiders_custom_renderer:textures/entities/default.png");
				if (ResourceLocation.tryParse("simpleraces:textures/entities/tusks.png") != null) {
					_texture = ResourceLocation.parse("simpleraces:textures/entities/tusks.png");
				}
				new com.kleiders.kleidersplayerrenderer.KleidersSkinRenderer(context, false, _texture).render((AbstractClientPlayer) _evt.getEntity(), _evt.getEntity().getYRot(), _evt.getPartialTick(), _evt.getPoseStack(),
						_evt.getMultiBufferSource(), _evt.getPackedLight());
			}
		} else if (SimpleracesModVariables.getPlayerVariables(entity).elf && SimpleRPGRacesConfiguration.ELF_EARS.get()) {
			if (_evt.getRenderer() instanceof PlayerRenderer && !(_evt.getRenderer() instanceof com.kleiders.kleidersplayerrenderer.KleidersIgnoreCancel)) {
				ResourceLocation _texture = ResourceLocation.parse("kleiders_custom_renderer:textures/entities/default.png");
				if (ResourceLocation.tryParse("simpleraces:textures/entities/elf_ears.png") != null) {
					_texture = ResourceLocation.parse("simpleraces:textures/entities/elf_ears.png");
				}
				new com.kleiders.kleidersplayerrenderer.KleidersSkinRenderer(context, false, _texture).render((AbstractClientPlayer) _evt.getEntity(), _evt.getEntity().getYRot(), _evt.getPartialTick(), _evt.getPoseStack(),
						_evt.getMultiBufferSource(), _evt.getPackedLight());
			}
		} else if (SimpleracesModVariables.getPlayerVariables(entity).merfolk && entity.isInWater() && SimpleRPGRacesConfiguration.MERFOLK_FORM.get()) {
			if (_evt.getRenderer() instanceof PlayerRenderer && !(_evt.getRenderer() instanceof com.kleiders.kleidersplayerrenderer.KleidersIgnoreCancel)) {
				ResourceLocation _texture = ResourceLocation.parse("kleiders_custom_renderer:textures/entities/default.png");
				if (ResourceLocation.tryParse("simpleraces:textures/entities/merfolk.png") != null) {
					_texture = ResourceLocation.parse("simpleraces:textures/entities/merfolk.png");
				}
				new com.kleiders.kleidersplayerrenderer.KleidersSkinRenderer(context, false, _texture).render((AbstractClientPlayer) _evt.getEntity(), _evt.getEntity().getYRot(), _evt.getPartialTick(), _evt.getPoseStack(),
						_evt.getMultiBufferSource(), _evt.getPackedLight());
			}
		} else if (SimpleracesModVariables.getPlayerVariables(entity).werewolf
				&& SyncVars.werewolf){
			if (_evt.getRenderer() instanceof PlayerRenderer && !(_evt.getRenderer() instanceof com.kleiders.kleidersplayerrenderer.KleidersIgnoreCancel)) {
				_evt.setCanceled(true);
				ResourceLocation _texture = ResourceLocation.parse("simpleraces:textures/entities/werewolf.png");
				new com.kleiders.kleidersplayerrenderer.KleidersSkinRenderer(context, false, _texture).render((AbstractClientPlayer) _evt.getEntity(), _evt.getEntity().getYRot(), _evt.getPartialTick(), _evt.getPoseStack(),
						_evt.getMultiBufferSource(), _evt.getPackedLight());
			}
		}
	}
}






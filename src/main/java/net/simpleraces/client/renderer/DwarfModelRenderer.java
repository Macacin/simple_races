
package net.simpleraces.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.monster.Monster;
import net.simpleraces.entity.DwarfModelEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

public class DwarfModelRenderer extends HumanoidMobRenderer<Monster, HumanoidModel<Monster>> {
	public DwarfModelRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<Monster>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(Monster entity) {
		return new ResourceLocation("simpleraces:textures/entities/2024_10_07_dwarf-22808312.png");
	}

	@Override
	public void render(Monster p_115455_, float p_115456_, float p_115457_, PoseStack poseStack, MultiBufferSource p_115459_, int p_115460_) {
		poseStack.pushPose();
		poseStack.scale(0.8f, 0.8f, 0.8f);
		super.render(p_115455_, p_115456_, p_115457_, poseStack, p_115459_, p_115460_);
		poseStack.popPose();
	}
}

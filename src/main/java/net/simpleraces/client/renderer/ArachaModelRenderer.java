
package net.simpleraces.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;

public class ArachaModelRenderer extends HumanoidMobRenderer<Monster, HumanoidModel<Monster>> {
	public ArachaModelRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(Monster entity) {
		return new ResourceLocation("simpleraces:textures/entities/arachna.png");
	}

	@Override
	public void render(Monster p_115455_, float p_115456_, float p_115457_, PoseStack poseStack, MultiBufferSource p_115459_, int p_115460_) {
		poseStack.pushPose();
		poseStack.scale(0.9f, 0.9f, 0.9f);
		super.render(p_115455_, p_115456_, p_115457_, poseStack, p_115459_, p_115460_);
		poseStack.popPose();
	}
}

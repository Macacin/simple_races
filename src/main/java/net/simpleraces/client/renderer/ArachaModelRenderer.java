
package net.simpleraces.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.simpleraces.entity.ArachaModelEntity;
import net.simpleraces.entity.MerfolkModelEntity;

public class ArachaModelRenderer extends HumanoidMobRenderer<ArachaModelEntity, HumanoidModel<ArachaModelEntity>> {
	public ArachaModelRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(ArachaModelEntity entity) {
		return new ResourceLocation("simpleraces:textures/entities/aracha.png");
	}
}

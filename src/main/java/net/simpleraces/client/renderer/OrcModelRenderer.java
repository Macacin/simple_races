
package net.simpleraces.client.renderer;

import net.simpleraces.entity.OrcModelEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

public class OrcModelRenderer extends HumanoidMobRenderer<OrcModelEntity, HumanoidModel<OrcModelEntity>> {
	public OrcModelRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<OrcModelEntity>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(OrcModelEntity entity) {
		return new ResourceLocation("simpleraces:textures/entities/2024_10_07_orc-22808309.png");
	}
}


package net.simpleraces.client.renderer;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.monster.Monster;

public class DragonModelRenderer extends HumanoidMobRenderer<Monster, HumanoidModel<Monster>> {
	public DragonModelRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<Monster>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(Monster entity) {
		return new ResourceLocation("simpleraces:textures/entities/2024_09_02_fantasy-mc-dragonborn--red---fixed-for-3d-pixels--22745241.png");
	}
}

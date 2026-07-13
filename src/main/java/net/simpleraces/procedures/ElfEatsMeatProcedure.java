package net.simpleraces.procedures;

import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.simpleraces.network.SimpleracesModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ElfEatsMeatProcedure {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity(), event.getItem());
		}
	}

	public static void execute(Entity entity, ItemStack itemstack) {
		execute(null, entity, itemstack);
	}

	private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (SimpleRPGRacesConfiguration.ELF_MEAT_RESTRICT.get() && itemstack.is(ItemTags.create(ResourceLocation.parse("minecraft:meat")))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide() && SimpleracesModVariables.getPlayerVariables(entity).elf)
				_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, false));
		}
	}
}






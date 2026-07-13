
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.monster.Monster;
import net.simpleraces.entity.*;
import net.simpleraces.SimpleracesMod;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class SimpleracesModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, SimpleracesMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> DWARF_MODEL = register("dwarf_model",
			EntityType.Builder.<Monster>of(DwarfModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.44f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> ELF_MODEL = register("elf_model",
			EntityType.Builder.<Monster>of(ElfModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> ORC_MODEL = register("orc_model",
			EntityType.Builder.<Monster>of(OrcModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> DRAGON_MODEL = register("dragon_model",
			EntityType.Builder.<Monster>of(DragonModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> MERFOLK_MODEL = register("merfolk_model",
			EntityType.Builder.<Monster>of(MerfolkModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> FAIRY_MODEL = register("fairy_model",
			EntityType.Builder.<Monster>of(FairyModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.0f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> SERPENTIN_MODEL = register("serpentin_model",
			EntityType.Builder.<Monster>of(SerpentinModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> WEREWOLF_MODEL = register("werewolf_model",
			EntityType.Builder.<Monster>of(WerewolfModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> HALFDEAD_MODEL = register("halfdead_model",
			EntityType.Builder.<Monster>of(HalfdeadModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> ARACHA_MODEL = register("aracha_model",
			EntityType.Builder.<Monster>of(ArachaModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> GARGOYLE_MODEL = register("gargoyle_model",
			EntityType.Builder.<Monster>of(GargoyleModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<Monster>> HUMAN_MODEL = register("human_model",
			EntityType.Builder.<Monster>of(HumanModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6f, 1.8f));

	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DwarfModelEntity.init();
			ElfModelEntity.init();
			OrcModelEntity.init();
			DragonModelEntity.init();
			MerfolkModelEntity.init();
			FairyModelEntity.init();
			SerpentinModelEntity.init();
			WerewolfModelEntity.init();
			HalfdeadModelEntity.init();
			ArachaModelEntity.init();
			GargoyleModelEntity.init();
			HumanModelEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(DWARF_MODEL.get(), DwarfModelEntity.createAttributes().build());
		event.put(ELF_MODEL.get(), ElfModelEntity.createAttributes().build());
		event.put(ORC_MODEL.get(), OrcModelEntity.createAttributes().build());
		event.put(DRAGON_MODEL.get(), DragonModelEntity.createAttributes().build());
		event.put(MERFOLK_MODEL.get(), MerfolkModelEntity.createAttributes().build());
		event.put(FAIRY_MODEL.get(), FairyModelEntity.createAttributes().build());
		event.put(SERPENTIN_MODEL.get(), SerpentinModelEntity.createAttributes().build());
		event.put(WEREWOLF_MODEL.get(), WerewolfModelEntity.createAttributes().build());
		event.put(HALFDEAD_MODEL.get(), HalfdeadModelEntity.createAttributes().build());
		event.put(ARACHA_MODEL.get(), ArachaModelEntity.createAttributes().build());
		event.put(GARGOYLE_MODEL.get(), GargoyleModelEntity.createAttributes().build());
		event.put(HUMAN_MODEL.get(), HumanModelEntity.createAttributes().build());
	}

	public static EntityType<Monster> getByName(String name) {
		return switch(name) {
			case "arachna" -> ARACHA_MODEL.get();
			case "dwarf" -> DWARF_MODEL.get();
			case "elf" -> ELF_MODEL.get();
			case "orc" -> ORC_MODEL.get();
			case "dragon" -> DRAGON_MODEL.get();
			case "merfolk" -> MERFOLK_MODEL.get();
			case "fairy" -> FAIRY_MODEL.get();
			case "serpentin" -> SERPENTIN_MODEL.get();
			case "werewolf" -> WEREWOLF_MODEL.get();
			case "halfdead" -> HALFDEAD_MODEL.get();
			case "gargoyle" -> GARGOYLE_MODEL.get();
			case "human" -> HUMAN_MODEL.get();
			default -> null;
		};
	}
}






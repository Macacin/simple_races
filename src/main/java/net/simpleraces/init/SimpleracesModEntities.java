
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.minecraft.world.entity.monster.Monster;
import net.simpleraces.entity.*;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SimpleracesModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SimpleracesMod.MODID);
	public static final RegistryObject<EntityType<Monster>> DWARF_MODEL = register("dwarf_model",
			EntityType.Builder.<Monster>of(DwarfModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DwarfModelEntity::new)

					.sized(0.6f, 1.44f));
	public static final RegistryObject<EntityType<Monster>> ELF_MODEL = register("elf_model",
			EntityType.Builder.<Monster>of(ElfModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ElfModelEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<Monster>> ORC_MODEL = register("orc_model",
			EntityType.Builder.<Monster>of(OrcModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(OrcModelEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<Monster>> DRAGON_MODEL = register("dragon_model",
			EntityType.Builder.<Monster>of(DragonModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DragonModelEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<Monster>> MERFOLK_MODEL = register("merfolk_model",
			EntityType.Builder.<Monster>of(MerfolkModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MerfolkModelEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<Monster>> FAIRY_MODEL = register("fairy_model",
			EntityType.Builder.<Monster>of(FairyModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FairyModelEntity::new)

					.sized(0.6f, 1.0f));

	public static final RegistryObject<EntityType<Monster>> SERPENTIN_MODEL = register("serpentin_model",
			EntityType.Builder.<Monster>of(SerpentinModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SerpentinModelEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<Monster>> WEREWOLF_MODEL = register("werewolf_model",
			EntityType.Builder.<Monster>of(WerewolfModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(WerewolfModelEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<Monster>> HALFDEAD_MODEL = register("halfdead_model",
			EntityType.Builder.<Monster>of(HalfdeadModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(HalfdeadModelEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<Monster>> ARACHA_MODEL = register("aracha_model",
			EntityType.Builder.<Monster>of(ArachaModelEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ArachaModelEntity::new)

					.sized(0.6f, 1.8f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<Monster> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
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
			default -> null;
		};
	}
}

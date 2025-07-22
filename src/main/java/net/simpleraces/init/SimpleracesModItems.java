
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.item.ItempopeffectItem;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;

public class SimpleracesModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SimpleracesMod.MODID);
	public static final RegistryObject<Item> DWARF_MODEL_SPAWN_EGG = REGISTRY.register("dwarf_model_spawn_egg", () -> new ForgeSpawnEggItem(SimpleracesModEntities.DWARF_MODEL, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> ELF_MODEL_SPAWN_EGG = REGISTRY.register("elf_model_spawn_egg", () -> new ForgeSpawnEggItem(SimpleracesModEntities.ELF_MODEL, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> ORC_MODEL_SPAWN_EGG = REGISTRY.register("orc_model_spawn_egg", () -> new ForgeSpawnEggItem(SimpleracesModEntities.ORC_MODEL, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> DRAGON_MODEL_SPAWN_EGG = REGISTRY.register("dragon_model_spawn_egg", () -> new ForgeSpawnEggItem(SimpleracesModEntities.DRAGON_MODEL, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> MERFOLK_MODEL_SPAWN_EGG = REGISTRY.register("merfolk_model_spawn_egg", () -> new ForgeSpawnEggItem(SimpleracesModEntities.MERFOLK_MODEL, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> ITEMPOPEFFECT = REGISTRY.register("itempopeffect", () -> new ItempopeffectItem());
	// Start of user code block custom items
	// End of user code block custom items
}

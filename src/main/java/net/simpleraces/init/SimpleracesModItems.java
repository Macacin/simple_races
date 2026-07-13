
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.minecraft.core.registries.Registries;
import net.simpleraces.item.ItempopeffectItem;
import net.simpleraces.SimpleracesMod;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import net.minecraft.world.item.Item;

public class SimpleracesModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(SimpleracesMod.MODID);
	public static final DeferredItem<Item> DWARF_MODEL_SPAWN_EGG = REGISTRY.register("dwarf_model_spawn_egg", () -> new DeferredSpawnEggItem(SimpleracesModEntities.DWARF_MODEL, -1, -1, new Item.Properties()));
	public static final DeferredItem<Item> ELF_MODEL_SPAWN_EGG = REGISTRY.register("elf_model_spawn_egg", () -> new DeferredSpawnEggItem(SimpleracesModEntities.ELF_MODEL, -1, -1, new Item.Properties()));
	public static final DeferredItem<Item> ORC_MODEL_SPAWN_EGG = REGISTRY.register("orc_model_spawn_egg", () -> new DeferredSpawnEggItem(SimpleracesModEntities.ORC_MODEL, -1, -1, new Item.Properties()));
	public static final DeferredItem<Item> DRAGON_MODEL_SPAWN_EGG = REGISTRY.register("dragon_model_spawn_egg", () -> new DeferredSpawnEggItem(SimpleracesModEntities.DRAGON_MODEL, -1, -1, new Item.Properties()));
	public static final DeferredItem<Item> MERFOLK_MODEL_SPAWN_EGG = REGISTRY.register("merfolk_model_spawn_egg", () -> new DeferredSpawnEggItem(SimpleracesModEntities.MERFOLK_MODEL, -1, -1, new Item.Properties()));
	public static final DeferredItem<Item> ITEMPOPEFFECT = REGISTRY.register("itempopeffect", ItempopeffectItem::new);
	// Start of user code block custom items
	// End of user code block custom items
}






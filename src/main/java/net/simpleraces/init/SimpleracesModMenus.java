
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.minecraft.core.registries.Registries;
import net.simpleraces.world.inventory.*;
import net.simpleraces.SimpleracesMod;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.MenuType;

public class SimpleracesModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, SimpleracesMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<DwarfSelectMenu>> DWARF_SELECT = REGISTRY.register("dwarf_select", () -> IMenuTypeExtension.create(DwarfSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<StartMenu>> START = REGISTRY.register("start", () -> IMenuTypeExtension.create(StartMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ElfSelectMenu>> ELF_SELECT = REGISTRY.register("elf_select", () -> IMenuTypeExtension.create(ElfSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<MerfolkSelectMenu>> MERFOLK_SELECT = REGISTRY.register("merfolk_select", () -> IMenuTypeExtension.create(MerfolkSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<OrcSelectMenu>> ORC_SELECT = REGISTRY.register("orc_select", () -> IMenuTypeExtension.create(OrcSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<DragonSelectMenu>> DRAGON_SELECT = REGISTRY.register("dragon_select", () -> IMenuTypeExtension.create(DragonSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FairySelectMenu>> FAIRY_SELECT = REGISTRY.register("fairy_select", () -> IMenuTypeExtension.create(FairySelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SerpentinSelectMenu>> SERPENTIN_SELECT = REGISTRY.register("serpentin_select", () -> IMenuTypeExtension.create(SerpentinSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<WerewolfSelectMenu>> WEREWOLF_SELECT = REGISTRY.register("werewolf_select", () -> IMenuTypeExtension.create(WerewolfSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<HalfdeadSelectMenu>> HALFDEAD_SELECT = REGISTRY.register("halfdead_select", () -> IMenuTypeExtension.create(HalfdeadSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ArachaSelectMenu>> ARACHNA_SELECT = REGISTRY.register("arachna_select", () -> IMenuTypeExtension.create(ArachaSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<GargoyleSelectMenu>> GARGOYLE_SELECT = REGISTRY.register("gargoyle_select", () -> IMenuTypeExtension.create(GargoyleSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<HumanSelectMenu>> HUMAN_SELECT = REGISTRY.register("human_select", () -> IMenuTypeExtension.create(HumanSelectMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ClassDescMenu>> CLASS_DESC = REGISTRY.register("class_desc", () -> IMenuTypeExtension.create(ClassDescMenu::new));
}






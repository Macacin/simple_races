
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.simpleraces.init;

import net.simpleraces.world.inventory.*;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class SimpleracesModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SimpleracesMod.MODID);
	public static final RegistryObject<MenuType<DwarfSelectMenu>> DWARF_SELECT = REGISTRY.register("dwarf_select", () -> IForgeMenuType.create(DwarfSelectMenu::new));
	public static final RegistryObject<MenuType<StartMenu>> START = REGISTRY.register("start", () -> IForgeMenuType.create(StartMenu::new));
	public static final RegistryObject<MenuType<ElfSelectMenu>> ELF_SELECT = REGISTRY.register("elf_select", () -> IForgeMenuType.create(ElfSelectMenu::new));
	public static final RegistryObject<MenuType<MerfolkSelectMenu>> MERFOLK_SELECT = REGISTRY.register("merfolk_select", () -> IForgeMenuType.create(MerfolkSelectMenu::new));
	public static final RegistryObject<MenuType<OrcSelectMenu>> ORC_SELECT = REGISTRY.register("orc_select", () -> IForgeMenuType.create(OrcSelectMenu::new));
	public static final RegistryObject<MenuType<DragonSelectMenu>> DRAGON_SELECT = REGISTRY.register("dragon_select", () -> IForgeMenuType.create(DragonSelectMenu::new));
	public static final RegistryObject<MenuType<FairySelectMenu>> FAIRY_SELECT = REGISTRY.register("fairy_select", () -> IForgeMenuType.create(FairySelectMenu::new));
	public static final RegistryObject<MenuType<SerpentinSelectMenu>> SERPENTIN_SELECT = REGISTRY.register("serpentin_select", () -> IForgeMenuType.create(SerpentinSelectMenu::new));
	public static final RegistryObject<MenuType<WerewolfSelectMenu>> WEREWOLF_SELECT = REGISTRY.register("werewolf_select", () -> IForgeMenuType.create(WerewolfSelectMenu::new));
	public static final RegistryObject<MenuType<HalfdeadSelectMenu>> HALFDEAD_SELECT = REGISTRY.register("halfdead_select", () -> IForgeMenuType.create(HalfdeadSelectMenu::new));
	public static final RegistryObject<MenuType<ArachaSelectMenu>> ARACHNA_SELECT = REGISTRY.register("arachna_select", () -> IForgeMenuType.create(ArachaSelectMenu::new));
	public static final RegistryObject<MenuType<ClassDescMenu>> CLASS_DESC = REGISTRY.register("class_desc", () -> IForgeMenuType.create(ClassDescMenu::new));
}

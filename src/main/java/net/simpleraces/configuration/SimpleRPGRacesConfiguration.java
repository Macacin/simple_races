package net.simpleraces.configuration;

import net.minecraftforge.common.ForgeConfigSpec;


public class SimpleRPGRacesConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> FORCE_PICK;
	public static final ForgeConfigSpec.ConfigValue<Boolean> JOIN_MESSAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DESELECT;
	public static final ForgeConfigSpec.ConfigValue<String> JOIN_MESSAGE_TEXT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_NIGHT_VISION;
	public static final ForgeConfigSpec.ConfigValue<Double> ELF_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_BOW_SIGHT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_PICKAXE_RESTRICT;
	public static final ForgeConfigSpec.ConfigValue<Double> ELF_BOW_BONUS_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_MEAT_RESTRICT;
	public static final ForgeConfigSpec.ConfigValue<Double> ORC_RES_LEVEL;
	public static final ForgeConfigSpec.ConfigValue<Double> ORC_ATTACK_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ORC_RAGE;
	public static final ForgeConfigSpec.ConfigValue<Double> ORC_KNOCKBACK_RES;
	public static final ForgeConfigSpec.ConfigValue<Double> ORC_ARMOR_VALUE_SLOW;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ORC_MINI_RAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DWARF_HASTE;
	public static final ForgeConfigSpec.ConfigValue<Double> DWARF_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Double> DWARF_RES_LEVEL;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DWARF_SUBZERO_EFFECTS;
	public static final ForgeConfigSpec.ConfigValue<Double> DWARF_PICKAXE_STRENGTH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DWARF_BOW_RESTRICT;
	public static final ForgeConfigSpec.ConfigValue<Double> MERFOLK_SWIM_SPEED;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MERFOLK_CONDUIT_EFFECT;
	public static final ForgeConfigSpec.ConfigValue<Double> MERFOLK_ATTACK_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DRAK_FIRE;
	public static final ForgeConfigSpec.ConfigValue<Double> DRAK_ATTACK_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DRAK_FIRE_RES;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DRAKONID_ARMOR_MELT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DRAKONID_WATER_HURT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_EARS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ORC_TUSKS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MERFOLK_FORM;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DWARF_RESIZE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_RESIZE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ORC_RESIZE;

	static {
		BUILDER.push("Class Select");
		FORCE_PICK = BUILDER.comment("Force player to pick a class after joining").define("force_pick", false);
		JOIN_MESSAGE = BUILDER.comment("Message players who haven't selected a class").define("join_message", true);
		DESELECT = BUILDER.comment("Ability to deselect classes (survival) via /classreset").define("deselect", false);
		JOIN_MESSAGE_TEXT = BUILDER.comment("Enter Join Message Text").define("join_message_text", "Click the Assigned Key [M] To Open the RPG Race Selection Menu");
		BUILDER.pop();
		BUILDER.push("Elf Class Attributes");
		ELF_NIGHT_VISION = BUILDER.comment("Whether or not elves have night vision").define("elf_night_vision", false);
		ELF_MAX_HEALTH = BUILDER.comment("Set elf max health").define("elf_max_health", 16.0);
		ELF_BOW_SIGHT = BUILDER.comment("Add glowing effect when drawing bow").define("elf_bow_sight", true);
		ELF_PICKAXE_RESTRICT = BUILDER.comment("Elf gains mining fatigue while holding a pickaxe").define("elf_pickaxe_restrict", true);
		ELF_BOW_BONUS_DAMAGE = BUILDER.comment("Bonus damage elves deal with bows").define("elf_bow_bonus_damage", 2.0);
		ELF_MEAT_RESTRICT = BUILDER.comment("Whether or not elves can eat meat without being poisoned").define("elf_meat_restrict", true);
		BUILDER.pop();
		BUILDER.push("Orc Class Attributes");
		ORC_RES_LEVEL = BUILDER.comment("Level of resistance effect").define("orc_res_level", 0.0);
		ORC_ATTACK_DAMAGE = BUILDER.comment("Orc attack damage bonus").define("orc_attack_damage", 1.5);
		ORC_RAGE = BUILDER.comment("Increased strength on low health").define("orc_rage", true);
		ORC_KNOCKBACK_RES = BUILDER.comment("Knockback resistance bonus").define("orc_knockback_res", 1.1);
		ORC_ARMOR_VALUE_SLOW = BUILDER.comment("Armor value at which the Orc class begins to be slowed").define("orc_armor_value_slow", 0.0);
		ORC_MINI_RAGE = BUILDER.comment("Orc gains minor strength boost at less than 5 health").define("orc_mini_rage", true);
		BUILDER.pop();
		BUILDER.push("Dwarf Class Attributes");
		DWARF_HASTE = BUILDER.comment("Increased mining speed underground").define("dwarf_haste", true);
		DWARF_MAX_HEALTH = BUILDER.define("dwarf_max_health", 10.0);
		DWARF_RES_LEVEL = BUILDER.comment("Level of resistance effect").define("dwarf_res_level", 1.0);
		DWARF_SUBZERO_EFFECTS = BUILDER.comment("Dwarf gains nightvision + haste 2 at subzero levels (underground only)").define("dwarf_subzero_effects", false);
		DWARF_PICKAXE_STRENGTH = BUILDER.comment("Dwarf strength level while holding a pickaxe").define("dwarf_pickaxe_strength", 0.0);
		DWARF_BOW_RESTRICT = BUILDER.comment("Dwarf cant use bows").define("dwarf_bow_restrict", true);
		BUILDER.pop();
		BUILDER.push("Merfolk Class Attributes");
		MERFOLK_SWIM_SPEED = BUILDER.comment("Swim speed").define("merfolk_swim_speed", 2.0);
		MERFOLK_CONDUIT_EFFECT = BUILDER.comment("Whether or not the conduit effect is applied").define("merfolk_conduit_effect", true);
		MERFOLK_ATTACK_DAMAGE = BUILDER.comment("Merfolk attack damage level").define("merfolk_attack_damage", 0.8);
		BUILDER.pop();
		BUILDER.push("Drakonid Class Attributes");
		DRAK_FIRE = BUILDER.comment("Apply fire aspect to attacks").define("drak_fire", true);
		DRAK_ATTACK_DAMAGE = BUILDER.comment("Drakonid attack damage bonus").define("drak_attack_damage", 1.25);
		DRAK_FIRE_RES = BUILDER.comment("Whether or not to resist damage from fire sources").define("drak_fire_res", true);
		DRAKONID_ARMOR_MELT = BUILDER.comment("Drakonid (gold/leather armor) melts").define("drakonid_armor_mel", true);
		DRAKONID_WATER_HURT = BUILDER.comment("Drakonid hurt by water").define("drakonid_water_hurt", true);
		BUILDER.pop();
		BUILDER.push("Cosmetics");
		ELF_EARS = BUILDER.define("elf_ears", true);
		ORC_TUSKS = BUILDER.define("orc_tusks", true);
		MERFOLK_FORM = BUILDER.define("merfolk_form", true);
		DWARF_RESIZE = BUILDER.define("dwarf_resize", true);
		ELF_RESIZE = BUILDER.define("elf_resize", true);
		ORC_RESIZE = BUILDER.define("orc_resize", true);
		BUILDER.pop();
		SPEC = BUILDER.build();
	}
}

//public class SimpleRPGRacesConfiguration {
//	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
//	public static final ForgeConfigSpec SPEC;
//
//	static {
//
//		SPEC = BUILDER.build();
//	}
//
//}

package net.simpleraces.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.simpleraces.world.inventory.ArachaSelectMenu;


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
	public static final ForgeConfigSpec.ConfigValue<Integer> DRAKONID_HEAT_PER_ATTACK;
	public static final ForgeConfigSpec.ConfigValue<Integer> DRAKONID_MAX_HEAT;
	public static final ForgeConfigSpec.ConfigValue<Integer> DRAKONID_OVERHEAT_TIME;
	public static final ForgeConfigSpec.ConfigValue<Float> DRAKONID_FALL_MULTIPLY;
	public static final ForgeConfigSpec.ConfigValue<Integer> DRAKONID_OVERHEAT_FIRE_TIME;
	public static final ForgeConfigSpec.ConfigValue<Float> FAIRY_FALL_MULTIPLY;
	public static final ForgeConfigSpec.ConfigValue<Float> FAIRY_FLY_SPEED_MULTIPLY;
	public static final ForgeConfigSpec.ConfigValue<Double> FAIRY_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_EARS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ORC_TUSKS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MERFOLK_FORM;
	public static final ForgeConfigSpec.ConfigValue<Double> MERFOLK_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DWARF_RESIZE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ELF_RESIZE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ORC_RESIZE;
	public static final ForgeConfigSpec.ConfigValue<Double> ARACHA_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Double> HALFDEAD_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Double> WEREFOLF_MAX_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Double> SERPENTIN_MAX_HEALTH;

	public static final ForgeConfigSpec.ConfigValue<Integer> FAIRY_MAX_FLYING_TIME;


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
		MERFOLK_MAX_HEALTH = BUILDER.comment("Merfolk max health").define("merfolk_max_health", 18.0);
		BUILDER.pop();
		BUILDER.push("Drakonid Class Attributes");
		DRAK_FIRE = BUILDER.comment("Apply fire aspect to attacks").define("drak_fire", true);
		DRAK_ATTACK_DAMAGE = BUILDER.comment("Drakonid attack damage bonus").define("drak_attack_damage", 1.25);
		DRAK_FIRE_RES = BUILDER.comment("Whether or not to resist damage from fire sources").define("drak_fire_res", true);
		DRAKONID_ARMOR_MELT = BUILDER.comment("Drakonid (gold/leather armor) melts").define("drakonid_armor_mel", true);
		DRAKONID_WATER_HURT = BUILDER.comment("Drakonid hurt by water").define("drakonid_water_hurt", true);
		DRAKONID_HEAT_PER_ATTACK = BUILDER.comment("Heat generated per attack").define("drakonid_heat_per_attack", 4);
		DRAKONID_MAX_HEAT = BUILDER.comment("Maximum heat level before overheating").define("drakonid_max_heat", 100);
		DRAKONID_OVERHEAT_TIME = BUILDER.comment("Time in seconds of overheating").define("drakonid_overheat_time", 20 * 30);
		DRAKONID_FALL_MULTIPLY = BUILDER.comment("Drakonid fall damage multiplier").define("drakonid_fall_multiply", 0.85f);
		DRAKONID_OVERHEAT_FIRE_TIME = BUILDER.comment("Time in seconds fire duration after overheating").define("drakonid_overheat_fire_time", 5);
		BUILDER.pop();
		BUILDER.push("Fairy Class Attributes");
		FAIRY_FALL_MULTIPLY = BUILDER.comment("Fairy fall damage multiplier").define("fairy_fall_multiply", 0.75f);
		FAIRY_FLY_SPEED_MULTIPLY = BUILDER.comment("Fairy fly speed multiplier").define("fairy_fly_speed_multiply", 1.0f);
		FAIRY_MAX_HEALTH = BUILDER.comment("Fairy max health").define("fairy_max_health", 8.0);
		FAIRY_MAX_FLYING_TIME = BUILDER.comment("Fairy flying time(seconds)").define("fairy_max_flying_time", 10);
		BUILDER.pop();
		BUILDER.push("Another Class Attributes");
		ARACHA_MAX_HEALTH = BUILDER.comment("Aracha max health").define("aracha_max_health", 16.0);
		HALFDEAD_MAX_HEALTH = BUILDER.comment("Halfdead max health").define("halfdead_max_health", 23.0);
		WEREFOLF_MAX_HEALTH = BUILDER.comment("Werewolf max health").define("werewolf_max_health", 20.0);
		SERPENTIN_MAX_HEALTH = BUILDER.comment("Serpentin max health").define("serpentin_max_health", 20.0);
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

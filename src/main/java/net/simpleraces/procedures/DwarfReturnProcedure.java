package net.simpleraces.procedures;

import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.entity.DwarfModelEntity;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class DwarfReturnProcedure {
	public static Entity execute(LevelAccessor world) {
		return world instanceof Level _level ? new DwarfModelEntity(SimpleracesModEntities.DWARF_MODEL.get(), _level) : null;
	}
}

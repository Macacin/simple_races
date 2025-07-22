package net.simpleraces.procedures;

import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.entity.OrcModelEntity;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class OrcReturnProcedure {
	public static Entity execute(LevelAccessor world) {
		return world instanceof Level _level ? new OrcModelEntity(SimpleracesModEntities.ORC_MODEL.get(), _level) : null;
	}
}

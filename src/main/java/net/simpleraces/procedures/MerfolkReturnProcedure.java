package net.simpleraces.procedures;

import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.entity.MerfolkModelEntity;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class MerfolkReturnProcedure {
	public static Entity execute(LevelAccessor world) {
		return world instanceof Level _level ? new MerfolkModelEntity(SimpleracesModEntities.MERFOLK_MODEL.get(), _level) : null;
	}
}

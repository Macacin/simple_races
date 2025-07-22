package net.simpleraces.procedures;

import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.entity.DragonModelEntity;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class DragonReturnProcedure {
	public static Entity execute(LevelAccessor world) {
		return world instanceof Level _level ? new DragonModelEntity(SimpleracesModEntities.DRAGON_MODEL.get(), _level) : null;
	}
}

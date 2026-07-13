package net.simpleraces.procedures;

import net.minecraft.world.entity.Entity;
import net.simpleraces.network.SimpleracesModVariables;

public class SelectRandomRaceProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (SimpleracesModVariables.getPlayerVariables(entity).selected)
			return;

		switch (entity.level().random.nextInt(12)) {
			case 0 -> SelectedDwarfProcedure.execute(entity);
			case 1 -> SelectedElfProcedure.execute(entity);
			case 2 -> SelectedOrcProcedure.execute(entity);
			case 3 -> SelectedMerfolkProcedure.execute(entity);
			case 4 -> SelectedDragonProcedure.execute(entity);
			case 5 -> SelectedFairyProcedure.execute(entity);
			case 6 -> SelectedWerewolfProcedure.execute(entity);
			case 7 -> SelectedSerpentinProcedure.execute(entity);
			case 8 -> SelectedArachaProcedure.execute(entity);
			case 9 -> SelectedHalfdeadProcedure.execute(entity);
			case 10 -> SelectedGargoyleProcedure.execute(entity);
			default -> SelectedHumanProcedure.execute(entity);
		}
	}
}





package net.simpleraces.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.simpleraces.SimpleracesMod;

public final class SimpleracesDamageTypes {
	public static final ResourceKey<DamageType> HALFDEAD_AURA = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(SimpleracesMod.MODID, "halfdead_aura"));

	private SimpleracesDamageTypes() {
	}

	public static DamageSource halfdeadAura(Level level) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HALFDEAD_AURA));
	}

	public static DamageSource halfdeadAura(Level level, Entity attacker) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HALFDEAD_AURA), attacker);
	}

	public static DamageSource halfdeadAuraDirect(Level level, Entity directEntity) {
		return new DamageSource(
				level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HALFDEAD_AURA),
				directEntity,
				null
		);
	}
}





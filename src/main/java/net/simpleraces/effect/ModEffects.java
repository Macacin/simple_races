package net.simpleraces.effect;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.world.inventory.ArachaSelectMenu;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SimpleracesMod.MODID);

    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", BleedingMobEffect::new);
    public static final RegistryObject<MobEffect> WEREWOLF_TRANSFORMATION = MOB_EFFECTS.register("werwolf_transformation", WerewolfTransformationEffect::new);
    public static final RegistryObject<MobEffect> DEATH_MARK = MOB_EFFECTS.register("death_mark", DeathMarkEffect::new);

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }
}
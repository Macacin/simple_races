package net.simpleraces.effect;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.world.inventory.ArachaSelectMenu;

import java.util.UUID;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SimpleracesMod.MODID);

    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", BleedingMobEffect::new);
    public static final RegistryObject<MobEffect> WEREWOLF_TRANSFORMATION = MOB_EFFECTS.register("werwolf_transformation", WerewolfTransformationEffect::new);
    public static final RegistryObject<MobEffect> DEATH_MARK = MOB_EFFECTS.register("death_mark", DeathMarkEffect::new);

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }

    public static final RegistryObject<MobEffect> FERVOR = MOB_EFFECTS.register("fervor", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000) {
        private static final UUID ATTACK_SPEED_UUID = UUID.fromString("1f6c1b94-2d7d-42f8-bbdc-a4a58cebede3");  // Уникальный UUID для modifier

        @Override
        public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
            AttributeInstance speed = entity.getAttribute(Attributes.ATTACK_SPEED);
            if (speed != null) {
                speed.addTransientModifier(new AttributeModifier(ATTACK_SPEED_UUID, "Fervor attack speed", (amplifier + 1) * 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));  // +10% per stack, один раз
            }
        }

        @Override
        public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
            AttributeInstance speed = entity.getAttribute(Attributes.ATTACK_SPEED);
            if (speed != null) {
                speed.removeModifier(ATTACK_SPEED_UUID);  // Удаляем когда эффект спадает
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return false;  // Не нужно каждый тик — modifier применяется один раз
        }
    });
}
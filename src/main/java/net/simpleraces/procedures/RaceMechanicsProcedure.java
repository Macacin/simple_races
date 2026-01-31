package net.simpleraces.procedures;

import com.jabroni.weightmod.capability.WeightCapabilities;
import com.jabroni.weightmod.event.ArmorWeightCalculationEvent;
import net.minecraft.core.particles.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.*;
import net.simpleraces.init.SimpleracesModItems;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.SimpleracesMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.common.ForgeMod;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber
public class RaceMechanicsProcedure {
    private static final UUID MERFOLK_LAND_DAMAGE_UUID = UUID.fromString("e7e3c8a0-1d2b-4c9f-a1be-123456789abc");
    private static final UUID MERFOLK_WATER_DAMAGE_UUID = UUID.fromString("d9f6b0c2-3e97-4a84-8f45-abcdef012345");
    private static final UUID MERFOLK_SWIM_SPEED_BOOST_UUID = UUID.fromString("a1a3b1e3-b0c9-d4e2-f3e2-d3a7abb3a0b1");
    private static final UUID MERFOLK_WATER_HEALTH_BOOST_UUID = UUID.fromString("a4a4b1e3-b0c4-d4e2-f3e2-d3a7a4b3a0b1");
    private static final UUID MERFOLK_LAND_SPEED_PENALTY_UUID = UUID.fromString("a1a1b1e9-b0c9-d4e2-f5e2-d3a7abb9a0b1");
    private static final UUID ELF_SPEED_UUID = UUID.fromString("f2b4c6d8-7e9f-4a12-b3c4-556677889901");
    private static final UUID ORC_DAMAGE_PENALTY_UUID = UUID.fromString("b4b4c6d8-7e9f-4a12-b3c4-556677889900");
    private static final UUID ORC_MAGIC_PENALTY_UUID = UUID.fromString("c5d4e6f8-2d3e-4f5a-b6c7-d8e9f0a1b2c3");
    private static final UUID ORC_LOW_HEALTH_BOOST_UUID = UUID.fromString("d6e7f8a9-b0c1-d2e3-f4e5-d6a728b9a0b1");
    private static final UUID ELF_BOW_BOOST_UUID = UUID.fromString("b6a2f8e9-b0c1-d2e3-f4e5-d6a7abb9a0b1");
    private static final UUID WEREWOLF_BEAST_DAMAGE_UUID = UUID.fromString("90e188f6-6f25-4b3f-b2bc-4f020d4e4bf7");
    private static final UUID WEREWOLF_HUMAN_DAMAGE_UUID = UUID.fromString("776ac817-8d9e-4ce3-8727-1ab84e7373fe");
    private static final UUID WEREWOLF_BEAST_SPEED_UUID = UUID.fromString("ca8e2dbb-0054-431b-96b5-d37a59397f75");
    private static final UUID WEREWOLF_HUMAN_SPEED_UUID = UUID.fromString("99044fb5-f130-41ea-9124-dc3d9a773586");
    private static final UUID WEREWOLF_BEAST_HEALTH_UUID = UUID.fromString("9809562a-faa3-45f9-83a7-4eb9228b9c5b");
    private static final UUID WEREWOLF_HUMAN_HEALTH_UUID = UUID.fromString("c561d21a-47fd-40ed-ab8b-8d457f4c6557");
    private static final UUID ARACHA_ATTACK_SPEED_BOOST_UUID = UUID.fromString("76557286-c292-421d-8c2e-f7b7bc77abd9");

    private static final String TAG_PST_FAIRY_EXTRA = "pst_fairy_extra_flight_ticks";
    private static final String TAG_PST_FAIRY_EXTRA_SPENT = "pst_fairy_extra_spent_ticks";

    private static final int PST_FAIRY_TOTAL_MAX_TICKS = 30 * 20; // 30 секунд

    private static final Map<UUID, Map<MobEffect, Integer>> preAttackDebuffs = new HashMap<>();

    private static final Set<ResourceKey<Biome>> FOREST_BIOMES = Set.of(
            Biomes.FOREST,
            Biomes.FLOWER_FOREST,
            Biomes.BIRCH_FOREST,
            Biomes.OLD_GROWTH_BIRCH_FOREST,
            Biomes.DARK_FOREST,
            Biomes.TAIGA,
            Biomes.OLD_GROWTH_PINE_TAIGA,
            Biomes.OLD_GROWTH_SPRUCE_TAIGA,

            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "aspen_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "autumnal_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "autumnal_valley")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "black_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "cherry_blossom_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "cika_woods")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "deciduous_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "ebony_woods")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "forgotten_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "fragment_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "glowshroom_bayou")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "jacaranda_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "maple_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "mega_spruce_taiga")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "mega_coniferous_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "orchard")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "pine_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "red_oak_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "redwood_thicket")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "seasonal_birch_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "seasonal_deciduous_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "seasonal_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "snowy_deciduous_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "temperate_rainforest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "twilight_meadow")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "weeping_witch_forest")),
            ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "zelkova_forest"))
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;
        Level level = player.level();
        execute(event, player.level(), player.getX(), player.getY(), player.getZ(), player);

        SimpleracesModVariables.PlayerVariables vars = player.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());

        player.getCapability(WeightCapabilities.CAPABILITY).ifPresent(cap -> {
            if (vars.elf) {
                cap.setCapacity(SimpleRPGRacesConfiguration.ELF_CARRY_CAPACITY.get());
            } else if (vars.orc) {
                cap.setCapacity(SimpleRPGRacesConfiguration.ORC_CARRY_CAPACITY.get());
            } else if (vars.dwarf) {
                cap.setCapacity(SimpleRPGRacesConfiguration.DWARF_CARRY_CAPACITY.get());
            } else if (vars.merfolk) {
                cap.setCapacity(SimpleRPGRacesConfiguration.MERFOLK_CARRY_CAPACITY.get());
            } else if (vars.dragon) {
                cap.setCapacity(SimpleRPGRacesConfiguration.DRAKONID_CARRY_CAPACITY.get());
            } else if (vars.fairy) {
                cap.setCapacity(SimpleRPGRacesConfiguration.FAIRY_CARRY_CAPACITY.get());
            } else if (vars.werewolf) {
                cap.setCapacity(SimpleRPGRacesConfiguration.WEREWOLF_CARRY_CAPACITY.get());
            } else if (vars.serpentin) {
                cap.setCapacity(SimpleRPGRacesConfiguration.SERPENTIN_CARRY_CAPACITY.get());
            } else if (vars.aracha) {
                cap.setCapacity(SimpleRPGRacesConfiguration.ARACHA_CARRY_CAPACITY.get());
            } else if (vars.halfdead) {
                cap.setCapacity(SimpleRPGRacesConfiguration.HALFDEAD_CARRY_CAPACITY.get());
            } else {
                cap.setCapacity(10);
            }
        });

        if (vars.merfolk) {
            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance swimSpeedAttr = player.getAttribute(ForgeMod.SWIM_SPEED.get());

            assert healthAttr != null;
            if (player.isInWater()) {
                healthAttr.removeModifier(MERFOLK_WATER_HEALTH_BOOST_UUID);
                healthAttr.addTransientModifier(new AttributeModifier(
                        MERFOLK_WATER_HEALTH_BOOST_UUID,
                        "Merfolk water health boost",
                        SimpleRPGRacesConfiguration.MERFOLK_WATER_HEALTH_BOOST.get(),
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
                if (player.getHealth() > healthAttr.getValue()) {
                    player.setHealth((float) healthAttr.getValue());
                }

                if (dmgAttr != null) {
                    dmgAttr.removeModifier(MERFOLK_LAND_DAMAGE_UUID);
                    if (dmgAttr.getModifier(MERFOLK_WATER_DAMAGE_UUID) == null) {
                        dmgAttr.addTransientModifier(new AttributeModifier(
                                MERFOLK_WATER_DAMAGE_UUID,
                                "Merfolk water damage bonus",
                                SimpleRPGRacesConfiguration.MERFOLK_ATTACK_DAMAGE_WATER.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 0, false, false));

                if (swimSpeedAttr != null) {
                    swimSpeedAttr.removeModifier(MERFOLK_SWIM_SPEED_BOOST_UUID);
                    swimSpeedAttr.addTransientModifier(new AttributeModifier(
                            MERFOLK_SWIM_SPEED_BOOST_UUID,
                            "Merfolk swim speed boost",
                            SimpleRPGRacesConfiguration.MERFOLK_SWIM_SPEED.get() - 1.0,
                            AttributeModifier.Operation.ADDITION
                    ));
                }

                // Новое: быстрее копание в воде (Haste)
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, SimpleRPGRacesConfiguration.MERFOLK_DIG_SPEED_WATER.get(), false, false));

            } else {
                healthAttr.removeModifier(MERFOLK_WATER_HEALTH_BOOST_UUID);
                if (player.getHealth() > healthAttr.getValue()) {
                    player.setHealth((float) healthAttr.getValue());
                }

                if (dmgAttr != null) {
                    dmgAttr.removeModifier(MERFOLK_WATER_DAMAGE_UUID);
                    if (dmgAttr.getModifier(MERFOLK_LAND_DAMAGE_UUID) == null) {
                        dmgAttr.addTransientModifier(new AttributeModifier(
                                MERFOLK_LAND_DAMAGE_UUID,
                                "Merfolk land damage penalty",
                                SimpleRPGRacesConfiguration.MERFOLK_DAMAGE_PENALTY_SURFACE.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                if (speedAttr != null) {
                    speedAttr.removeModifier(MERFOLK_LAND_SPEED_PENALTY_UUID);
                    speedAttr.addTransientModifier(new AttributeModifier(
                            MERFOLK_LAND_SPEED_PENALTY_UUID,
                            "Merfolk land speed penalty",
                            SimpleRPGRacesConfiguration.MERFOLK_SURFACE_SPEED_PENALTY.get(),
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }

                if (swimSpeedAttr != null) {
                    swimSpeedAttr.removeModifier(MERFOLK_SWIM_SPEED_BOOST_UUID);
                }
            }
        } else if (vars.dragon) {
            if (player.isInWater()) {
                player.hurt(player.damageSources().fellOutOfWorld(), 2.0F);
            }
            player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
                int maxHeat = SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get();
                int maxOverheatTicks = SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_TIME.get() * 20;

                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new SyncHeatPacket(
                                player.getUUID(),
                                data.getHeat(),
                                maxHeat,
                                data.isOverheated(),
                                data.getOverheatTicks(),
                                maxOverheatTicks
                        ));
                if (data.isOverheated()) {
                    int ticks = data.getOverheatTicks() - 1;
                    data.setOverheatTicks(ticks);

                    maxHeat = SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get();
                    if (ticks % 10 == 0) {
                        float stepAmount = maxHeat / 40f;
                        data.setHeat((int) (data.getHeat() - stepAmount));
                        if (data.getHeat() < 0) data.setHeat(0);
                    }

                    if (ticks % 10 == 0 || ticks <= 0) {
                        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                new SyncHeatPacket(
                                        player.getUUID(),
                                        data.getHeat(),
                                        maxHeat,
                                        data.isOverheated(),
                                        data.getOverheatTicks(),
                                        maxOverheatTicks
                                ));
                    }

                    if (player.tickCount % 60 == 0) {
                        player.hurt(player.damageSources().fellOutOfWorld(), 1);
                    }
                    if (ticks % 100 == 0) {
                        player.hurt(player.damageSources().onFire(), 1.0F);
                    }
                    if (ticks <= 0) {
                        data.setOverheated(false);
                        data.setHeat(0);
                    }
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2, 0, true, false));
                    BlockPos pos = player.blockPosition().below();
                    if (level.getBlockState(pos).isAir() &&
                            Blocks.FIRE.defaultBlockState().canSurvive(level, pos)) {
                        level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                    }
                }
            });

            // ===== FAIRY (НОВАЯ ЛОГИКА: единый spentTicks, фикс бесконечного полёта) =====
            // ===== FAIRY (цельный блок для вставки ВМЕСТО твоего текущего `} else if (vars.fairy) { ... }` ) =====
        } else if (vars.fairy) {
            CompoundTag data = player.getPersistentData();

            // --- базовые тики ---
            int flightTime = data.getInt("fairy_flight_ticks");
            int fallingTime = data.getInt("fairy_falling_ticks");

            // base max (из конфига)
            int baseMax = SimpleRPGRacesConfiguration.FAIRY_MAX_FLYING_TIME.get() * 20;

            // WindWings: общий cap 30 сек (если включено)
            final String TAG_PST_WIND_WINGS = "pst_fairy_wind_wings";
            final String TAG_PST_EXTRA_SPENT = "pst_fairy_extra_spent_ticks";
            final int PST_FAIRY_TOTAL_MAX_TICKS = 30 * 20;

            boolean windWingsEnabled = data.getBoolean(TAG_PST_WIND_WINGS);

            int extraCap = Math.max(0, PST_FAIRY_TOTAL_MAX_TICKS - baseMax); // сколько “доп. тиков” максимум можно потратить
            int effectiveMax = windWingsEnabled ? (baseMax + extraCap) : baseMax;

            int extraSpent = data.getInt(TAG_PST_EXTRA_SPENT);
            if (extraSpent < 0) extraSpent = 0;
            if (extraSpent > extraCap) extraSpent = extraCap;

            // --- расход полёта ---
            // Базовое время тратим через flightTime, а доп. время тратим через extraSpent (чтобы не было “67%” скачков).
            if (player.getAbilities().flying) {
                // тратим базу
                if (flightTime < baseMax) {
                    flightTime++;
                } else {
                    // база уже исчерпана — тратим extra
                    if (windWingsEnabled && extraSpent < extraCap) {
                        extraSpent++;
                        data.putInt(TAG_PST_EXTRA_SPENT, extraSpent);
                    }
                }
            }

            int spent = Math.min(flightTime, baseMax) + extraSpent;

            // recovery включаем по реальному потраченному ресурсу, а не по flightTime
            boolean isRecovering = (spent >= effectiveMax);

            // --- HUD значения для Sync пакета ---
            int hudBar;
            int hudMax;

            if (isRecovering) {
                // фаза восстановления: бар “зарядки”
                hudBar = fallingTime;
                hudMax = baseMax; // как было у тебя: восстановление по базовому времени (20с)
            } else {
                // фаза траты: бар “расхода”
                hudBar = spent;
                hudMax = effectiveMax; // 20с или 30с (если windwings)
            }

            // --- геймплей: восстановление / полёт ---
            if (isRecovering) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();

                // логика падения/восстановления как у тебя
                if (!data.getBoolean("falled") && player.getY() < player.yo) {
                    if (player.onGround()) {
                        data.putBoolean("falled", true);

                        // ❗️ВАЖНО: больше НЕ делаем "рефанд" flightTime при касании земли (он и создавал ~67% при windwings).
                        // Ничего не трогаем тут.

                    } else {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 0, false, true));
                        fallingTime++;
                    }
                }

                if (data.getBoolean("falled")) {
                    fallingTime++;
                }

                // когда восстановление завершено
                if (fallingTime >= baseMax) {
                    flightTime = 0;
                    fallingTime = 0;
                    extraSpent = 0;
                    data.putInt(TAG_PST_EXTRA_SPENT, 0);

                    player.level().playSound(null, player.blockPosition(), SimpleracesMod.FAIRY_RECOVER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            } else {
                // не recovering — фея может летать
                player.getAbilities().mayfly = true;
                data.putBoolean("falled", false);

                player.getAbilities().setFlyingSpeed(0.025f * SimpleRPGRacesConfiguration.FAIRY_FLY_SPEED_MULTIPLY.get());
                player.onUpdateAbilities();

                // (опционально) если ты хочешь ускорять восстановление brightmind — это должно быть отдельно
            }

            // --- сохраняем тики ---
            data.putInt("fairy_flight_ticks", flightTime);
            data.putInt("fairy_falling_ticks", fallingTime);

            // --- частицы ---
            ((ServerLevel) player.level()).sendParticles(
                    new DustColorTransitionOptions(
                            new Vector3f(1f, 1f, 1f),
                            new Vector3f(0.8f, 0.8f, 0.8f),
                            1.4f
                    ),
                    player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0
            );

            // --- синхронизация HUD ---
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new SyncFairyFlightPacket(player.getUUID(), hudBar, hudMax, isRecovering));
        }
// ===== /FAIRY =====

        else if (vars.aracha) {
            if (player.isInWater()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        SimpleRPGRacesConfiguration.ARACHA_WATER_SLOWDOWN_DURATION.get(),
                        SimpleRPGRacesConfiguration.ARACHA_WATER_SLOWDOWN_AMPLIFIER.get(), false, false));
            }
            AttributeInstance attackSpeedAttr = player.getAttribute(Attributes.ATTACK_SPEED);
            if (attackSpeedAttr != null) {
                attackSpeedAttr.removeModifier(ARACHA_ATTACK_SPEED_BOOST_UUID);
                attackSpeedAttr.addTransientModifier(new AttributeModifier(
                        ARACHA_ATTACK_SPEED_BOOST_UUID,
                        "Aracha attack speed boost",
                        SimpleRPGRacesConfiguration.ARACHA_ATTACK_SPEED_BONUS.get(),
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }
        } else if (vars.werewolf) {
            AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);

            if (WerewolfState.isBeast(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 0, false, true));

                // Damage modifier
                if (dmgAttr != null) {
                    dmgAttr.removeModifier(WEREWOLF_HUMAN_DAMAGE_UUID);
                    if (dmgAttr.getModifier(WEREWOLF_BEAST_DAMAGE_UUID) == null) {
                        dmgAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_BEAST_DAMAGE_UUID,
                                "Werewolf beast damage bonus",
                                SimpleRPGRacesConfiguration.WEREWOLF_BEAST_DAMAGE_BONUS.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                if (speedAttr != null) {
                    speedAttr.removeModifier(WEREWOLF_HUMAN_SPEED_UUID);
                    if (speedAttr.getModifier(WEREWOLF_BEAST_SPEED_UUID) == null) {
                        speedAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_BEAST_SPEED_UUID,
                                "Werewolf beast speed bonus",
                                SimpleRPGRacesConfiguration.WEREWOLF_BEAST_SPEED_BONUS.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                if (healthAttr != null) {
                    healthAttr.removeModifier(WEREWOLF_HUMAN_HEALTH_UUID);
                    if (healthAttr.getModifier(WEREWOLF_BEAST_HEALTH_UUID) == null) {
                        healthAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_BEAST_HEALTH_UUID,
                                "Werewolf beast health bonus",
                                SimpleRPGRacesConfiguration.WEREWOLF_BEAST_HEALTH_BONUS.get(),
                                AttributeModifier.Operation.ADDITION
                        ));
                    }
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                }

            } else {
                if (dmgAttr != null) {
                    dmgAttr.removeModifier(WEREWOLF_BEAST_DAMAGE_UUID);
                    if (dmgAttr.getModifier(WEREWOLF_HUMAN_DAMAGE_UUID) == null) {
                        dmgAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_HUMAN_DAMAGE_UUID,
                                "Werewolf human damage penalty",
                                SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_DAMAGE_PENALTY.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                if (speedAttr != null) {
                    speedAttr.removeModifier(WEREWOLF_BEAST_SPEED_UUID);
                    if (speedAttr.getModifier(WEREWOLF_HUMAN_SPEED_UUID) == null) {
                        speedAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_HUMAN_SPEED_UUID,
                                "Werewolf human speed penalty",
                                SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_SPEED_PENALTY.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                if (healthAttr != null) {
                    healthAttr.removeModifier(WEREWOLF_BEAST_HEALTH_UUID);
                    if (healthAttr.getModifier(WEREWOLF_HUMAN_HEALTH_UUID) == null) {
                        healthAttr.addTransientModifier(new AttributeModifier(
                                WEREWOLF_HUMAN_HEALTH_UUID,
                                "Werewolf human health penalty",
                                SimpleRPGRacesConfiguration.WEREWOLF_HUMAN_HEALTH_PENALTY.get(),
                                AttributeModifier.Operation.ADDITION
                        ));
                    }
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                }
            }
        } else if (vars.elf) {
            if (level.getBrightness(LightLayer.SKY, player.getOnPos().above(2)) > 0 && FOREST_BIOMES.contains(level.getBiome(player.getOnPos()).unwrapKey().get())) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 5, 0, false, true));
                AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null && speedAttr.getModifier(ELF_SPEED_UUID) == null) {
                    speedAttr.addTransientModifier(new AttributeModifier(
                            ELF_SPEED_UUID,
                            "Elf speed boost",
                            SimpleRPGRacesConfiguration.ELF_FOREST_SPEED_BUFF.get(),
                            AttributeModifier.Operation.MULTIPLY_BASE
                    ));
                }
            } else {
                AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) {
                    speedAttr.removeModifier(ELF_SPEED_UUID);
                }
            }

            boolean inForest = FOREST_BIOMES.contains(level.getBiome(player.getOnPos()).unwrapKey().get()); // Переиспользуем существующую проверку биома
            double cooldownStep = inForest ? SimpleRPGRacesConfiguration.ELF_FOREST_COOLDOWN_MULTIPLIER.get() : 1;

            for (int i = 0; i < SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get(); i++) {
                if (vars.spiritCooldowns[i] > 0) {
                    vars.spiritCooldowns[i] -= (int) cooldownStep;
                    if (vars.spiritCooldowns[i] <= 0) {
                        vars.spiritCooldowns[i] = 0;
                        vars.forestSpirits = Math.min(SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get(), vars.forestSpirits + 1); // Восстанавливаем дух

                        player.level().playSound(null, player.blockPosition(), SimpleracesMod.FAIRY_RECOVER.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
                    }
                }
            }
            vars.syncPlayerVariables(player); // Синхронизируем изменения (это уже было в твоей инструкции, но если синхронизация нужна только при изменениях, можно оптимизировать)
        } else if (vars.orc) {
            AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (dmgAttr != null) {
                dmgAttr.removeModifier(ORC_DAMAGE_PENALTY_UUID);
                dmgAttr.addTransientModifier(new AttributeModifier(
                        ORC_DAMAGE_PENALTY_UUID,
                        "Orc damage penalty",
                        SimpleRPGRacesConfiguration.ORC_DAMAGE_PENALTY.get(),
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }
            String[] spellSchools = {"spell_power", "fire_spell_power", "ice_spell_power", "lightning_spell_power", "holy_spell_power", "ender_spell_power",
                    "blood_spell_power", "evocation_spell_power", "nature_spell_power", "eldritch_spell_power"};

            CompoundTag pst = player.getPersistentData();
            long gt = player.level().getGameTime();
            boolean shamanRage = pst.getLong("pst_orc_shaman_rage_until") >= gt;

            for (String attrName : spellSchools) {
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", attrName));
                if (attribute != null) {
                    AttributeInstance instance = player.getAttribute(attribute);
                    if (instance != null) {
                        instance.removeModifier(ORC_MAGIC_PENALTY_UUID);

                        // если камня нет — штраф остаётся
                        if (!shamanRage) {
                            instance.addTransientModifier(new AttributeModifier(
                                    ORC_MAGIC_PENALTY_UUID,
                                    "Orc magic penalty",
                                    SimpleRPGRacesConfiguration.ORC_MAGIC_DAMAGE_PENALTY.get(),
                                    AttributeModifier.Operation.MULTIPLY_TOTAL
                            ));
                        }
                    }
                }
            }

            if (dmgAttr != null) {
                dmgAttr.removeModifier(ORC_LOW_HEALTH_BOOST_UUID);
                if (player.getHealth() < 8.0f) {
                    dmgAttr.addTransientModifier(new AttributeModifier(
                            ORC_LOW_HEALTH_BOOST_UUID,
                            "Orc low health boost",
                            SimpleRPGRacesConfiguration.ORC_RAGE.get(),
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
            }
            float exhaustion = player.getFoodData().getExhaustionLevel();
            player.getFoodData().setExhaustion(exhaustion * 1.15f);

            int currentHunger = player.getFoodData().getFoodLevel();
            if (currentHunger == 20 && vars.previousFoodLevel < 20) {
                player.level().playSound(null, player.blockPosition(), SimpleracesMod.ORC_ROAR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                player.level().playSound(null, player.blockPosition(), SimpleracesMod.ORC_RAGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                List<LivingEntity> enemies = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10), e -> e != player && e.isAlive());
                pst = player.getPersistentData();
                gt = player.level().getGameTime();
                boolean dumbPlan = pst.getLong("pst_orc_dumb_plan_until") >= gt;

                int stunDuration = 60 + (dumbPlan ? 40 : 0); // +2 секунды

                for (LivingEntity e : enemies) {
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, stunDuration, 255, false, false));
                }
                if (dumbPlan && player.getRandom().nextFloat() < 0.20f) {
                    List<Player> allies = player.level().getEntitiesOfClass(
                            Player.class,
                            player.getBoundingBox().inflate(10),
                            p -> p != player && p.isAlive()
                    );

                    for (Player ally : allies) {
                        ally.getActiveEffects().stream()
                                .filter(eff -> !eff.getEffect().isBeneficial())
                                .map(MobEffectInstance::getEffect)
                                .toList()
                                .forEach(ally::removeEffect);
                    }
                }

                player.getActiveEffects().stream().filter(effectInstance -> !effectInstance.getEffect().isBeneficial()).forEach(effectInstance -> player.removeEffect(effectInstance.getEffect()));

                player.getPersistentData().putBoolean("orc_rage_strike", true);
            }

            vars.previousFoodLevel = currentHunger;
            vars.syncPlayerVariables(player);
            CompoundTag persistentData = player.getPersistentData();

            boolean hasFervorNow = player.hasEffect(ModEffects.FERVOR.get());

            boolean exitingFervor = (!hasFervorNow && vars.fervorStacks > 0);

            if (exitingFervor) {

                CompoundTag data = player.getPersistentData();
                boolean skipStupor = data.getBoolean("pst_orc_skip_stupor");

                if (!skipStupor) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.MOVEMENT_SLOWDOWN,
                            SimpleRPGRacesConfiguration.ORC_FERVOR_DEBUFF_DURATION.get(),
                            SimpleRPGRacesConfiguration.ORC_FERVOR_SLOWDOWN_LEVEL.get(),
                            false, true
                    ));
                    data.putInt("orc_fervor_debuff_ticks",
                            SimpleRPGRacesConfiguration.ORC_FERVOR_DEBUFF_DURATION.get()
                    );
                }

                vars.fervorStacks = 0;
                vars.syncPlayerVariables(player);

                if (!skipStupor) {
                    player.level().playSound(null, player.blockPosition(),
                            SimpleracesMod.ORC_EXHAUSTION.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                data.remove("pst_orc_skip_stupor");
            }


            int debuffTicks = persistentData.getInt("orc_fervor_debuff_ticks");
            if (debuffTicks > 0) {

                if (hasFervorNow) {
                    persistentData.putInt("orc_fervor_debuff_ticks", 0);
                    player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                } else {
                    persistentData.putInt("orc_fervor_debuff_ticks", debuffTicks - 1);
                }
            }
        } else if (vars.serpentin) {

            CompoundTag persistentData = player.getPersistentData();
            for (MobEffectInstance inst : new ArrayList<>(player.getActiveEffects())) {
                if (!inst.getEffect().isBeneficial() && inst.getEffect() != MobEffects.POISON && inst.getEffect() != MobEffects.WITHER) {
                    String effectKey = "serpentin_shortened_" + ForgeRegistries.MOB_EFFECTS.getKey(inst.getEffect()).toString();
                    if (!persistentData.getBoolean(effectKey)) {
                        int newDuration = inst.getDuration() / 2;
                        if (newDuration > 0) {
                            player.removeEffect(inst.getEffect());
                            player.addEffect(new MobEffectInstance(inst.getEffect(), newDuration, inst.getAmplifier(), inst.isAmbient(), inst.isVisible(), inst.showIcon()));
                        }
                        persistentData.putBoolean(effectKey, true);
                    }
                } else {
                    String effectKey = "serpentin_shortened_" + ForgeRegistries.MOB_EFFECTS.getKey(inst.getEffect()).toString();
                    persistentData.remove(effectKey);
                }
            }
        } else if (vars.halfdead) {
            if (player.tickCount % 100 == 0) {
                AABB aabb = new AABB(player.getX() - 5, player.getY() - 5, player.getZ() - 5, player.getX() + 5, player.getY() + 5, player.getZ() + 5);
                List<LivingEntity> nearbyMobs = level.getEntitiesOfClass(LivingEntity.class, aabb, e -> e != player && e.isAlive() && !(e instanceof Player));
                for (LivingEntity mob : nearbyMobs) {
                    mob.hurt(player.damageSources().indirectMagic(player, player), SimpleRPGRacesConfiguration.HALFDEAD_AURA_DAMAGE.get().floatValue());
                    double dx = mob.getX() - player.getX();
                    double dz = mob.getZ() - player.getZ();

                    double d2 = dx * dx + dz * dz;
                    if (d2 < 1.0e-6) {
                        // можно чуть-чуть толкнуть в сторону взгляда игрока, чтобы гарантированно "отлипало"
                        var look = player.getLookAngle();
                        mob.knockback(0.1f, look.x, look.z);
                    } else {
                        double invLen = 1.0 / Math.sqrt(d2);
                        double nx = dx * invLen;
                        double nz = dz * invLen;

                        // сила — подбери по вкусу (обычно 0.3–0.8 комфортно)
                        mob.knockback(0.25f, nx, nz);
                    }
                }
            }
        }
    }

    public static int FAIRY_FLYING_BAR = 0;

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        SimpleracesModVariables.PlayerVariables vars = entity.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());
        if (vars.fairy) {
            event.setDamageMultiplier(SimpleRPGRacesConfiguration.FAIRY_FALL_MULTIPLY.get());
        } else if (vars.dragon) {
            // Базовый порог: урон начинается только после 4 блоков
            float d = event.getDistance();
            if (d <= 4.0f) {
                event.setCanceled(true); // полностью отменяем урон от падения
                return;
            }

            // если >4 — применяем твой мультипликатор
            event.setDamageMultiplier(SimpleRPGRacesConfiguration.DRAKONID_FALL_MULTIPLY.get());
        }

    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent evt) {
        evt.add(EntityType.PLAYER, ForgeMod.ENTITY_REACH.get());
    }

    @SubscribeEvent
    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Animal mob)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        boolean isFairy = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY)
                .map(vars -> vars.fairy)
                .orElse(false);

        if (isFairy) {
            mob.setTarget(null);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        for (ServerLevel level : server.getAllLevels()) {
            for (Player player : level.players()) {

                boolean isFairy = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY)
                        .map(vars -> vars.fairy)
                        .orElse(false);
                if (!isFairy) continue;

                BlockPos playerPos = player.blockPosition();
                BlockPos.betweenClosedStream(playerPos.offset(-6, -2, -6), playerPos.offset(6, 2, 6)).forEach(pos -> {
                    BlockState state = level.getBlockState(pos);

                    if (state.getBlock() instanceof CropBlock crop) {
                        if (!crop.isMaxAge(state)) {
                            if (level.random.nextInt(1000) == 0) {
                                level.setBlock(pos, crop.getStateForAge(crop.getAge(state) + 1), 2);
                            }
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPotionUsed(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        LivingEntity entity = event.getEntity();
        if (!entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(vars -> vars.serpentin).orElse(false)) {
            return;
        }

        if (!(stack.getItem() instanceof PotionItem)) return;

        List<MobEffectInstance> originalEffects = PotionUtils.getMobEffects(stack);

        for (MobEffectInstance effect : originalEffects) {
            if (!effect.getEffect().isBeneficial()) continue;
            MobEffect type = effect.getEffect();
            int amplifier = effect.getAmplifier();
            int duration = effect.getDuration();

            duration = (int) (duration * SimpleRPGRacesConfiguration.SERPENTIN_POTION_DURATION_MULTIPLIER.get());

            if (type == MobEffects.DAMAGE_BOOST) {
                amplifier += SimpleRPGRacesConfiguration.SERPENTIN_STRENGTH_AMPLIFIER_BONUS.get();
            } else if (type == MobEffects.MOVEMENT_SPEED) {
                amplifier += SimpleRPGRacesConfiguration.SERPENTIN_SPEED_AMPLIFIER_BONUS.get();
            } else if (type == MobEffects.JUMP) {
                amplifier += SimpleRPGRacesConfiguration.SERPENTIN_JUMP_AMPLIFIER_BONUS.get();
            }

            MobEffectInstance modified = new MobEffectInstance(type, duration, amplifier, effect.isAmbient(), effect.isVisible(), effect.showIcon());
            entity.addEffect(modified);
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if ((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(playerVariables -> playerVariables.serpentin).orElse(false))) {
            if (entity.hasEffect(MobEffects.POISON)) {
                entity.removeEffect(MobEffects.POISON);
            }
            if (entity.hasEffect(MobEffects.WITHER)) {
                entity.removeEffect(MobEffects.WITHER);
            }
        } else if ((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(playerVariables -> playerVariables.werewolf)
                .orElse(false))) {
            if (player.level().isNight() && WerewolfState.isHuman(player)) {
                WerewolfState.transformToBeast(player);
            } else if (!player.level().isNight() && WerewolfState.isBeast(player)) {
                WerewolfState.transformToHuman(player);
            }
        }
    }

    private static boolean isWearingHeavyArmor(Player player) {
        for (ItemStack armorPiece : player.getArmorSlots()) {
            Item item = armorPiece.getItem();
            if (item instanceof ArmorItem armorItem) {
                ArmorMaterial material = armorItem.getMaterial();
                if (material == ArmorMaterials.IRON ||
                        material == ArmorMaterials.GOLD ||
                        material == ArmorMaterials.NETHERITE) {
                    return true;
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(vars -> {
            if (vars.werewolf && WerewolfState.isBeast(player)) {
                ItemStack stack = event.getItemStack();

                if (!isMeat(stack.getItem())) {
                    if (!player.level().isClientSide) {
                        player.displayClientMessage(Component.literal("В форме зверя ты можешь есть только мясо!"), true);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 0.5f, 1f);
                    }
                    event.setCanceled(true);
                }
            }
        });
    }

    private static boolean isMeat(Item item) {
        return item.isEdible() && item.getFoodProperties() != null && item.getFoodProperties().isMeat();
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (((event.getSource().getEntity() instanceof Player) && (((Player) event.getSource().getEntity()).getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables()).serpentin))) {
            boolean hasDebuff = false;
            for (MobEffectInstance effect : target.getActiveEffects()) {
                if (effect.getEffect().isBeneficial()) continue;
                hasDebuff = true;
                break;
            }
            float damage = event.getAmount();
            if (hasDebuff) {
                damage *= SimpleRPGRacesConfiguration.SERPENTIN_DAMAGE_BONUS_WITH_DEBUFF.get();
            } else {
                damage *= SimpleRPGRacesConfiguration.SERPENTIN_DAMAGE_PENALTY_WITHOUT_DEBUFF.get();
            }
            event.setAmount(damage);
        } else if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables()).aracha) {
            DamageSource source = event.getSource();
            if (source.typeHolder().is(DamageTypes.IN_FIRE)) {
                event.setAmount((float) (event.getAmount() * SimpleRPGRacesConfiguration.ARACHA_FIRE_DAMAGE_MULTIPLIER.get()));
            }
        } else if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables()).aracha) {
            DamageSource source = event.getSource();

            if (source.typeHolder().is(DamageTypes.IN_FIRE)) {
                event.setAmount(event.getAmount() * 1.5f);
            }
        } else if (event.getEntity() instanceof Player player && player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables()).werewolf) {
            if (player.getRandom().nextInt(20) == 0) {
                player.addEffect(new MobEffectInstance(
                        ModEffects.WEREWOLF_TRANSFORMATION.get(),
                        SimpleRPGRacesConfiguration.WEREWOLF_BEAST_DURATION.get(),
                        0,
                        true,   // ambient (можно false, не критично)
                        false,  // visible = частицы выключены
                        false   // showIcon = иконка выключена
                ));

            }
        } else if (event.getEntity() instanceof Player player && player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables()).dragon) {
            player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
                if (!data.isOverheated()) {
                    data.setHeat(data.getHeat() + 2);
                }
            });
        }
        if (event.getSource().getDirectEntity() instanceof AbstractArrow && event.getSource().getEntity() instanceof Player player) {
            SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new SimpleracesModVariables.PlayerVariables());
        }
        if (event.getEntity() instanceof Player player) {
            SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new SimpleracesModVariables.PlayerVariables());

            if (vars.orc) {
                CompoundTag persistentData = player.getPersistentData();
                int debuffTicks = persistentData.getInt("orc_fervor_debuff_ticks");

                if (debuffTicks > 0) {
                    event.setAmount((float) (event.getAmount() * SimpleRPGRacesConfiguration.ORC_FERVOR_INCOMING_DAMAGE_MULTIPLIER.get()));
                }
                if (event.getSource().typeHolder().is(DamageTypes.MAGIC)) {
                    float amount = event.getAmount();
                    amount *= SimpleRPGRacesConfiguration.ORC_INCOMING_MAGIC_DAMAGE_PENALTY.get();

                    CompoundTag pst = player.getPersistentData();
                    long gt = player.level().getGameTime();
                    if (pst.getLong("pst_orc_shaman_rage_until") >= gt) {
                        amount *= 0.7f; // -30% магического урона
                    }

                    event.setAmount(amount);
                }

            }
        }
        if (event.getEntity() instanceof Player player) {
            SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new SimpleracesModVariables.PlayerVariables());
            if (vars.halfdead && player.getRandom().nextFloat() < 0.15f) {
                float drainAmount = event.getAmount() * 0.3f;
                Entity sourceEntity = event.getSource().getEntity();
                if (sourceEntity instanceof LivingEntity attacker) {
                    attacker.hurt(player.damageSources().magic(), drainAmount);
                    player.heal(drainAmount * 10);  // Восстановить себе
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());
        if (player.level().isClientSide()) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;
        if (vars.serpentin && event.getTarget() instanceof LivingEntity livingTarget) {
            Map<MobEffect, Integer> currentDebuffs = new HashMap<>();
            for (MobEffectInstance inst : livingTarget.getActiveEffects()) {
                if (!inst.getEffect().isBeneficial()) {
                    currentDebuffs.put(inst.getEffect(), inst.getDuration());
                }
            }
            preAttackDebuffs.put(livingTarget.getUUID(), currentDebuffs);

            SimpleracesMod.queueServerWork(1, () -> {
                adjustDebuffDurations(livingTarget);
                preAttackDebuffs.remove(livingTarget.getUUID());
            });
        }
        if (vars.aracha) {
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag data;
            if (!persistentData.contains(Player.PERSISTED_NBT_TAG)) {
                data = new CompoundTag();
                persistentData.put(Player.PERSISTED_NBT_TAG, data);
            } else {
                data = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
            }

            int count = data.getInt("bite_count") + 1;

            // --- НОВОЕ: Mandible Cunning (skilltree камень) ---
            int threshold = SimpleRPGRacesConfiguration.ARACHA_BITE_COUNT_THRESHOLD.get(); // дефолт из конфига (у тебя это "каждые 3")
            CompoundTag pst = player.getPersistentData();
            if (pst.getBoolean("pst_aracha_mandible_cunning")) {
                threshold = 2; // каждая 2-я атака
            }
            // --- /НОВОЕ ---

            if (count >= threshold) {
                data.putInt("bite_count", 0);

                spawnFangs(player.level(), target.getX(), target.getY(), target.getZ(), player.getYRot(), player);

                target.addEffect(new MobEffectInstance(
                        MobEffects.POISON,
                        SimpleRPGRacesConfiguration.ARACHA_POISON_DURATION.get(),
                        SimpleRPGRacesConfiguration.ARACHA_POISON_AMPLIFIER.get()
                ));

                target.level().playSound(null, target.blockPosition(), SoundEvents.BEE_STING, SoundSource.PLAYERS, 1.0f, 1.0f);

                if (target.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(),
                            10, 0.2, 0.2, 0.2, 0.1);
                }
            } else {
                data.putInt("bite_count", count);
            }
        }
        else if (vars.orc) {

            if (player.getPersistentData().getBoolean("orc_rage_strike")) {
                player.getPersistentData().remove("orc_rage_strike");

                float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float rageDamage = baseDamage * 3.0f;

                List<LivingEntity> aoeTargets = player.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3), e -> e != player);
                for (LivingEntity e : aoeTargets) {
                    e.hurt(player.damageSources().playerAttack(player), rageDamage);
                }
            }

            UUID currentTarget = target.getUUID();
            if (vars.lastTarget == null || !vars.lastTarget.equals(currentTarget)) {
                vars.fervorStacks = 0;
                player.removeEffect(ModEffects.FERVOR.get());
            } else {
                CompoundTag pst = player.getPersistentData();
                long gt = player.level().getGameTime();

                int maxFervor = 10;
                if (pst.getLong("pst_orc_berserk_power_until") >= gt) {
                    maxFervor = 13;
                }

                vars.fervorStacks = Math.min(maxFervor, vars.fervorStacks + 1);
            }
            vars.lastTarget = currentTarget;

            if (vars.fervorStacks > 0) {
                int visualAmplifier = Math.min(vars.fervorStacks - 1, 9);

                player.addEffect(new MobEffectInstance(
                        ModEffects.FERVOR.get(),
                        200,
                        visualAmplifier,
                        false,
                        false
                ));
            }
            vars.syncPlayerVariables(player);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) return;

        DamageSource source = event.getSource();
        if (source.typeHolder().is(DamageTypes.FALL) || source.typeHolder().is(DamageTypes.DROWN) || source.typeHolder().is(DamageTypes.IN_FIRE)) {
            return;
        }

        SimpleracesModVariables.PlayerVariables vars = player.getCapability(
                SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
        ).orElse(new SimpleracesModVariables.PlayerVariables());

        if (vars.elf && vars.forestSpirits > 0) {
            double dodgeChance = SimpleRPGRacesConfiguration.ELF_DODGE_CHANCE_PER_SPIRIT.get() * vars.forestSpirits;
            RandomSource random = player.getRandom();
            if (random.nextDouble() < dodgeChance) {
                event.setCanceled(true);

                for (int i = 0; i < SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get(); i++) {
                    if (vars.spiritCooldowns[i] == 0) {
                        vars.spiritCooldowns[i] = SimpleRPGRacesConfiguration.ELF_SPIRIT_COOLDOWN_SECONDS.get() * 20; // *20 для тиков
                        vars.forestSpirits = Math.max(0, vars.forestSpirits - 1);
                        break;
                    }
                }

                player.level().playSound(null, player.blockPosition(), SimpleracesMod.ELF_DODGE.get(), SoundSource.PLAYERS, 1.0f, 1.0f); // Убедитесь, что ELF_DODGE существует
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 10, 0.5, 0.5, 0.5, 0.1); // Зеленые частицы или другие
                }

                vars.syncPlayerVariables(player);
            }
        }
    }

    public static void spawnFangs(Level level, double x, double y, double z, float yaw, Player owner) {
        if (level.isClientSide()) return;

        EvokerFangs fangs = new EvokerFangs(level, x, y, z, yaw, 0, owner);
        level.addFreshEntity(fangs);
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) return;

        player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(vars -> {
            if (!vars.halfdead) return;
            float reduced = event.getAmount() * SimpleRPGRacesConfiguration.HALFDEAD_HEAL_MULTIPLIER.get().floatValue();
            event.setAmount(reduced);
        });
    }

    @SubscribeEvent
    public static void onSerpentinEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;

        boolean serpentin = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY)
                .map(v -> v.serpentin)
                .orElse(false);
        if (!serpentin) return;

        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null) return;

        // чистим флаг независимо от beneficial/poison/wither — безопасно
        var key = ForgeRegistries.MOB_EFFECTS.getKey(inst.getEffect());
        if (key == null) return;

        String effectKey = "serpentin_shortened_" + key.toString();
        player.getPersistentData().remove(effectKey);
    }

    @SubscribeEvent
    public static void onSerpentinEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;

        boolean serpentin = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY)
                .map(v -> v.serpentin)
                .orElse(false);
        if (!serpentin) return;

        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null) return;

        var key = ForgeRegistries.MOB_EFFECTS.getKey(inst.getEffect());
        if (key == null) return;

        String effectKey = "serpentin_shortened_" + key.toString();
        player.getPersistentData().remove(effectKey);
    }

    @SubscribeEvent
    public static void onLivingHurtForBleed(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        // кто ударил
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        // атакующий — оборотень?
        var vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());
        if (!vars.werewolf) return;

        // цель
        LivingEntity target = event.getEntity();

        // шанс прока (у тебя 1/10)
        if (player.getRandom().nextInt(10) != 0) return;

        // считаем DOT = 15% от урона удара
        float bleedDot = event.getAmount() * 0.15f;

        // записываем в NBT цели (важно: до наложения эффекта или сразу после — не критично)
        target.getPersistentData().putFloat("simpleraces_bleed_dot", bleedDot);

        // накладываем эффект (длительность как у тебя в конфиге)
        target.addEffect(new MobEffectInstance(
                ModEffects.BLEEDING.get(),
                SimpleRPGRacesConfiguration.WEREWOLF_BLEEDING_DURATION.get(),
                0,
                true,
                true
        ));
    }

    private static void addHealthDirect(ServerPlayer p, float amount) {
        if (amount <= 0f) return;
        float hp = p.getHealth() + amount;
        float max = p.getMaxHealth();
        if (hp > max) hp = max;
        p.setHealth(hp);
    }


    @SubscribeEvent
    public static void onBleedRemoved(MobEffectEvent.Remove event) {
        if (event.getEntity().level().isClientSide()) return;
        var inst = event.getEffectInstance();
        if (inst == null) return;

        if (inst.getEffect() == ModEffects.BLEEDING.get()) {
            event.getEntity().getPersistentData().remove("simpleraces_bleed_dot");
        }
    }

    @SubscribeEvent
    public static void onBleedExpired(MobEffectEvent.Expired event) {
        if (event.getEntity().level().isClientSide()) return;
        var inst = event.getEffectInstance();
        if (inst == null) return;

        if (inst.getEffect() == ModEffects.BLEEDING.get()) {
            event.getEntity().getPersistentData().remove("simpleraces_bleed_dot");
        }
    }


    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) return;

        killer.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(vars -> {
            if (!vars.halfdead) return;

            // 10% от maxHP жертвы, ИГНОРИРУЕТ штрафы лечения
            float killHeal = event.getEntity().getMaxHealth() * 0.10f;
            addHealthDirect(killer, killHeal);

            Level level = killer.level();
            double radius = SimpleRPGRacesConfiguration.HALFDEAD_DEATH_MARK_RADIUS.get();

            List<LivingEntity> nearby = level.getEntitiesOfClass(
                    LivingEntity.class,
                    event.getEntity().getBoundingBox().inflate(radius),
                    e -> e.isAlive() && e != event.getEntity() && e != killer
            );

            if (!nearby.isEmpty()) {
                LivingEntity target = nearby.get(0);
                target.addEffect(new MobEffectInstance(
                        ModEffects.DEATH_MARK.get(),
                        SimpleRPGRacesConfiguration.HALFDEAD_DEATH_MARK_DURATION.get()
                ));
                level.playSound(null, target.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1f);
            }
        });
    }



    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player && (player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)) {
            player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
                if (data.isOverheated() && event.getSource().getEntity() instanceof LivingEntity attacker) {
                    attacker.setSecondsOnFire(4);
                }
                applyHeat(player);
            });
        }
        if (event.getSource().getEntity() instanceof LivingEntity living && living.hasEffect(ModEffects.DEATH_MARK.get())) {
            event.setAmount((float) (event.getAmount() * SimpleRPGRacesConfiguration.HALFDEAD_DAMAGE_REDUCTION_FROM_MARK.get()));
        }
        if (event.getEntity().hasEffect(ModEffects.DEATH_MARK.get())) {
            event.setAmount((float) (event.getAmount() * SimpleRPGRacesConfiguration.HALFDEAD_DAMAGE_INCREASE_TO_MARKED.get()));
        }
        if (event.getEntity().getType().getCategory().equals(MobCategory.MONSTER) && event.getSource().getEntity() instanceof Player player &&
                event.getEntity().getMobType().equals(MobType.UNDEAD)) {
            player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(vars -> {
                if (!vars.halfdead) return;
                float original = event.getAmount();
                event.setAmount((float) (original * SimpleRPGRacesConfiguration.HALFDEAD_DAMAGE_BONUS_VS_UNDEAD.get()));
            });
        }
    }

    private static void applyHeat(Player player) {
        player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
            if (data.isOverheated()) return;

            data.setHeat(data.getHeat() + SimpleRPGRacesConfiguration.DRAKONID_HEAT_PER_ATTACK.get());
            if (data.getHeat() >= SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get()) {
                data.setHeat(SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get());
                data.setOverheated(true);
                data.setOverheatTicks(SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_TIME.get() * 20);

                player.level().playSound(null, player.blockPosition(), SimpleracesMod.DRAGON_OVERHEAT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                int maxHeat = SimpleRPGRacesConfiguration.DRAKONID_MAX_HEAT.get();
                int maxOverheatTicks = SimpleRPGRacesConfiguration.DRAKONID_OVERHEAT_TIME.get() * 20;

                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new SyncHeatPacket(
                                player.getUUID(),
                                data.getHeat(),
                                maxHeat,
                                data.isOverheated(),
                                data.getOverheatTicks(),
                                maxOverheatTicks
                        ));

            }
        });
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(SimpleracesModVariables.HEAT).ifPresent(oldStore -> {
            event.getEntity().getCapability(SimpleracesModVariables.HEAT).ifPresent(newStore -> {
                newStore.setHeat(oldStore.getHeat());
                newStore.setOverheated(oldStore.isOverheated());
                newStore.setOverheatTicks(oldStore.getOverheatTicks());
            });
        });
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if ((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)) {
            Vec3 velocity = player.getDeltaMovement();
            player.setDeltaMovement(velocity.x, 0.57, velocity.z);
        }
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        execute(null, world, x, y, z, entity);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf) {
            if (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) && y < 0) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20, 0, false, false));
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, (int) (double) SimpleRPGRacesConfiguration.DWARF_RES_LEVEL.get(), false, false));
            if (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) && y < 0 && SimpleRPGRacesConfiguration.DWARF_SUBZERO_EFFECTS.get()) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1, false, false));
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, (int) (double) SimpleRPGRacesConfiguration.DWARF_RES_LEVEL.get(), false, false));
        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).orc) {

        } else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dragon && SimpleRPGRacesConfiguration.DRAKONID_ARMOR_MELT.get()) {
            if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:melt")))) {
                {
                    Entity _entity = entity;
                    if (_entity instanceof Player _player) {
                        _player.getInventory().armor.set(0, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                        _player.getInventory().setChanged();
                    } else if (_entity instanceof LivingEntity _living) {
                        _living.setItemSlot(EquipmentSlot.FEET, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                    }
                }
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1, false);
                    }
                }
                SimpleracesMod.queueServerWork(3, () -> {
                    {
                        Entity _entity = entity;
                        if (_entity instanceof Player _player) {
                            _player.getInventory().armor.set(0, new ItemStack(Blocks.AIR));
                            _player.getInventory().setChanged();
                        } else if (_entity instanceof LivingEntity _living) {
                            _living.setItemSlot(EquipmentSlot.FEET, new ItemStack(Blocks.AIR));
                        }
                    }
                });
            }
            if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:melt")))) {
                {
                    Entity _entity = entity;
                    if (_entity instanceof Player _player) {
                        _player.getInventory().armor.set(1, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                        _player.getInventory().setChanged();
                    } else if (_entity instanceof LivingEntity _living) {
                        _living.setItemSlot(EquipmentSlot.LEGS, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                    }
                }
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1, false);
                    }
                }
                SimpleracesMod.queueServerWork(3, () -> {
                    {
                        Entity _entity = entity;
                        if (_entity instanceof Player _player) {
                            _player.getInventory().armor.set(1, new ItemStack(Blocks.AIR));
                            _player.getInventory().setChanged();
                        } else if (_entity instanceof LivingEntity _living) {
                            _living.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Blocks.AIR));
                        }
                    }
                });
            }
            if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:melt")))) {
                {
                    Entity _entity = entity;
                    if (_entity instanceof Player _player) {
                        _player.getInventory().armor.set(2, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                        _player.getInventory().setChanged();
                    } else if (_entity instanceof LivingEntity _living) {
                        _living.setItemSlot(EquipmentSlot.CHEST, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                    }
                }
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1, false);
                    }
                }
                SimpleracesMod.queueServerWork(3, () -> {
                    {
                        Entity _entity = entity;
                        if (_entity instanceof Player _player) {
                            _player.getInventory().armor.set(2, new ItemStack(Blocks.AIR));
                            _player.getInventory().setChanged();
                        } else if (_entity instanceof LivingEntity _living) {
                            _living.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Blocks.AIR));
                        }
                    }
                });
            }
            if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("minecraft:melt")))) {
                {
                    Entity _entity = entity;
                    if (_entity instanceof Player _player) {
                        _player.getInventory().armor.set(3, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                        _player.getInventory().setChanged();
                    } else if (_entity instanceof LivingEntity _living) {
                        _living.setItemSlot(EquipmentSlot.HEAD, new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get()));
                    }
                }
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1);
                    } else {
                        _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.NEUTRAL, 1, 1, false);
                    }
                }
                SimpleracesMod.queueServerWork(3, () -> {
                    {
                        Entity _entity = entity;
                        if (_entity instanceof Player _player) {
                            _player.getInventory().armor.set(3, new ItemStack(Blocks.AIR));
                            _player.getInventory().setChanged();
                        } else if (_entity instanceof LivingEntity _living) {
                            _living.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.AIR));
                        }
                    }
                });
            }
            if (entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(SimpleracesModItems.ITEMPOPEFFECT.get())) : false) {
                SimpleracesMod.queueServerWork(3, () -> {
                    {
                        Entity _ent = entity;
                        if (!_ent.level().isClientSide() && _ent.getServer() != null) {
                            _ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
                                    _ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "/clear @s simpleraces:itempopeffect");
                        }
                    }
                });
            }
        }
        if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).elf) {
            ItemStack mainHand = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);

            if (entity instanceof LivingEntity living && mainHand.getItem() instanceof BowItem) {
                AttributeInstance dmgAttr = living.getAttribute(Attributes.ATTACK_DAMAGE);
                if (dmgAttr != null) {
                    dmgAttr.removeModifier(ELF_BOW_BOOST_UUID);
                    dmgAttr.addTransientModifier(new AttributeModifier(
                            ELF_BOW_BOOST_UUID,
                            "Elf bow damage boost",
                            SimpleRPGRacesConfiguration.ELF_BOW_BONUS_DAMAGE.get(),
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
            } else if (entity instanceof LivingEntity living) {
                AttributeInstance dmgAttr = living.getAttribute(Attributes.ATTACK_DAMAGE);
                if (dmgAttr != null) {
                    dmgAttr.removeModifier(ELF_BOW_BOOST_UUID);
                }
            }
        }
    }

    private static void adjustDebuffDurations(LivingEntity target) {
        if (target == null || !target.isAlive()) return;

        Map<MobEffect, Integer> pre = preAttackDebuffs.get(target.getUUID());
        if (pre == null) return;

        for (MobEffectInstance inst : new ArrayList<>(target.getActiveEffects())) {
            if (inst.getEffect().isBeneficial()) continue; // Только дебафы

            MobEffect effect = inst.getEffect();
            int preDuration = pre.getOrDefault(effect, 0);
            int currentDuration = inst.getDuration();

            if (currentDuration > preDuration) {
                int added = currentDuration - preDuration;
                int newAdded = added * 2; // Удваиваем добавленную часть
                int newDuration = preDuration + newAdded;

                target.removeEffect(effect);
                target.addEffect(new MobEffectInstance(effect, newDuration, inst.getAmplifier(),
                        inst.isAmbient(), inst.isVisible(), inst.showIcon()));
            }
        }
    }

    @SubscribeEvent
    public static void onArmorWeightCalculation(ArmorWeightCalculationEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());

        if (vars.aracha) {
            double multiplier = SimpleRPGRacesConfiguration.ARACHA_WEIGHT_MULTIPLIER.get();
            int modifiedWeight = (int) Math.round(event.getWeight() * multiplier);
            event.setWeight(modifiedWeight);
        }
    }
}

package net.simpleraces.procedures;

import net.minecraft.core.particles.*;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.heat.HeatProvider;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
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
import net.simpleraces.network.SyncHeatPacket;
import net.minecraftforge.common.ForgeMod;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ResistanceAddProcedure {
	// UUID-константы (где-то в начале класса)
	private static final UUID MERFOLK_LAND_DAMAGE_UUID   = UUID.fromString("e7e3c8a0-1d2b-4c9f-a1be-123456789abc");
	private static final UUID MERFOLK_WATER_DAMAGE_UUID  = UUID.fromString("d9f6b0c2-3e97-4a84-8f45-abcdef012345");
	private static final UUID WEREWOLF_HUMAN_DAMAGE_UUID = UUID.fromString("a3e1f9d4-5b2c-4e1a-9f77-112233445566");
	private static final UUID WEREWOLF_BEAST_DAMAGE_UUID = UUID.fromString("f2b4c6d8-7e9f-4a12-b3c4-556677889900");
	private static final UUID ELF_SPEED_UUID = UUID.fromString("f2b4c6d8-7e9f-4a12-b3c4-556677889901");

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
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "autumnal_valley")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "cherry_blossom_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "cika_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "ebony_woods")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "glowshroom_bayou")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "jacaranda_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "maple_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "mega_spruce_taiga")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "mega_coniferous_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "pine_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "redwood_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "seasonal_forest")),
        ResourceKey.create(Registries.BIOME, new ResourceLocation("byg", "snowy_fir_forest")),
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

		if (vars.merfolk) {
			AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
			AttributeInstance reachAttr  = player.getAttribute(ForgeMod.ENTITY_REACH.get());
			AttributeInstance dmgAttr    = player.getAttribute(Attributes.ATTACK_DAMAGE);

			if (player.isInWater()) {
				healthAttr.setBaseValue(20.0);
				if (player.getHealth() > 20.0f) player.setHealth(20.0f);

				if (reachAttr != null) {
					double defReach = ForgeMod.ENTITY_REACH.get().getDefaultValue();
					reachAttr.setBaseValue(defReach + 1.0);
				}

				if (dmgAttr != null) {
					dmgAttr.removeModifier(MERFOLK_LAND_DAMAGE_UUID);
					if (dmgAttr.getModifier(MERFOLK_WATER_DAMAGE_UUID) == null) {
						dmgAttr.addTransientModifier(new AttributeModifier(
								MERFOLK_WATER_DAMAGE_UUID,
								"Merfolk water damage bonus",
								0.1,
								AttributeModifier.Operation.MULTIPLY_TOTAL
						));
					}
				}

				player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 0, false, false));
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,   40, 0, false, false));
				player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,   20, 0, false, false));

			} else {
				healthAttr.setBaseValue(10.0);
				if (player.getHealth() > 10.0f) player.setHealth(10.0f);

				if (reachAttr != null) {
					double defReach = ForgeMod.ENTITY_REACH.get().getDefaultValue();
					reachAttr.setBaseValue(defReach);
				}

				if (dmgAttr != null) {
					dmgAttr.removeModifier(MERFOLK_WATER_DAMAGE_UUID);
					if (dmgAttr.getModifier(MERFOLK_LAND_DAMAGE_UUID) == null) {
						dmgAttr.addTransientModifier(new AttributeModifier(
								MERFOLK_LAND_DAMAGE_UUID,
								"Merfolk land damage penalty",
								-0.35,
								AttributeModifier.Operation.MULTIPLY_TOTAL
						));
					}
				}

				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
			}

		} else if (vars.dragon) {
			if (player.isInWater()) {
				player.hurt(player.damageSources().fellOutOfWorld(), 2.0F);
			}
			player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
				ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
						new SyncHeatPacket(player.getUUID(), data.getHeat(), data.isOverheated()));
				if (data.isOverheated()) {
					int ticks = data.getOverheatTicks() - 1;
					data.setOverheatTicks(ticks);
					if (player.tickCount % 60 == 0) {
						player.hurt(player.damageSources().fellOutOfWorld(), 1);
					}
					if (ticks % 100 == 0) {
						player.hurt(player.damageSources().onFire(), 1.0F);
					}
					if (ticks <= 0) {
						data.setOverheated(false);
						data.setHeat(0);
						player.sendSystemMessage(Component.literal("§eВы остынули."));
					}
					player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2, 0, true, false));
					BlockPos pos = player.blockPosition().below();
					if (level.getBlockState(pos).isAir() &&
							Blocks.FIRE.defaultBlockState().canSurvive(level, pos)) {
						level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
					}
				}
			});

		} else if (vars.fairy) {
			CompoundTag data = player.getPersistentData();

			int flightTime = data.getInt("fairy_flight_ticks");
			if(player.getAbilities().flying) {
                flightTime++;
            }
			data.putInt("fairy_flight_ticks", flightTime);
			if(flightTime > 10 * 20){
				player.getAbilities().mayfly = false;
				player.getAbilities().flying = false;
				player.onUpdateAbilities();
				if(!data.getBoolean("falled") && player.getY() < player.yo){
					if(player.onGround()){
						data.putBoolean("falled", true);
					} else {
						player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 0, false, true));
					}
				}
			} else{
				player.getAbilities().mayfly = true;
				data.putBoolean("falled", false);
				player.getAbilities().setFlyingSpeed(0.025f);
				player.onUpdateAbilities();
			}

			((ServerLevel) player.level()).sendParticles(
					new DustColorTransitionOptions(
							new Vector3f(1f, 1f, 1f),
							new Vector3f(0.8f, 0.8f, 0.8f),
							1.4f
					),
					player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0
			);

		} else if (vars.aracha) {
			if (player.isInWater()) {
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 2, false, false));
			}

		} else if (vars.werewolf) {
			AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);

			if (WerewolfState.isBeast(player)) {
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,   2, 0, false, true));
				player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,      2, 0, false, true));
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0, false, true));

				if (dmgAttr != null) {
					dmgAttr.removeModifier(WEREWOLF_HUMAN_DAMAGE_UUID);
					if (dmgAttr.getModifier(WEREWOLF_BEAST_DAMAGE_UUID) == null) {
						dmgAttr.addTransientModifier(new AttributeModifier(
								WEREWOLF_BEAST_DAMAGE_UUID,
								"Werewolf beast damage bonus",
								0.15,
								AttributeModifier.Operation.MULTIPLY_TOTAL
						));
					}
				}

			} else {
				if (dmgAttr != null) {
					dmgAttr.removeModifier(WEREWOLF_BEAST_DAMAGE_UUID);
					if (dmgAttr.getModifier(WEREWOLF_HUMAN_DAMAGE_UUID) == null) {
						dmgAttr.addTransientModifier(new AttributeModifier(
								WEREWOLF_HUMAN_DAMAGE_UUID,
								"Werewolf human damage penalty",
								-0.2,
								AttributeModifier.Operation.MULTIPLY_TOTAL
						));
					}
				}

				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, true));
			}
		} else if(vars.elf){
			if(level.getBrightness(LightLayer.SKY, player.getOnPos().above(2)) > 0 && FOREST_BIOMES.contains(level.getBiome(player.getOnPos()).unwrapKey().get())){
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 5, 0, false, true));
				AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
				if (speedAttr != null && speedAttr.getModifier(ELF_SPEED_UUID) == null) {
					speedAttr.addTransientModifier(new AttributeModifier(
							ELF_SPEED_UUID,
							"Elf speed boost",
							0.1,
							AttributeModifier.Operation.MULTIPLY_BASE
					));
				}
			} else {
				AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
				if (speedAttr != null) {
					speedAttr.removeModifier(ELF_SPEED_UUID);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
		Player player = event.getEntity();
		SimpleracesModVariables.PlayerVariables vars = player.getCapability(
				SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
		).orElse(new SimpleracesModVariables.PlayerVariables());

		AttributeDeathFixProcedure.execute(player);
		vars.selected = false;
		if (vars.fairy) {
			SelectedFairyProcedure.execute(player);
		} else if (vars.aracha) {
			SelectedArachaProcedure.execute(player);
		} else if (vars.dragon) {
			SelectedDragonProcedure.execute(player);
		} else if (vars.halfdead) {
			SelectedHalfdeadProcedure.execute(player);
		} else if (vars.werewolf) {
			SelectedWerewolfProcedure.execute(player);
		} else if (vars.dwarf) {
			SelectedDwarfProcedure.execute(player);
		} else if (vars.elf) {
			SelectedElfProcedure.execute(player);
		} else if (vars.orc) {
			SelectedOrcProcedure.execute(player);
		} else if (vars.merfolk) {
			SelectedMerfolkProcedure.execute(player);
		} else if (vars.serpentin) {
			SelectedSerpentinProcedure.execute(player);
		}
	}

	@SubscribeEvent
	public static void onLivingFall(LivingFallEvent event) {
		LivingEntity entity = event.getEntity();
		SimpleracesModVariables.PlayerVariables vars = entity.getCapability(
				SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null
		).orElse(new SimpleracesModVariables.PlayerVariables());
		if (vars.fairy) {
			event.setDamageMultiplier(0.75f);
		} else if(vars.dragon){
			event.setDamageMultiplier(0.85f);
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
		if(!entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(vars -> vars.serpentin).orElse(false)){
			return;
		}

        if (!(stack.getItem() instanceof PotionItem)) return;

        List<MobEffectInstance> originalEffects = PotionUtils.getMobEffects(stack);

        for (MobEffectInstance effect : originalEffects) {
            MobEffect type = effect.getEffect();
            int amplifier = effect.getAmplifier();
            int duration = effect.getDuration();

            duration = (int) (duration * 1.5f);

            if (type == MobEffects.DAMAGE_BOOST || type == MobEffects.MOVEMENT_SPEED) {
                amplifier += 1;
            } else if (type == MobEffects.JUMP) {
                amplifier += 2;
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
		if ((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(playerVariables -> playerVariables.serpentin)
				.orElse(false))){
			if (entity.hasEffect(MobEffects.POISON)) {
				entity.removeEffect(MobEffects.POISON);
			}
			if (entity.hasEffect(MobEffects.WITHER)) {
				entity.removeEffect(MobEffects.WITHER);
			}
		} else if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(playerVariables -> playerVariables.aracha)
				.orElse(false))) {

			boolean hasHeavyArmor = isWearingHeavyArmor(player);

			if (hasHeavyArmor) {
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3, 0, true, false, false));
			}
		} else if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(playerVariables -> playerVariables.werewolf)
				.orElse(false))){
			if(player.level().isNight() && WerewolfState.isHuman(player)){
				WerewolfState.transformToBeast(player);
			} else if(!player.level().isNight() && WerewolfState.isBeast(player)){
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
				.orElse(new SimpleracesModVariables.PlayerVariables()).serpentin))){
			boolean hasDebuff = false;

			for (MobEffectInstance effect : target.getActiveEffects()) {
				if (effect.getEffect().isBeneficial()) continue;
				hasDebuff = true;
				break;
			}
			float damage = event.getAmount();
			if (hasDebuff) {
				damage *= 1.25f;
			} else {
				damage *= 0.65f;
			}

			event.setAmount(damage);
		} else if(event.getEntity() instanceof Player && ((Player) event.getEntity()).getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new SimpleracesModVariables.PlayerVariables()).aracha) {
			DamageSource source = event.getSource();

			if (source.typeHolder().is(DamageTypes.IN_FIRE)) {
				event.setAmount(event.getAmount() * 1.5f);
			}
		} else if(event.getEntity() instanceof Player player && player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new SimpleracesModVariables.PlayerVariables()).werewolf){
			if (player.getRandom().nextInt(20) == 0) {
                    player.addEffect(new MobEffectInstance(ModEffects.WEREWOLF_TRANSFORMATION.get(), 1200));
                }
		} else if(event.getEntity() instanceof Player player && player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new SimpleracesModVariables.PlayerVariables()).dragon){
			player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
				if(!data.isOverheated()){
					data.setHeat(data.getHeat() + 2);
				}
			});
		}
    }

	@SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;
		if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).aracha)) {

			CompoundTag persistentData = player.getPersistentData();
			CompoundTag data;
			if (!persistentData.contains(Player.PERSISTED_NBT_TAG)) {
				data = new CompoundTag();
				persistentData.put(Player.PERSISTED_NBT_TAG, data);
			} else {
				data = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
			}

			int count = data.getInt("bite_count") + 1;

			if (count >= 3) {
				data.putInt("bite_count", 0);
//            target.hurt(player.damageSources().magic(), 4.0f);
				spawnFangs(player.level(), target.getX(), target.getY(), target.getZ(), player.getYRot(), player);
				target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));

				target.level().playSound(null, target.blockPosition(), SoundEvents.BEE_STING, SoundSource.PLAYERS, 1.0f, 1.0f);

				if (target.level() instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
				}

			} else {
				data.putInt("bite_count", count);
			}
		} else if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).werewolf)) {
			if(player.getRandom().nextInt(10) == 0) {
				 target.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 100, 0, true, true));
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

			float reduced = event.getAmount() * 0.2F;
			event.setAmount(reduced);
		});
	}

	@SubscribeEvent
	public static void onMobKilled(LivingDeathEvent event) {
		if (!(event.getSource().getEntity() instanceof Player killer)) return;

		killer.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(vars -> {
			if (!vars.halfdead) return;
			killer.heal(20.0F);
			Level level = killer.level();
			EntityType<?> killedType = event.getEntity().getType();

			List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, event.getEntity().getBoundingBox().inflate(10), e ->
				e.getType() == killedType && e != event.getEntity());

			if (!nearby.isEmpty()) {
				LivingEntity target = nearby.get(0);
				target.addEffect(new MobEffectInstance(ModEffects.DEATH_MARK.get(), 200));

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
		Entity attacker = event.getSource().getEntity();
		if (attacker instanceof LivingEntity living && living.hasEffect(ModEffects.DEATH_MARK.get())) {
			event.setAmount(event.getAmount() * 0.67F);
		}
		if (event.getEntity().hasEffect(ModEffects.DEATH_MARK.get())) {
			event.setAmount(event.getAmount() * 1.33F);
		}
		if (event.getEntity().getType().getCategory().equals(MobCategory.MONSTER) && event.getSource().getEntity() instanceof Player player &&
			event.getEntity().getMobType().equals(MobType.UNDEAD)) {
			player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(vars -> {
				if (!vars.halfdead) return;
				float original = event.getAmount();
				event.setAmount(original * 1.2f);
			});
		}
	}

	private static void applyHeat(Player player) {
		player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
			if (data.isOverheated()) return;

			data.setHeat(data.getHeat() + 4);
			if (data.getHeat() >= 100) {
				data.setHeat(100);
				data.setOverheated(true);
				data.setOverheatTicks(30 * 20);

				player.sendSystemMessage(Component.literal("§cВы перегрелись!"));
			}
		});
	}

	@SubscribeEvent
	public static void onHit(LivingAttackEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) return;
		if(!(player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)) return;
		player.getCapability(SimpleracesModVariables.HEAT).ifPresent(data -> {
			if (data.isOverheated()) {
				event.getEntity().setSecondsOnFire(5);
			}
		});
	}
	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			event.addCapability(new ResourceLocation(SimpleracesMod.MODID, "heat"), new HeatProvider());
		}
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
		if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)) {
			Vec3 velocity = player.getDeltaMovement();
			player.setDeltaMovement(velocity.x, 0.60, velocity.z);
		}
    }

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf) {
			if (((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getDisplayName().getString()).contains("Pickaxe")) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, (int) (double) SimpleRPGRacesConfiguration.DWARF_PICKAXE_STRENGTH.get(), false, false));
			}
			if (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) && y < 0 && SimpleRPGRacesConfiguration.DWARF_SUBZERO_EFFECTS.get()) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1, false, false));
			}
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, (int) (double) SimpleRPGRacesConfiguration.DWARF_RES_LEVEL.get(), false, false));
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).orc) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= 10 && SimpleRPGRacesConfiguration.ORC_MINI_RAGE.get()) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 0, false, false));
			}
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
		if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).elf
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getDisplayName().getString()).contains("Pickaxe") && SimpleRPGRacesConfiguration.ELF_PICKAXE_RESTRICT.get()) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 0, false, false));
		}
	}
}

package net.simpleraces.procedures;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ClassAbilitiesProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf && !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))
				&& SimpleRPGRacesConfiguration.DWARF_HASTE.get()) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()){
				_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 0, false, false));
				if(_entity.getY() <= 100) {
                    _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, false, false));
                }
			}
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).merfolk && entity.isInWater() && SimpleRPGRacesConfiguration.MERFOLK_CONDUIT_EFFECT.get()) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 20, 1, false, false));
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dragon && SimpleRPGRacesConfiguration.DRAK_FIRE_RES.get()) {
		}
		if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getDisplayName().getString()).contains("Bow") && SimpleRPGRacesConfiguration.DWARF_BOW_RESTRICT.get()) {
		} else if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dwarf
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getDisplayName().getString()).contains("Bow") && SimpleRPGRacesConfiguration.DWARF_BOW_RESTRICT.get()) {
		}
		if ((entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).dragon && entity.isInWater() && SimpleRPGRacesConfiguration.DRAKONID_WATER_HURT.get()) {
			if (entity instanceof LivingEntity _entity) {
				DamageSource _dmgsource = new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC));
				_entity.hurt(new DamageSource(_dmgsource.typeHolder(), _dmgsource.getEntity(), _dmgsource.getDirectEntity()) {
					@Override
					public Component getLocalizedDeathMessage(LivingEntity _msgEntity) {
						String _translatekey = "death.water.attack";
						if (this.getEntity() == null && this.getDirectEntity() == null) {
							return _msgEntity.getKillCredit() != null
									? Component.translatable(_translatekey + ".player", _msgEntity.getDisplayName(), _msgEntity.getKillCredit().getDisplayName())
									: Component.translatable(_translatekey, _msgEntity.getDisplayName());
						} else {
							Component _component = this.getEntity() == null ? this.getDirectEntity().getDisplayName() : this.getEntity().getDisplayName();
							ItemStack _itemstack = ItemStack.EMPTY;
							if (this.getEntity() instanceof LivingEntity _livingentity)
								_itemstack = _livingentity.getMainHandItem();
							return !_itemstack.isEmpty() && _itemstack.hasCustomHoverName()
									? Component.translatable(_translatekey + ".item", _msgEntity.getDisplayName(), _component, _itemstack.getDisplayName())
									: Component.translatable(_translatekey, _msgEntity.getDisplayName(), _component);
						}
					}
				}, 1);
			}
		}
	}
}

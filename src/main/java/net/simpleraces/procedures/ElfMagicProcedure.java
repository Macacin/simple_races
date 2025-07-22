package net.simpleraces.procedures;

import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ElfMagicProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getDirectEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, Entity entity, Entity immediatesourceentity, Entity sourceentity) {
		execute(null, world, entity, immediatesourceentity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity immediatesourceentity, Entity sourceentity) {
		if (entity == null || immediatesourceentity == null || sourceentity == null)
			return;
		if ((sourceentity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).elf
				&& (immediatesourceentity instanceof SpectralArrow || immediatesourceentity instanceof Arrow)) {
			if (entity instanceof LivingEntity _entity) {
				DamageSource _dmgsource = new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC));
				_entity.hurt(new DamageSource(_dmgsource.typeHolder(), _dmgsource.getEntity(), _dmgsource.getDirectEntity()) {
					@Override
					public Component getLocalizedDeathMessage(LivingEntity _msgEntity) {
						String _translatekey = "death.attack.elfmagic";
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
				}, (float) (double) SimpleRPGRacesConfiguration.ELF_BOW_BONUS_DAMAGE.get());
			}
		}
	}
}

package net.simpleraces.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.simpleraces.network.SimpleracesModVariables;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalInt;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    public PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean onClimbable() {
        var vars = ((Player)(Object) this).getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());
        if (this.horizontalCollision && this.isAlive() && vars.aracha) {
            return true;
        }
        return super.onClimbable();
    }

    @Override
    public boolean addEffect(MobEffectInstance oldInstance, @Nullable Entity entity) {
        if(entity == null){
            return super.addEffect(oldInstance, entity);
        }
        if(entity.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY).map(vars -> vars.serpentin).orElse(false) && !oldInstance.getEffect().isBeneficial()) {
            MobEffectInstance instance = new MobEffectInstance(oldInstance.getEffect(), oldInstance.getDuration() / 2, oldInstance.getAmplifier(), oldInstance.isAmbient(), oldInstance.isVisible(), oldInstance.showIcon());
            return super.addEffect(instance, entity);
        } else {
            return super.addEffect(oldInstance, entity);
        }
    }
}
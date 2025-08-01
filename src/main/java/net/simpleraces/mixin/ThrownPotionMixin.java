package net.simpleraces.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.simpleraces.network.SimpleracesModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin extends Projectile {

    protected ThrownPotionMixin() {
        super(null, null);
    }

    // Inject в начало метода onHit для проверки серпентина и удвоения эффектов
    @Inject(method = "onHit", at = @At("HEAD"))
    private void onHitInject(net.minecraft.world.phys.HitResult hitResult, CallbackInfo ci) {
        ThrownPotion potion = (ThrownPotion) (Object) this;
        if (!(potion.getOwner() instanceof Player player)) return;

        SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());

        if (!vars.serpentin) return;  // Только для серпентина

        // Получаем и удваиваем эффекты в зелье (это повлияет на applySplash и makeAreaOfEffectCloud)
        List<MobEffectInstance> effects = PotionUtils.getMobEffects(potion.getItem());
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance eff = effects.get(i);
            effects.set(i, new MobEffectInstance(eff.getEffect(), eff.getDuration() * 2, eff.getAmplifier(), eff.isAmbient(), eff.isVisible(), eff.showIcon()));
        }
        // Обновляем itemstack зелья с новыми эффектами
        PotionUtils.setCustomEffects(potion.getItem(), effects);
    }
}
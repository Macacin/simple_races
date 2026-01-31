package net.simpleraces.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.simpleraces.network.SimpleracesModVariables;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @SuppressWarnings("DataFlowIssue")
    protected LivingEntityMixin() {
        // так же, как в skilltree: mixin-ctor с null'ами
        // потому что Mixin сам "подклеится" к настоящему конструктору
        // и IDE не будет требовать реальный вызов super(...)
    }

    @Inject(method = "onClimbable()Z", at = @At("HEAD"), cancellable = true)
    private void simpleraces$arachaClimb(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;

        var vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());

        if (vars.aracha && self.isAlive() && self.horizontalCollision) {
            cir.setReturnValue(true);
        }
    }
}

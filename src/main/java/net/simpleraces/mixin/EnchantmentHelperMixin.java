package net.simpleraces.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.simpleraces.network.SimpleracesModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchantmentHelper.class, priority = 1001)
public class EnchantmentHelperMixin {
    @Inject(method = "hasAquaAffinity", at = @At("HEAD"), cancellable = true)
    private static void hasAquaAffinityMixin(LivingEntity p_44935_, CallbackInfoReturnable<Boolean> cir) {
        if(p_44935_ instanceof Player player && (player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables())).merfolk){
            cir.setReturnValue(true);
        }
    }
}

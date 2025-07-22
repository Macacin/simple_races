package net.simpleraces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.simpleraces.network.SimpleracesModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WebBlock.class)
public class WebBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void cancelWebSlowdown(BlockState p_58180_, Level p_58181_, BlockPos p_58182_, Entity p_58183_, CallbackInfo ci) {
            var vars = p_58183_.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                             .orElse(new SimpleracesModVariables.PlayerVariables());
        if (vars.aracha) {
            ci.cancel();
        }
    }
}
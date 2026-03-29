package net.simpleraces.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.simpleraces.data.WerewolfForbiddenItems;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.SimpleracesModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class WerewolfEatRestrictionMixin {

    @Inject(
            method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onEat(Level level, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayer player)) return;

        boolean isWerewolf = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables())
                .werewolf;

        if (!isWerewolf || !WerewolfState.isBeast(player)) return;

        var itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return;

        if (WerewolfForbiddenItems.ITEMS.contains(itemId)) {
            // Блокируем фактическое поедание, но не сбрасываем использование предмета.
            cir.setReturnValue(stack);
            cir.cancel();
        }
    }
}

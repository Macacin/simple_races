package net.simpleraces.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.simpleraces.data.WerewolfForbiddenItems;
import net.simpleraces.entity.WerewolfState;
import net.simpleraces.network.SimpleracesModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class WerewolfStartUsingMixin {

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStartUsing(InteractionHand hand, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getFoodProperties(player) == null) return;

        boolean isWerewolf = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables())
                .werewolf;

        if (!isWerewolf || !WerewolfState.isBeast(player)) return;

        var itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return;

        if (WerewolfForbiddenItems.ITEMS.contains(itemId)) {
            // В звероформе не отменяем старт использования:
            // игрок должен бесконечно пытаться есть запрещенную еду.
        }
    }
}

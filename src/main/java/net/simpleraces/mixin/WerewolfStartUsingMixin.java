package net.simpleraces.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.simpleraces.data.WerewolfForbiddenItems;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.StopEatingPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class WerewolfStartUsingMixin {

    private static final String MSG_TIMER = "sr_badfood_timer";

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

        if (!isWerewolf) return;

        var itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return;

        if (WerewolfForbiddenItems.ITEMS.contains(itemId)) {
            // СНАЧАЛА отправляем сообщение и пакет, ПОТОМ отменяем
            sendBlockedMessage(player);

            // Отправляем пакет клиенту для сброса анимации
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new StopEatingPacket()
            );

            // Отменяем использование в последнюю очередь
            ci.cancel();
        }
    }

    private void sendBlockedMessage(ServerPlayer player) {
        net.minecraft.nbt.CompoundTag data = player.getPersistentData();
        int timer = data.getInt(MSG_TIMER);

        if (timer <= 0) {
            // Используем sendSystemMessage вместо displayClientMessage для гарантии
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("message.simpleraces.werewolf.bad_food")
                            .withStyle(net.minecraft.ChatFormatting.RED),
                    true // overlay (action bar)
            );
            data.putInt(MSG_TIMER, 20); // 1 секунда
        } else {
            data.putInt(MSG_TIMER, timer - 1);
        }
    }
}
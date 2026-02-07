package net.simpleraces.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.simpleraces.data.WerewolfForbiddenItems;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.StopEatingPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class WerewolfEatRestrictionMixin {

    // Таймер для спама сообщений (каждые 20 тиков = 1 секунда)
    private static final String MSG_TIMER = "sr_badfood_msg_timer";

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

        if (!isWerewolf) return;

        var itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return;

        if (WerewolfForbiddenItems.ITEMS.contains(itemId)) {
            // ОТМЕНЯЕМ МГНОВЕННО
            cir.setReturnValue(stack);

            player.stopUsingItem();
            player.releaseUsingItem();

            // Отправляем пакет клиенту для сброса анимации
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new StopEatingPacket()
            );

            // Спам сообщения каждую секунду
            net.minecraft.nbt.CompoundTag data = player.getPersistentData();
            int timer = data.getInt(MSG_TIMER);

            if (timer <= 0) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("message.simpleraces.werewolf.bad_food")
                                .withStyle(net.minecraft.ChatFormatting.RED),
                        true
                );
                data.putInt(MSG_TIMER, 20); // 20 тиков = 1 секунда
            } else {
                data.putInt(MSG_TIMER, timer - 1);
            }
        }
    }
}
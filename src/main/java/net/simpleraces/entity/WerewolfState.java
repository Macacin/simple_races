package net.simpleraces.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.effect.ModEffects;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SyncWerewolfPacket;

public class WerewolfState {

    private static final String NBT_KEY = "WerewolfForm";

    public enum Form {
        HUMAN,
        BEAST
    }

    public static void setForm(Player player, Form form) {
        CompoundTag tag = player.getPersistentData();
        tag.putString(NBT_KEY, form.name());
        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player),
                new SyncWerewolfPacket(form == Form.BEAST));
    }

    public static Form getForm(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains(NBT_KEY)) {
            try {
                return Form.valueOf(tag.getString(NBT_KEY));
            } catch (IllegalArgumentException e) {
                return Form.HUMAN;
            }
        }
        return Form.HUMAN;
    }

    public static boolean isBeast(Player player) {
        return getForm(player) == Form.BEAST;
    }

    public static boolean isHuman(Player player) {
        return getForm(player) == Form.HUMAN;
    }
    public static void transformToBeast(Player player) {
        WerewolfState.setForm(player, WerewolfState.Form.BEAST);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOLF_HOWL, player.getSoundSource(), 2.0F, 2.0F);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 4));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 128));
    }

    public static void transformToHuman(Player player) {
        if(player.hasEffect(ModEffects.WEREWOLF_TRANSFORMATION.get())) return;
        WerewolfState.setForm(player, WerewolfState.Form.HUMAN);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
    }
}
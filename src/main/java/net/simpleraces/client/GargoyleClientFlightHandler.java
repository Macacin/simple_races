package net.simpleraces.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.simpleraces.network.ModMessages;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.network.StartGargoyleGlidePacket;

@EventBusSubscriber(modid = "simpleraces", value = Dist.CLIENT)
public class GargoyleClientFlightHandler {
    private static final float FOV_TRANSITION_SPEED = 0.18f;
    private static boolean jumpWasDown;
    private static int clientTicks;
    private static int lastJumpPressTick = -100;
    private static boolean glideActive;
    private static float currentFovScale = 1.0f;

    private GargoyleClientFlightHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        clientTicks++;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options == null) {
            jumpWasDown = false;
            glideActive = false;
            currentFovScale = approach(currentFovScale, 1.0f, FOV_TRANSITION_SPEED);
            return;
        }

        boolean jumpDown = mc.options.keyJump.isDown();
        boolean justPressed = jumpDown && !jumpWasDown;
        jumpWasDown = jumpDown;

        SimpleracesModVariables.PlayerVariables vars = SimpleracesModVariables.getPlayerVariables(player);
        boolean hasCreativeFlight = player.getAbilities().instabuild || player.isSpectator();

        boolean canSpirewingGlide = vars.gargoyle
                && SyncVars.gargoyleStance == 3
                && !hasCreativeFlight
                && !player.isInWater()
                && !player.isPassenger();
        float targetFovScale = getTargetFovScale(vars.gargoyle, SyncVars.gargoyleStance);
        currentFovScale = approach(currentFovScale, targetFovScale, FOV_TRANSITION_SPEED);

        if (hasCreativeFlight) {
            glideActive = false;
        } else if (!canSpirewingGlide || player.onGround()) {
            glideActive = false;
            if (player.onGround() && player.isFallFlying()) {
                player.stopFallFlying();
            }
        } else if (glideActive) {
            player.startFallFlying();
        }

        if (!justPressed) {
            return;
        }

        if (!canSpirewingGlide || player.onGround() || player.isFallFlying()) {
            lastJumpPressTick = clientTicks;
            return;
        }

        if (clientTicks - lastJumpPressTick <= 7) {
            glideActive = true;
            player.startFallFlying();
            ModMessages.INSTANCE.sendToServer(new StartGargoyleGlidePacket());
        }

        lastJumpPressTick = clientTicks;
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        if (Math.abs(currentFovScale - 1.0f) < 0.001f) {
            return;
        }

        event.setFOV(event.getFOV() * currentFovScale);
    }

    private static float getTargetFovScale(boolean gargoyle, int stance) {
        if (!gargoyle) {
            return 1.0f;
        }
        return switch (stance) {
            case 1 -> 0.80f;
            case 3 -> 1.18f;
            default -> 1.0f;
        };
    }

    private static float approach(float current, float target, float speed) {
        return current + (target - current) * speed;
    }
}






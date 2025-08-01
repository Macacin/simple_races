package net.simpleraces.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.simpleraces.client.SyncVars;
import net.simpleraces.network.SimpleracesModVariables;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "simpleraces", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OverlayRenderer {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        SimpleracesModVariables.PlayerVariables vars = player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables());
        if (player == null) return;
        if (vars.dragon) {
            GuiGraphics gui = event.getGuiGraphics();

            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();

            int hotbarHeight = 22;
            int offsetY = 30;

            int maxWidth = 99;
            int x = (width - maxWidth) / 2;
            int y = height - hotbarHeight - 18 - offsetY;

            float procents = SyncVars.heat / (float) SyncVars.maxHeat;
            int barWidth = (int) (procents * maxWidth);
            int backgroundV = SyncVars.overheated ? 46 : 0;
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x, y, 0, backgroundV, maxWidth, 19);
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x, y + 10, 0, 25, barWidth, 5);
            if (!SyncVars.overheated) {
                if (procents <= 0.3f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x + maxWidth / 2 - 6, y, 101, 3, 10, 14);
                } else if (procents <= 0.6f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x + maxWidth / 2 - 6, y, 101, 19, 10, 14);
                } else if (procents <= 0.8f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x + maxWidth / 2 - 6, y, 118, 3, 10, 14);
                } else if (procents <= 1.0f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x + maxWidth / 2 - 6, y, 118, 19, 10, 14);
                }
            } else {
                gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x + maxWidth / 2 - 6, y, 134, 20, 10, 14);
            }

        } else if (vars.fairy) {
            GuiGraphics gui = event.getGuiGraphics();

            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();

            int hotbarHeight = 22;
            int offsetY = 30;

            int maxWidth = 99;
            int x = (width - maxWidth) / 2;
            int y = height - hotbarHeight - 18 - offsetY;

            float procents;
            if (SyncVars.isFairyRecovering) {
                procents = SyncVars.fairyFlightBar / (float) SyncVars.maxFairyFlight;
            } else {
                procents = 1f - (SyncVars.fairyFlightBar / (float) SyncVars.maxFairyFlight);
            }

            int barWidth = (int) (procents * maxWidth);
            int backgroundV = SyncVars.isFairyRecovering ? 0 : 43;

            gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), x, y, 0, backgroundV, maxWidth, 19);
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), x, y + 10, 0, 25, barWidth, 5);
            int iconWidth = 13;
            int iconX = x + (maxWidth - iconWidth) / 2;
            int iconY = y + 2;

            if (!SyncVars.isFairyRecovering) {
                if (procents <= 0.3f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), iconX, iconY, 100, 19, iconWidth, 16);
                } else if (procents <= 0.6f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), iconX, iconY, 117, 3, iconWidth, 16);
                } else if (procents <= 0.8f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), iconX, iconY, 117, 19, iconWidth, 16);
                } else if (procents <= 1.0f) {
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), iconX, iconY, 135, 19, iconWidth, 16);
                }
            } else {
                gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), iconX, iconY, 100, 3, iconWidth, 16);
            }
        }
    }
}

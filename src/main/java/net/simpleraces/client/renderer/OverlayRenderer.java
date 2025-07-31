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
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;
import net.simpleraces.network.SimpleracesModVariables;
import net.simpleraces.procedures.ResistanceAddProcedure;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "simpleraces", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OverlayRenderer {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)){
            GuiGraphics gui = event.getGuiGraphics();

            int x = 11;
            int y = 11;

            float procents = SyncVars.heat / (float) SyncVars.maxHeat;
            int maxWidth = 97;
            int barWidth = (int) (procents * maxWidth);
            gui.blit(new ResourceLocation("simpleraces","textures/screens/overheat.png"), x, y, 0, 0, maxWidth, 18);
            gui.blit(new ResourceLocation("simpleraces","textures/screens/overheat.png"), x, y + 10, 0, 25, barWidth, 5);
            if(procents <= 0.25f){
                gui.blit(new ResourceLocation("simpleraces","textures/screens/overheat.png"), maxWidth / 2 + 5, y + 1, 100, 3, 10, 14);
            } else if(procents <= 0.5f){
                gui.blit(new ResourceLocation("simpleraces","textures/screens/overheat.png"), maxWidth / 2 + 5, y + 1, 100, 19, 10, 14);
            } else if(procents <= 0.75f){
                gui.blit(new ResourceLocation("simpleraces","textures/screens/overheat.png"), maxWidth / 2 + 5, y + 1, 117, 3, 10, 14);
            } else if(procents <= 1.0f){
                gui.blit(new ResourceLocation("simpleraces","textures/screens/overheat.png"), maxWidth / 2 + 5, y + 1, 117, 19, 10, 14);
            }

        } else if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).fairy)){
            GuiGraphics gui = event.getGuiGraphics();
            int maxWidth = 70;
            int x = event.getWindow().getGuiScaledWidth() / 2 - maxWidth / 2 - 50;
            int y = event.getWindow().getGuiScaledHeight() - 60;
            int maxFlyingTicks =  SimpleRPGRacesConfiguration.FAIRY_MAX_FLYING_TIME.get() * 20;
            float procentWidth = Math.min(ResistanceAddProcedure.FAIRY_FLYING_BAR / (float) maxFlyingTicks, 1.0f);
            int width = (int) ((procentWidth) * maxWidth);
            gui.fill(x, y, x + maxWidth, y + 10, 0xFF000000);
            gui.fill(x, y, x + width, y + 10, 0xFFFFFF00);
        }
    }
}

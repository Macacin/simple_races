package net.simpleraces.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
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
        if (player == null) return;
        if((player.getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new SimpleracesModVariables.PlayerVariables()).dragon)){
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            GuiGraphics gui = event.getGuiGraphics();

            int x = 11;
            int y = 11;

            int maxWidth = 80;
            int barWidth = (int) ((SyncVars.heat / 100.0f) * maxWidth);

            gui.fill(x, y, x + maxWidth, y + 8, 0x77000000);
            gui.fill(x, y, x + barWidth, y + 8, SyncVars.overheated ? 0xFFFF0000 : 0xFFFFA500);

            gui.drawString(mc.font, "Heat: " + SyncVars.heat + "%", x, y - 10, 0xFFFFFF, true);
        }
    }
}

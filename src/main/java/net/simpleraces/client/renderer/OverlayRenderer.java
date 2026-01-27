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
            int y = height - hotbarHeight - 32 - offsetY;

            float procents;
            if (!SyncVars.overheated) {
                procents = SyncVars.heat / (float) SyncVars.maxHeat; // накопление
            } else {
                procents = SyncVars.overheatTicks / (float) SyncVars.maxOverheatTicks; // сколько осталось перегрева
            }
            procents = Math.max(0f, Math.min(1f, procents));

            int barWidth = (int) (procents * maxWidth);
            int backgroundV = SyncVars.overheated ? 46 : 0;
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x, y, 0, backgroundV, maxWidth, 19);
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x, y + 10, 0, 25, barWidth, 6);
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
                gui.blit(new ResourceLocation("simpleraces", "textures/screens/overheat.png"), x + maxWidth / 2 - 6, y - 2, 134, 18, 10, 17);
            }

        } else if (vars.fairy) {
            GuiGraphics gui = event.getGuiGraphics();

            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();

            int hotbarHeight = 22;
            int offsetY = 30;

            int maxWidth = 99;
            int x = (width - maxWidth) / 2;
            int y = height - hotbarHeight - 32 - offsetY;

            float procents;
            if (SyncVars.isFairyRecovering) {
                procents = SyncVars.fairyFlightBar / (float) SyncVars.maxFairyFlight;
            } else {
                procents = 1f - (SyncVars.fairyFlightBar / (float) SyncVars.maxFairyFlight);
            }

            int barWidth = (int) (procents * maxWidth);
            int backgroundV = SyncVars.isFairyRecovering ? 0 : 43;

            gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), x, y, 0, backgroundV, maxWidth, 19);
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/fairy_flight.png"), x, y + 10, 0, 25, barWidth, 6);
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
        } else if (vars.elf) {
            GuiGraphics gui = event.getGuiGraphics();

            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();

            int hotbarHeight = 22;
            int offsetY = 30;

            int maxWidth = 99;
            int x = (width - maxWidth) / 2;
            int y = height - hotbarHeight - 32 - offsetY;

            int maxSpirits = SimpleRPGRacesConfiguration.ELF_MAX_FOREST_SPIRITS.get();
            int maxCooldown = SimpleRPGRacesConfiguration.ELF_SPIRIT_COOLDOWN_SECONDS.get() * 20;

            // Фон для всей панели (как раньше)
            int backgroundV = 0;
            gui.blit(new ResourceLocation("simpleraces", "textures/screens/dodge_bar.png"), x, y, 0, backgroundV, maxWidth, 19);

            // Кастомные ширины сегментов: 0-34 (35px), 35-64 (30px), 65-99 (35px, но 99-65=34, так что 34px для точности)
            int[] segmentWidths = {35, 30, 34};
            int[] cords = {0, 35, 65};
            int barHeight = 9; // Высота бара

            // Порядок отображения слотов: для визуальной траты левый (slot0) -> центр (slot1) -> правый (slot2)
            int[] slotOrder = {0, 1, 2}; // displayI=0 -> realSlot=0 (левый), displayI=1 -> real=1 (центр), displayI=2 -> real=2 (правый)

            // Рисуем 3 отдельных бара, каждый для слота spiritCooldowns[i]
            int currentX = x; // Начальный X
            for (int displayI = 0; displayI < maxSpirits; displayI++) {
                int realI = slotOrder[displayI]; // Реальный индекс слота по порядку траты
                int segmentWidth = segmentWidths[displayI];
                float progress;
                if (vars.spiritCooldowns[realI] == 0) {
                    // Слот свободен (дух активен или не трачен) — полный бар
                    progress = 1.0f;
                } else {
                    // На кулдауне — прогресс зарядки
                    progress = 1.0f - ((float) vars.spiritCooldowns[realI] / maxCooldown);
                }
                int barWidth = (int) (progress * segmentWidth);

                // Блит бара для этого сегмента с учетом направления
                if (displayI == 0) { // Левый: справа налево
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/dodge_bar.png"), currentX + (segmentWidth - barWidth), y + 8, cords[displayI] + (segmentWidth - barWidth), 50, barWidth, barHeight);
                } else if (displayI == 1) { // Центр (визуально второй): в обе стороны
                    int halfWidth = barWidth / 2;
                    int centerSegmentX = currentX + segmentWidth / 2;
                    // Левая половина: от центра влево
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/dodge_bar.png"), centerSegmentX - halfWidth, y + 8, cords[displayI] + (segmentWidth / 2 - halfWidth), 50, halfWidth, barHeight);
                    // Правая половина: от центра вправо
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/dodge_bar.png"), centerSegmentX, y + 8, cords[displayI] + (segmentWidth / 2), 50, halfWidth, barHeight);
                } else { // Правый (визуально третий): слева направо
                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/dodge_bar.png"), currentX, y + 8, cords[displayI], 50, barWidth, barHeight);
                }

                // Иконка на сегменте, если полный (progress == 1.0f)
                if (progress == 1.0f) {
                    // Позиция иконки адаптирована из твоего кода (смещение относительно центра панели, но теперь per-сегмент)
                    int iconX;
                    int iconY = y + 4; // По умолчанию y+4, как в первом и третьем
                    int u, v = 22; // UV Y=22 для всех
                    int iconWidth = 10, iconHeight = 5;

                    if (displayI == 0) { // Первый дух (левый)
                        iconX = x + maxWidth / 2 - 43; // Твоя позиция для >0.333
                        u = 6;
                    } else if (displayI == 1) { // Второй визуальный (центр)
                        iconX = x + maxWidth / 2 - 3;
                        u = 46;
                        iconY = y + 3; // Твоя специальная y+3 для среднего
                    } else { // Третий визуальный (правый)
                        iconX = x + maxWidth / 2 + 40;
                        u = 89;
                    }

                    gui.blit(new ResourceLocation("simpleraces", "textures/screens/dodge_bar.png"), iconX, iconY, u, v, iconWidth, iconHeight);
                }

                // Смещаем X для следующего сегмента
                currentX += segmentWidth;
            }
        }
    }
}

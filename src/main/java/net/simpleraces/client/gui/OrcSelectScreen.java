package net.simpleraces.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.simpleraces.network.ModMessages;
import net.simpleraces.world.inventory.OrcSelectMenu;
import net.simpleraces.procedures.OrcReturnProcedure;
import net.simpleraces.network.OrcSelectButtonMessage;
import net.simpleraces.SimpleracesMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

import static net.simpleraces.client.gui.MerfolkSelectScreen.renderWidgetBox;

@OnlyIn(Dist.CLIENT)
public class OrcSelectScreen extends AbstractContainerScreen<OrcSelectMenu> {
    private final static HashMap<String, Object> guistate = OrcSelectMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    Button button_conf;
    Button button_empty;
    Button button_empty1;

    public OrcSelectScreen(OrcSelectMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 0;
        this.imageHeight = 0;
    }

    private static final ResourceLocation texture = new ResourceLocation("simpleraces:textures/screens/orc_select.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (OrcReturnProcedure.execute(world) instanceof LivingEntity livingEntity) {
            InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics,
                    this.leftPos + -37, this.topPos + 32, 30,
                    (float) Math.atan((this.leftPos + -37 - mouseX) / 40.0),
                    (float) Math.atan((this.topPos + -17 - mouseY) / 40.0),
                    livingEntity);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (mouseX > leftPos + -7 && mouseX < leftPos + 17 && mouseY > topPos + -18 && mouseY < topPos + 6)
            guiGraphics.renderTooltip(font, Component.translatable("gui.simpleraces.orc_select.tooltip_next"), mouseX, mouseY);

        if (mouseX > leftPos + -7 && mouseX < leftPos + 17 && mouseY > topPos + 8 && mouseY < topPos + 32)
            guiGraphics.renderTooltip(font, Component.translatable("gui.simpleraces.orc_select.tooltip_previous"), mouseX, mouseY);

        if (mouseX > leftPos + 22 && mouseX < leftPos + 46 && mouseY > topPos + -41 && mouseY < topPos + -17)
            guiGraphics.renderTooltip(font,
                    Component.translatable(Screen.hasShiftDown()
                            ? "gui.simpleraces.orc_select.tooltip_dwarves_are_stout_skilled_craft.extended"
                            : "gui.simpleraces.orc_select.tooltip_dwarves_are_stout_skilled_craft"), mouseX, mouseY);

        if (mouseX > leftPos + 46 && mouseX < leftPos + 70 && mouseY > topPos + -41 && mouseY < topPos + -17)
            guiGraphics.renderTooltip(font,
                    Component.translatable(Screen.hasShiftDown()
                            ? "gui.simpleraces.orc_select.tooltip_passive_mine_faster_in_dark.extended"
                            : "gui.simpleraces.orc_select.tooltip_passive_mine_faster_in_dark"), mouseX, mouseY);

        if (mouseX > leftPos + 25 && mouseX < leftPos + 49 && mouseY > topPos + 24 && mouseY < topPos + 48)
            guiGraphics.renderTooltip(font, Component.translatable("gui.simpleraces.orc_select.tooltip_confirm"), mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        guiGraphics.blit(new ResourceLocation("simpleraces:textures/screens/orc.png"), this.leftPos + -89, this.topPos + -84, 0, 0, 256, 256, 256, 256);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void init() {
        super.init();
        button_conf = new PlainTextButton(this.leftPos - 3, this.topPos + 27, 46, 20, Component.translatable("gui.simpleraces.orc_select.button_conf"), e -> {
            if (true) {
                ModMessages.INSTANCE.sendToServer(new OrcSelectButtonMessage(0, x, y, z));
                OrcSelectButtonMessage.handleButtonAction(entity, 0, x, y, z);
            }
        }, this.font);
        guistate.put("button:button_conf", button_conf);
        this.addRenderableWidget(button_conf);
        button_empty = new PlainTextButton(this.leftPos + -9, this.topPos + -14, 25, 20, Component.translatable("gui.simpleraces.orc_select.button_empty"), e -> {
            if (true) {
                ModMessages.INSTANCE.sendToServer(new OrcSelectButtonMessage(1, x, y, z));
                OrcSelectButtonMessage.handleButtonAction(entity, 1, x, y, z);
            }
        }, this.font);
        guistate.put("button:button_empty", button_empty);
        this.addRenderableWidget(button_empty);
        button_empty1 = new PlainTextButton(this.leftPos + -9, this.topPos + 7, 25, 20, Component.translatable("gui.simpleraces.orc_select.button_empty1"), e -> {
            if (true) {
                ModMessages.INSTANCE.sendToServer(new OrcSelectButtonMessage(2, x, y, z));
                OrcSelectButtonMessage.handleButtonAction(entity, 2, x, y, z);
            }
        }, this.font);
        guistate.put("button:button_empty1", button_empty1);
        this.addRenderableWidget(button_empty1);
    }
}

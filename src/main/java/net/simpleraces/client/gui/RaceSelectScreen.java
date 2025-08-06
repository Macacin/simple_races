package net.simpleraces.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.simpleraces.entity.ArachaModelEntity;
import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.network.*;
import net.simpleraces.world.AbstractRaceSelectMenu;
import net.simpleraces.world.inventory.ElfSelectMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public abstract class RaceSelectScreen<T extends AbstractRaceSelectMenu> extends AbstractContainerScreen<T> {
	private static final Map<String, BiConsumer<Player, Integer>> actions = new HashMap<>();
	private final static Map<String, Runnable> actionsR = new HashMap<>();
	private final static HashMap<String, Object> guistate = ElfSelectMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_conf;
	Button button_empty;
	Button button_empty1;

	public RaceSelectScreen(T container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 0;
		this.imageHeight = 0;
	}

	private final ResourceLocation texture = new ResourceLocation("simpleraces:textures/screens/" + title.getString() + ".png");

	private Rect2i tooltipConfirm, tooltipNext, tooltipPrevious;
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + -37, this.topPos + 32, 28,
				(float) Math.atan((this.leftPos + -37 - mouseX) / 40.0),
				(float) Math.atan((this.topPos + -17 - mouseY) / 40.0),
				new ArachaModelEntity(SimpleracesModEntities.getByName(title.getString()), world));

		Rect2i tooltipTrait1 = new Rect2i(leftPos - 3, topPos - 23, 34, 11);
		Rect2i tooltipTrait2 = new Rect2i(leftPos + 33, topPos - 23, 34, 11);

		if (tooltipTrait1.contains(mouseX, mouseY)) {
			String key = Screen.hasShiftDown() ?
					"gui.simpleraces." + title.getString() + "_select.tooltip_dwarves_are_stout_skilled_craft.extended" :
					"gui.simpleraces." + title.getString() + "_select.tooltip_dwarves_are_stout_skilled_craft";
			List<Component> lines = splitComponent(Component.translatable(key));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		if (tooltipTrait2.contains(mouseX, mouseY)) {
			String key = Screen.hasShiftDown() ?
					"gui.simpleraces." + title.getString() + "_select.tooltip_passive_mine_faster_in_dark.extended" :
					"gui.simpleraces." + title.getString() + "_select.tooltip_passive_mine_faster_in_dark";
			List<Component> lines = splitComponent(Component.translatable(key));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		if (tooltipConfirm.contains(mouseX, mouseY)) {
			List<Component> lines = splitComponent(Component.translatable("gui.simpleraces.elf_select.tooltip_confirm"));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		if (tooltipNext.contains(mouseX, mouseY)) {
			List<Component> lines = splitComponent(Component.translatable("gui.simpleraces.elf_select.tooltip_next"));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		if (tooltipPrevious.contains(mouseX, mouseY)) {
			List<Component> lines = splitComponent(Component.translatable("gui.simpleraces.elf_select.tooltip_previous"));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(texture, this.leftPos + -89, this.topPos + -84, 0, 0, 256, 256, 256, 256);

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
		tooltipConfirm = new Rect2i(leftPos - 2, topPos + 27, 45, 15);
		tooltipNext = new Rect2i(leftPos - 3, topPos - 10, 11, 15);
		tooltipPrevious = new Rect2i(leftPos - 3, topPos + 8, 11, 15);
		button_conf = new PlainTextButton(tooltipPrevious.getX(), tooltipPrevious.getY(), tooltipPrevious.getWidth(), tooltipPrevious.getHeight(), Component.translatable("gui.simpleraces.elf_select.button_conf"), e -> {
			if (true) {
				ModMessages.INSTANCE.sendToServer(getPacketForRace(title.getString(), 2));
				execute(title.getString(), 2, entity);
			}
		}, this.font);
		guistate.put("button:button_conf", button_conf);
		this.addRenderableWidget(button_conf);
		button_empty = new PlainTextButton(tooltipConfirm.getX(), tooltipConfirm.getY(), tooltipConfirm.getWidth(), tooltipConfirm.getHeight(), Component.translatable("gui.simpleraces.elf_select.button_empty"), e -> {
			if (true) {
				ModMessages.INSTANCE.sendToServer(getPacketForRace(title.getString(), 0));
				execute(title.getString(), 0, entity);
			}
		}, this.font);
		guistate.put("button:button_empty", button_empty);
		this.addRenderableWidget(button_empty);
		button_empty1 = new PlainTextButton(tooltipNext.getX(), tooltipNext.getY(), tooltipNext.getWidth(), tooltipNext.getHeight(), Component.translatable("gui.simpleraces.elf_select.button_empty1"), e -> {
			if (true) {
				ModMessages.INSTANCE.sendToServer(getPacketForRace(title.getString(), 1));
				execute(title.getString(), 1, entity);
			}
		}, this.font);
		guistate.put("button:button_empty1", button_empty1);
		this.addRenderableWidget(button_empty1);
	}

	static {
		actions.put("dwarf", (player, id) -> DwarfSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("elf",  (player, id) -> ElfSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("orc", (player, id) -> OrcSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("merfolk", (player, id) -> MerfolkSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("dragon", (player, id) -> DragonSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("fairy", (player, id) -> FairySelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("halfdead", (player, id) -> HalfdeadSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("serpentin", (player, id) -> SerpentinSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("werewolf", (player, id) -> WerewolfSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
		actions.put("arachna", (player, id) -> ArachaSelectButtonMessage.handleButtonAction(player, id, 0, 0, 0));
	}

	public Object getPacketForRace(String name, int id){
		return switch (name){
			case "dwarf" -> new DwarfSelectButtonMessage(id, x, y, z);
			case "elf" -> new ElfSelectButtonMessage(id, x, y, z);
			case "orc" -> new OrcSelectButtonMessage(id, x, y, z);
			case "merfolk" -> new MerfolkSelectButtonMessage(id, x, y, z);
			case "dragon" -> new DragonSelectButtonMessage(id, x, y, z);
			case "fairy" -> new FairySelectButtonMessage(id, x, y, z);
			case "halfdead" -> new HalfdeadSelectButtonMessage(id, x, y, z);
			case "serpentin" -> new SerpentinSelectButtonMessage(id, x, y, z);
			case "werewolf" -> new WerewolfSelectButtonMessage(id, x, y, z);
			case "arachna" -> new ArachaSelectButtonMessage(id, x, y, z);
			default -> null;
		};
	}

	public static void execute(String raceName, int id, Player entity) {
		BiConsumer<Player, Integer> action = actions.get(raceName.toLowerCase());
		if (action != null) {
			action.accept(entity, id);
		} else {
			System.out.println("Нет кнопки для расы: " + raceName);
		}
	}
	private List<Component> splitComponent(Component component) {
		String[] lines = component.getString().split("\n");
		List<Component> components = new ArrayList<>();
		for (String line : lines) {
			components.add(Component.literal(line));
		}
		return components;
	}
}
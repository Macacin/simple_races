package net.simpleraces.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.simpleraces.SimpleracesMod;
import net.simpleraces.init.SimpleracesModEntities;
import net.simpleraces.network.*;
import net.simpleraces.world.AbstractRaceSelectMenu;
import net.simpleraces.world.inventory.ElfSelectMenu;

import java.util.ArrayList;
import java.util.IdentityHashMap;
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
	Button button_help;

	private static final long CURSOR_RESTORE_TIMEOUT_NANOS = 10_000_000_000L;
	private static final int CURSOR_RESTORE_FRAME_COUNT = 10;
	private static final Map<Level, Map<String, LivingEntity>> PREVIEW_ENTITY_CACHE = new IdentityHashMap<>();
	private static double pendingCursorX = Double.NaN;
	private static double pendingCursorY = Double.NaN;
	private static long pendingCursorDeadline;

	private double lastMouseX;
	private double lastMouseY;
	private LivingEntity previewEntity;
	private int cursorRestoreFramesRemaining;
	private boolean loggedFirstRender;
	private boolean loggedFirstModelRender;
	private boolean loggedFirstBackgroundRender;

	public RaceSelectScreen(T container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 0;
		this.imageHeight = 0;
		SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} RaceSelectScreen ctor player={}", text.getString(), entity.getName().getString());
	}

	private Rect2i tooltipConfirm, tooltipNext, tooltipPrevious, tooltipHelp, tooltipDifficulty;
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (!loggedFirstRender) {
			loggedFirstRender = true;
			SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} first render begin", title.getString());
		}
		retryPendingCursorRestore();
		if (hasPendingCursorRestore()) {
			mouseX = (int) Math.round(pendingCursorX);
			mouseY = (int) Math.round(pendingCursorY);
		}
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;

		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		int modelYOffset = getEntityRenderYOffset();
		if (previewEntity == null) {
			previewEntity = getOrCreatePreviewEntity();
		}
		if (previewEntity != null) {
			if (!loggedFirstModelRender) {
				loggedFirstModelRender = true;
				SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} first preview render entity={}", title.getString(), previewEntity.getType().toShortString());
			}
			InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos - 65, this.topPos - 40 + modelYOffset, this.leftPos - 9, this.topPos + 48 + modelYOffset, 28, 0.0f,
					(float) Math.atan((this.leftPos - 37 - mouseX) / 40.0),
					(float) Math.atan((this.topPos - 17 - mouseY) / 40.0),
					previewEntity);
		}

		Rect2i tooltipTrait1 = new Rect2i(leftPos - 3, topPos - 23, 34, 11);
		Rect2i tooltipTrait2 = new Rect2i(leftPos + 33, topPos - 23, 34, 11);

		if (tooltipTrait1.contains(mouseX, mouseY)) {
			List<Component> lines = buildTraitTooltipLines("gui.simpleraces." + title.getString() + "_select.tooltip_dwarves_are_stout_skilled_craft");
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		if (tooltipTrait2.contains(mouseX, mouseY)) {
			List<Component> lines = buildTraitTooltipLines("gui.simpleraces." + title.getString() + "_select.tooltip_passive_mine_faster_in_dark");
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

		if (button_help != null && button_help.visible && tooltipHelp.contains(mouseX, mouseY)) {
			List<Component> lines = splitComponent(Component.translatable("gui.simpleraces.random_select.tooltip"));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		if (tooltipDifficulty.contains(mouseX, mouseY)) {
			List<Component> lines = buildDifficultyTooltipLines();
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}

		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		if (!loggedFirstBackgroundRender) {
			loggedFirstBackgroundRender = true;
			SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} first background render", title.getString());
		}
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		ResourceLocation texture = getLocalizedTexture();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(texture, this.leftPos + -89, this.topPos + -84, 0, 0, 256, 256, 256, 256);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			clearPendingCursorRestore();
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
		long start = System.nanoTime();
		super.init();
		long afterSuper = System.nanoTime();
		previewEntity = getOrCreatePreviewEntity();
		long afterPreview = System.nanoTime();
		tooltipConfirm = new Rect2i(leftPos - 2, topPos + 27, 45, 15);
		tooltipNext = new Rect2i(leftPos - 3, topPos - 10, 11, 15);
		tooltipPrevious = new Rect2i(leftPos - 3, topPos + 8, 11, 15);
		tooltipHelp = new Rect2i(this.leftPos + 46, this.topPos - 77, 12, 12);
		tooltipDifficulty = new Rect2i(this.leftPos - 70, this.topPos - 36, 66, 12);
		button_conf = new PlainTextButton(tooltipPrevious.getX(), tooltipPrevious.getY(), tooltipPrevious.getWidth(), tooltipPrevious.getHeight(), Component.translatable("gui.simpleraces.elf_select.button_conf"), e -> {
			rememberCursorForNextRaceScreen();
			ModMessages.INSTANCE.sendToServer(getPacketForRace(title.getString(), 2));
		}, this.font);
		guistate.put("button:button_conf", button_conf);
		this.addRenderableWidget(button_conf);
		button_empty = new PlainTextButton(tooltipConfirm.getX(), tooltipConfirm.getY(), tooltipConfirm.getWidth(), tooltipConfirm.getHeight(), Component.translatable("gui.simpleraces.elf_select.button_empty"), e -> {
			clearPendingCursorRestore();
			ModMessages.INSTANCE.sendToServer(getPacketForRace(title.getString(), 0));
		}, this.font);
		guistate.put("button:button_empty", button_empty);
		this.addRenderableWidget(button_empty);
		button_empty1 = new PlainTextButton(tooltipNext.getX(), tooltipNext.getY(), tooltipNext.getWidth(), tooltipNext.getHeight(), Component.translatable("gui.simpleraces.elf_select.button_empty1"), e -> {
			rememberCursorForNextRaceScreen();
			ModMessages.INSTANCE.sendToServer(getPacketForRace(title.getString(), 1));
		}, this.font);
		guistate.put("button:button_empty1", button_empty1);
		this.addRenderableWidget(button_empty1);
		button_help = new HelpButton(this.leftPos + 46, this.topPos - 77);
		button_help.visible = !SimpleracesModVariables.getPlayerVariables(entity).selected;
		guistate.put("button:button_help", button_help);
		this.addRenderableWidget(button_help);
		cursorRestoreFramesRemaining = hasPendingCursorRestore() ? CURSOR_RESTORE_FRAME_COUNT : 0;
		restorePendingCursorImmediately();
		SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} init finished: super={} ms preview={} ms total={} ms",
				title.getString(),
				(afterSuper - start) / 1_000_000.0,
				(afterPreview - afterSuper) / 1_000_000.0,
				(System.nanoTime() - start) / 1_000_000.0);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		retryPendingCursorRestore();
	}

	private int getEntityRenderYOffset() {
		return switch (title.getString()) {
			case "dwarf" -> 8;
			case "fairy" -> 16;
			default -> 4;
		};
	}

	private void rememberCursorForNextRaceScreen() {
		pendingCursorX = this.lastMouseX;
		pendingCursorY = this.lastMouseY;
		pendingCursorDeadline = System.nanoTime() + CURSOR_RESTORE_TIMEOUT_NANOS;
	}

	private void restorePendingCursorImmediately() {
		if (this.minecraft == null || !hasPendingCursorRestore()) {
			clearPendingCursorRestore();
			return;
		}

		double restoreX = pendingCursorX;
		double restoreY = pendingCursorY;

		Window window = this.minecraft.getWindow();
		double windowX = restoreX * window.getScreenWidth() / (double) window.getGuiScaledWidth();
		double windowY = restoreY * window.getScreenHeight() / (double) window.getGuiScaledHeight();

		// Restore during Screen.init(), before the next frame can display the centered cursor.
		InputConstants.grabOrReleaseMouse(window.getWindow(), InputConstants.CURSOR_NORMAL, windowX, windowY);
		this.lastMouseX = restoreX;
		this.lastMouseY = restoreY;
	}

	private void retryPendingCursorRestore() {
		if (cursorRestoreFramesRemaining <= 0) {
			return;
		}

		restorePendingCursorImmediately();
		cursorRestoreFramesRemaining--;
		if (cursorRestoreFramesRemaining <= 0) {
			clearPendingCursorRestore();
		}
	}

	private static boolean hasPendingCursorRestore() {
		return !Double.isNaN(pendingCursorX)
				&& !Double.isNaN(pendingCursorY)
				&& System.nanoTime() <= pendingCursorDeadline;
	}

	private static void clearPendingCursorRestore() {
		pendingCursorX = Double.NaN;
		pendingCursorY = Double.NaN;
		pendingCursorDeadline = 0L;
	}

	private LivingEntity getOrCreatePreviewEntity() {
		return getOrCreatePreviewEntity(title.getString());
	}

	private LivingEntity getOrCreatePreviewEntity(String raceName) {
		if (world == null) {
			return null;
		}

		long start = System.nanoTime();
		Map<String, LivingEntity> levelCache = PREVIEW_ENTITY_CACHE.computeIfAbsent(world, key -> new HashMap<>());
		return levelCache.computeIfAbsent(raceName, key -> {
			long createStart = System.nanoTime();
			SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} preview entity create begin", raceName);
			var entityType = SimpleracesModEntities.getByName(raceName);
			if (entityType == null) {
				SimpleracesMod.LOGGER.warn("[SR-RACE-LOAD] client {} preview entity type missing", raceName);
				return null;
			}

			var created = entityType.create(world);
			if (!(created instanceof LivingEntity livingEntity)) {
				SimpleracesMod.LOGGER.warn("[SR-RACE-LOAD] client {} preview entity create returned {}", raceName, created);
				return null;
			}

			if (livingEntity instanceof Mob mob) {
				mob.setNoAi(true);
			}
			livingEntity.setYRot(180.0f);
			livingEntity.yHeadRot = 180.0f;
			livingEntity.yBodyRot = 180.0f;
			SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] client {} preview entity create finished in {} ms",
					raceName, (System.nanoTime() - createStart) / 1_000_000.0);
			return livingEntity;
		});
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
			case "gargoyle" -> new GargoyleSelectButtonMessage(id, x, y, z);
			case "human" -> new HumanSelectButtonMessage(id, x, y, z);
			default -> null;
		};
	}

	private List<Component> splitComponent(Component component) {
		String[] lines = component.getString().split("\n");
		List<Component> components = new ArrayList<>();
		for (String line : lines) {
			components.add(Component.literal(line));
		}
		return components;
	}

	private List<Component> buildTraitTooltipLines(String baseKey) {
		boolean extended = Screen.hasShiftDown();
		String key = extended ? baseKey + ".extended" : baseKey;
		List<Component> lines = splitComponent(Component.translatable(key));
		if (!extended) {
            lines.add(Component.translatable("gui.simpleraces.tooltip.hold_shift")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		}
		return lines;
	}

	private List<Component> buildDifficultyTooltipLines() {
		String raceKey = "gui.simpleraces." + title.getString() + "_select.tooltip_difficulty_scale";
		Component translated = Component.translatable(raceKey);
		if (translated.getString().equals(raceKey)) {
			translated = Component.translatable("gui.simpleraces.race_select.tooltip_difficulty_scale");
		}
		return splitComponent(translated);
	}

	private ResourceLocation getLocalizedTexture() {
		String localeFolder = isRussianLocale() ? "ru" : "eng";
		return ResourceLocation.parse("simpleraces:textures/screens/" + localeFolder + "/" + title.getString() + ".png");
	}

	private boolean isRussianLocale() {
		if (minecraft == null || minecraft.getLanguageManager() == null) {
			return false;
		}

		String selectedLanguage = minecraft.getLanguageManager().getSelected();
		return selectedLanguage != null && selectedLanguage.toLowerCase().startsWith("ru");
	}

	private class HelpButton extends Button {
		private HelpButton(int x, int y) {
			super(x, y, 12, 12, Component.literal("?"), e -> {
				RaceSelectScreen.this.rememberCursorForNextRaceScreen();
				ModMessages.INSTANCE.sendToServer(new RandomRaceButtonMessage());
			}, DEFAULT_NARRATION);
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int borderColor = this.isHoveredOrFocused() ? 0xFF9D93C5 : 0xFF4C4764;
			int fillColor = this.isHoveredOrFocused() ? 0xFF5C5678 : 0xFF3E3A54;
			int innerColor = this.isHoveredOrFocused() ? 0xFF746D93 : 0xFF514B6A;
			guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, borderColor);
			guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, fillColor);
			guiGraphics.fill(this.getX() + 2, this.getY() + 2, this.getX() + this.width - 2, this.getY() + this.height - 2, innerColor);
			guiGraphics.drawCenteredString(RaceSelectScreen.this.font, this.getMessage(), this.getX() + this.width / 2, this.getY() + 2,
					this.isHoveredOrFocused() ? 0xFFD8C6FF : 0xFFB99AE8);
		}
	}
}

package net.simpleraces.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.simpleraces.SimpleracesMod;

@EventBusSubscriber(modid = SimpleracesMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RaceSelectionPreloader {
    private static final String[] RACES = {
            "dwarf", "elf", "orc", "merfolk", "dragon", "fairy",
            "halfdead", "serpentin", "werewolf", "arachna", "gargoyle", "human"
    };

    private static final ResourceLocation[] ENTITY_TEXTURES = {
            texture("entities/2024_10_07_dwarf-22808312.png"),
            texture("entities/2024_10_18_legolas-22827741.png"),
            texture("entities/2024_10_07_orc-22808309.png"),
            texture("entities/2023_10_18_kuno-thalassian-22053605.png"),
            texture("entities/2024_09_02_fantasy-mc-dragonborn--red---fixed-for-3d-pixels--22745241.png"),
            texture("entities/fairy.png"),
            texture("entities/halfdead.png"),
            texture("entities/serpentin.png"),
            texture("entities/werewolf.png"),
            texture("entities/arachna.png"),
            texture("entities/gargoyle.png"),
            texture("entities/human.png")
    };

    private RaceSelectionPreloader() {
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                preloadRaceSelectionTextures();
            }
        });
    }

    private static void preloadRaceSelectionTextures() {
        long start = System.nanoTime();
        var textureManager = Minecraft.getInstance().getTextureManager();

        preload(textureManager, texture("screens/start.png"));
        preload(textureManager, texture("screens/start_1.png"));
        preload(textureManager, texture("screens/garg_stances.png"));

        for (String race : RACES) {
            preload(textureManager, texture("screens/ru/" + race + ".png"));
            preload(textureManager, texture("screens/eng/" + race + ".png"));
        }

        for (ResourceLocation entityTexture : ENTITY_TEXTURES) {
            preload(textureManager, entityTexture);
        }

        SimpleracesMod.LOGGER.info("[SR-RACE-LOAD] preloaded race selection textures in {} ms", (System.nanoTime() - start) / 1_000_000.0);
    }

    private static void preload(net.minecraft.client.renderer.texture.TextureManager textureManager, ResourceLocation texture) {
        textureManager.getTexture(texture);
    }

    private static ResourceLocation texture(String path) {
        return ResourceLocation.fromNamespaceAndPath(SimpleracesMod.MODID, "textures/" + path);
    }
}

package net.simpleraces.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WerewolfForbiddenItems {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Set<ResourceLocation> ITEMS = new HashSet<>();

    public static void load() {
        ITEMS.clear();

        // Загружаем из assets (встроенный в мод)
        try (var stream = WerewolfForbiddenItems.class.getResourceAsStream("/assets/simpleraces/werewolf/forbidden_items.json")) {
            if (stream != null) {
                String json = new String(stream.readAllBytes());
                parseAndLoad(json);
                LOGGER.info("Loaded {} forbidden items for werewolves from assets", ITEMS.size());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load forbidden items from assets", e);
        }

        // Также проверяем config папку для пользовательских настроек
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("simpleraces/werewolf_forbidden_items.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                int before = ITEMS.size();
                parseAndLoad(json);
                LOGGER.info("Loaded additional {} forbidden items from config", ITEMS.size() - before);
            } catch (IOException e) {
                LOGGER.error("Failed to load forbidden items from config", e);
            }
        }
    }

    private static void parseAndLoad(String json) {
        Gson gson = new Gson();
        List<String> items = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
        if (items != null) {
            for (String item : items) {
                ITEMS.add(new ResourceLocation(item));
            }
        }
    }

    public static boolean isForbidden(ResourceLocation id) {
        return ITEMS.contains(id);
    }
}
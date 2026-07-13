package net.simpleraces.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public final class AttributeCompat {
    private AttributeCompat() {
    }

    public static ResourceLocation id(UUID uuid) {
        return ResourceLocation.fromNamespaceAndPath("simpleraces", uuid.toString().replace('-', '_'));
    }

    public static boolean hasModifier(AttributeInstance attribute, UUID uuid) {
        return attribute != null && attribute.getModifier(id(uuid)) != null;
    }

    public static void removeModifier(AttributeInstance attribute, UUID uuid) {
        if (attribute != null) {
            attribute.removeModifier(id(uuid));
        }
    }

    public static void addTransientModifier(AttributeInstance attribute, UUID uuid, double amount, AttributeModifier.Operation operation) {
        if (attribute != null && !hasModifier(attribute, uuid)) {
            attribute.addTransientModifier(new AttributeModifier(id(uuid), amount, operation));
        }
    }

    public static ResourceLocation id(String key) {
        return ResourceLocation.fromNamespaceAndPath("simpleraces", key.toLowerCase().replace(' ', '_'));
    }
}




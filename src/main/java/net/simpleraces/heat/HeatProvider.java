package net.simpleraces.heat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HeatProvider {
    private static final String TAG_NAME = "simpleraces_heat";
    private static final Map<UUID, PersistentHeat> HEAT_CACHE = new ConcurrentHashMap<>();

    private HeatProvider() {
    }

    public static IHeat get(Player player) {
        return HEAT_CACHE.computeIfAbsent(player.getUUID(), id -> {
            PersistentHeat heat = new PersistentHeat(player);
            load(player, heat);
            return heat;
        });
    }

    public static void save(Player player) {
        IHeat heat = HEAT_CACHE.get(player.getUUID());
        if (heat != null) {
            save(player, heat);
        }
    }

    public static void clear(Player player) {
        HEAT_CACHE.remove(player.getUUID());
    }

    public static void copy(Player from, Player to) {
        PersistentHeat copy = new PersistentHeat(to);
        IHeat source = get(from);
        copy.setHeat(source.getHeat());
        copy.setOverheated(source.isOverheated());
        copy.setOverheatTicks(source.getOverheatTicks());
        copy.setPostOverheatExtinguishTicks(source.getPostOverheatExtinguishTicks());
        HEAT_CACHE.put(to.getUUID(), copy);
        save(to, copy);
    }

    private static void load(Player player, IHeat heat) {
        CompoundTag root = player.getPersistentData().getCompound(TAG_NAME);
        heat.setHeat(root.getInt("heat"));
        heat.setOverheated(root.getBoolean("overheated"));
        heat.setOverheatTicks(root.getInt("overheat_ticks"));
        heat.setPostOverheatExtinguishTicks(root.getInt("post_overheat_extinguish_ticks"));
    }

    private static void save(Player player, IHeat heat) {
        CompoundTag root = new CompoundTag();
        root.putInt("heat", heat.getHeat());
        root.putBoolean("overheated", heat.isOverheated());
        root.putInt("overheat_ticks", heat.getOverheatTicks());
        root.putInt("post_overheat_extinguish_ticks", heat.getPostOverheatExtinguishTicks());
        player.getPersistentData().put(TAG_NAME, root);
    }

    private static final class PersistentHeat extends Heat {
        private final Player player;

        private PersistentHeat(Player player) {
            this.player = player;
        }

        @Override
        public void setHeat(int value) {
            super.setHeat(value);
            save(player, this);
        }

        @Override
        public void setOverheated(boolean value) {
            super.setOverheated(value);
            save(player, this);
        }

        @Override
        public void setOverheatTicks(int ticks) {
            super.setOverheatTicks(ticks);
            save(player, this);
        }

        @Override
        public void setPostOverheatExtinguishTicks(int ticks) {
            super.setPostOverheatExtinguishTicks(ticks);
            save(player, this);
        }
    }
}





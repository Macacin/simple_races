package net.simpleraces.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.simpleraces.compat.neoforge.network.NetworkEvent;
import net.simpleraces.heat.HeatProvider;
import net.simpleraces.heat.IHeat;
import net.simpleraces.SimpleracesMod;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber
public class SimpleracesModVariables {
    private static final String PLAYER_VARS_TAG = "simpleraces_player_variables";
    private static final Map<UUID, PlayerVariables> PLAYER_VARIABLES_CACHE = new ConcurrentHashMap<>();

    public static final EntityCapability<IHeat, Void> HEAT = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("simpleraces", "heat"), IHeat.class);
    public static final EntityCapability<PlayerVariables, Void> PLAYER_VARIABLES_CAPABILITY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("simpleraces", "player_variables"), PlayerVariables.class);

    @SubscribeEvent
    public static void init(RegisterCapabilitiesEvent event) {
        event.registerEntity(HEAT, EntityType.PLAYER, (entity, context) -> entity instanceof FakePlayer ? null : HeatProvider.get((Player) entity));
        event.registerEntity(PLAYER_VARIABLES_CAPABILITY, EntityType.PLAYER, (entity, context) -> entity instanceof FakePlayer ? null : getPlayerVariables((Player) entity));
    }

    @EventBusSubscriber
    public static class EventBusVariableHandlers {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                reloadPlayerVariables(event.getEntity());
                syncAllPlayers(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                syncAllPlayers(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                syncAllPlayers(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onPlayerSave(PlayerEvent.SaveToFile event) {
            savePlayerVariables(event.getEntity());
            HeatProvider.save(event.getEntity());
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            savePlayerVariables(event.getEntity());
            HeatProvider.save(event.getEntity());
            PLAYER_VARIABLES_CACHE.remove(event.getEntity().getUUID());
            HeatProvider.clear(event.getEntity());
        }

        @SubscribeEvent
        public static void clonePlayer(PlayerEvent.Clone event) {
            PlayerVariables original = getPlayerVariables(event.getOriginal());
            PlayerVariables clone = getPlayerVariables(event.getEntity());

            clone.copyFrom(original);
            savePlayerVariables(event.getEntity());
            HeatProvider.copy(event.getOriginal(), event.getEntity());

            if (!event.getEntity().level().isClientSide()) {
                syncAllPlayers(event.getEntity());
            }
        }

        private static void syncAllPlayers(Player referencePlayer) {
            for (Entity entityiterator : new ArrayList<>(referencePlayer.level().players())) {
                getPlayerVariables((Player) entityiterator).syncPlayerVariables(entityiterator);
            }
        }
    }

    public static PlayerVariables getPlayerVariables(Player player) {
        return PLAYER_VARIABLES_CACHE.computeIfAbsent(player.getUUID(), id -> {
            PlayerVariables variables = new PlayerVariables();
            Tag stored = player.getPersistentData().get(PLAYER_VARS_TAG);
            if (stored != null) {
                variables.readNBT(stored);
            }
            return variables;
        });
    }

    /**
     * A reconnect creates a new ServerPlayer instance, so its saved NBT must be
     * authoritative. Never reuse a UUID cache entry left by an older connection.
     */
    public static PlayerVariables reloadPlayerVariables(Player player) {
        PlayerVariables variables = new PlayerVariables();
        Tag stored = player.getPersistentData().get(PLAYER_VARS_TAG);
        if (stored instanceof CompoundTag) {
            variables.readNBT(stored);
        }
        PLAYER_VARIABLES_CACHE.put(player.getUUID(), variables);
        SimpleracesMod.LOGGER.info("[SR-RACE-SAVE] Loaded race for {}: selected={}, race={}",
                player.getGameProfile().getName(), variables.selected, variables.getRaceName());
        return variables;
    }

    public static PlayerVariables getPlayerVariables(Entity entity) {
        return entity instanceof Player player ? getPlayerVariables(player) : new PlayerVariables();
    }

    public static void updatePlayerVariables(Entity entity, Consumer<PlayerVariables> updater) {
        if (entity instanceof Player player) {
            PlayerVariables variables = getPlayerVariables(player);
            updater.accept(variables);
            variables.syncPlayerVariables(player);
        }
    }

    public static IHeat getHeat(Player player) {
        return HeatProvider.get(player);
    }

    public static void savePlayerVariables(Player player) {
        PlayerVariables variables = PLAYER_VARIABLES_CACHE.get(player.getUUID());
        if (variables != null) {
            player.getPersistentData().put(PLAYER_VARS_TAG, variables.writeNBT());
        }
    }

    public static class PlayerVariables {
        public boolean dwarf = false;
        public boolean elf = false;
        public boolean orc = false;
        public boolean merfolk = false;
        public boolean dragon = false;
        public boolean fairy = false;
        public boolean halfdead = false;
        public boolean serpentin = false;
        public boolean werewolf = false;
        public boolean aracha = false;
        public boolean gargoyle = false;
        public boolean human = false;
        public boolean selected = false;

        public int fervorStacks = 0;
        public UUID lastTarget = null;
        public int previousFoodLevel = 20;
        public int forestSpirits = 3;
        public int[] spiritCooldowns = new int[3];
        public boolean raceCapacityInitialized = false;
        public int humanMiningActions = 0;
        public int humanRangedActions = 0;
        public int humanSwordActions = 0;
        public int humanAxeActions = 0;
        public int humanTridentActions = 0;
        public int humanPickaxeActions = 0;
        public int humanShovelActions = 0;
        public int humanHoeActions = 0;
        public int humanDaggerActions = 0;
        public int humanPolearmActions = 0;
        public int humanBluntActions = 0;
        public int humanGenericMeleeActions = 0;

        public void syncPlayerVariables(Entity entity) {
            if (entity instanceof Player player) {
                savePlayerVariables(player);
            }
            if (entity instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToDimension((ServerLevel) entity.level(), new PlayerVariablesSyncMessage(this, entity.getId()));
            }
        }

        public CompoundTag writeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("dwarf", dwarf);
            nbt.putBoolean("elf", elf);
            nbt.putBoolean("orc", orc);
            nbt.putBoolean("merfolk", merfolk);
            nbt.putBoolean("dragon", dragon);
            nbt.putBoolean("selected", selected);
            nbt.putBoolean("fairy", fairy);
            nbt.putBoolean("halfdead", halfdead);
            nbt.putBoolean("Serpentin", serpentin);
            nbt.putBoolean("werewolf", werewolf);
            nbt.putBoolean("aracha", aracha);
            nbt.putBoolean("gargoyle", gargoyle);
            nbt.putBoolean("human", human);
            nbt.putBoolean("raceCapacityInitialized", raceCapacityInitialized);
            nbt.putInt("fervorStacks", fervorStacks);
            nbt.putInt("forestSpirits", forestSpirits);
            nbt.putIntArray("spiritCooldowns", spiritCooldowns);
            nbt.putInt("humanMiningActions", humanMiningActions);
            nbt.putInt("humanRangedActions", humanRangedActions);
            nbt.putInt("humanSwordActions", humanSwordActions);
            nbt.putInt("humanAxeActions", humanAxeActions);
            nbt.putInt("humanTridentActions", humanTridentActions);
            nbt.putInt("humanPickaxeActions", humanPickaxeActions);
            nbt.putInt("humanShovelActions", humanShovelActions);
            nbt.putInt("humanHoeActions", humanHoeActions);
            nbt.putInt("humanDaggerActions", humanDaggerActions);
            nbt.putInt("humanPolearmActions", humanPolearmActions);
            nbt.putInt("humanBluntActions", humanBluntActions);
            nbt.putInt("humanGenericMeleeActions", humanGenericMeleeActions);
            if (lastTarget != null) {
                nbt.putUUID("lastTarget", lastTarget);
            }
            nbt.putInt("previousFoodLevel", previousFoodLevel);
            return nbt;
        }

        public void readNBT(Tag tag) {
            CompoundTag nbt = (CompoundTag) tag;
            dwarf = nbt.getBoolean("dwarf");
            elf = nbt.getBoolean("elf");
            orc = nbt.getBoolean("orc");
            merfolk = nbt.getBoolean("merfolk");
            dragon = nbt.getBoolean("dragon");
            selected = nbt.getBoolean("selected");
            fairy = nbt.getBoolean("fairy");
            halfdead = nbt.getBoolean("halfdead");
            serpentin = nbt.getBoolean("Serpentin");
            werewolf = nbt.getBoolean("werewolf");
            aracha = nbt.getBoolean("aracha");
            gargoyle = nbt.getBoolean("gargoyle");
            human = nbt.getBoolean("human");
            raceCapacityInitialized = nbt.getBoolean("raceCapacityInitialized");
            fervorStacks = nbt.getInt("fervorStacks");
            humanMiningActions = nbt.getInt("humanMiningActions");
            humanRangedActions = nbt.getInt("humanRangedActions");
            humanSwordActions = nbt.getInt("humanSwordActions");
            humanAxeActions = nbt.getInt("humanAxeActions");
            humanTridentActions = nbt.getInt("humanTridentActions");
            humanPickaxeActions = nbt.getInt("humanPickaxeActions");
            humanShovelActions = nbt.getInt("humanShovelActions");
            humanHoeActions = nbt.getInt("humanHoeActions");
            humanDaggerActions = nbt.getInt("humanDaggerActions");
            humanPolearmActions = nbt.getInt("humanPolearmActions");
            humanBluntActions = nbt.getInt("humanBluntActions");
            humanGenericMeleeActions = nbt.getInt("humanGenericMeleeActions");
            lastTarget = nbt.hasUUID("lastTarget") ? nbt.getUUID("lastTarget") : null;
            previousFoodLevel = nbt.getInt("previousFoodLevel");
            forestSpirits = nbt.getInt("forestSpirits");
            spiritCooldowns = nbt.getIntArray("spiritCooldowns");
            if (spiritCooldowns.length != 3) {
                spiritCooldowns = new int[3];
            }
        }

        public void copyFrom(PlayerVariables other) {
            dwarf = other.dwarf;
            elf = other.elf;
            orc = other.orc;
            merfolk = other.merfolk;
            dragon = other.dragon;
            selected = other.selected;
            fairy = other.fairy;
            halfdead = other.halfdead;
            serpentin = other.serpentin;
            werewolf = other.werewolf;
            aracha = other.aracha;
            gargoyle = other.gargoyle;
            human = other.human;
            humanMiningActions = other.humanMiningActions;
            humanRangedActions = other.humanRangedActions;
            humanSwordActions = other.humanSwordActions;
            humanAxeActions = other.humanAxeActions;
            humanTridentActions = other.humanTridentActions;
            humanPickaxeActions = other.humanPickaxeActions;
            humanShovelActions = other.humanShovelActions;
            humanHoeActions = other.humanHoeActions;
            humanDaggerActions = other.humanDaggerActions;
            humanPolearmActions = other.humanPolearmActions;
            humanBluntActions = other.humanBluntActions;
            humanGenericMeleeActions = other.humanGenericMeleeActions;
            raceCapacityInitialized = other.raceCapacityInitialized;
            fervorStacks = other.fervorStacks;
            lastTarget = other.lastTarget;
            previousFoodLevel = other.previousFoodLevel;
            forestSpirits = other.forestSpirits;
            System.arraycopy(other.spiritCooldowns, 0, spiritCooldowns, 0, 3);
        }

        public String getRaceName() {
            if (dwarf) return "dwarf";
            if (elf) return "elf";
            if (orc) return "orc";
            if (merfolk) return "merfolk";
            if (dragon) return "dragon";
            if (fairy) return "fairy";
            if (halfdead) return "halfdead";
            if (serpentin) return "serpentin";
            if (werewolf) return "werewolf";
            if (aracha) return "aracha";
            if (gargoyle) return "gargoyle";
            if (human) return "human";
            return "none";
        }
    }

    public static class PlayerVariablesSyncMessage {
        private final int target;
        private final PlayerVariables data;

        public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
            this.data = new PlayerVariables();
            this.data.readNBT(buffer.readNbt());
            this.target = buffer.readInt();

            this.data.fervorStacks = buffer.readInt();
            if (buffer.readBoolean()) {
                this.data.lastTarget = buffer.readUUID();
            } else {
                this.data.lastTarget = null;
            }

            this.data.raceCapacityInitialized = buffer.readBoolean();
            this.data.previousFoodLevel = buffer.readInt();
            this.data.forestSpirits = buffer.readInt();
            for (int i = 0; i < 3; i++) {
                this.data.spiritCooldowns[i] = buffer.readInt();
            }
        }

        public PlayerVariablesSyncMessage(PlayerVariables data, int entityid) {
            this.data = data;
            this.target = entityid;
        }

        public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeNbt(message.data.writeNBT());
            buffer.writeInt(message.target);
            buffer.writeInt(message.data.fervorStacks);
            buffer.writeBoolean(message.data.lastTarget != null);
            if (message.data.lastTarget != null) {
                buffer.writeUUID(message.data.lastTarget);
            }
            buffer.writeBoolean(message.data.raceCapacityInitialized);
            buffer.writeInt(message.data.previousFoodLevel);
            buffer.writeInt(message.data.forestSpirits);
            for (int i = 0; i < 3; i++) {
                buffer.writeInt(message.data.spiritCooldowns[i]);
            }
        }

        public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getDirection().getReceptionSide().isServer()) {
                    Entity targetEntity = Minecraft.getInstance().player.level().getEntity(message.target);
                    if (targetEntity instanceof Player targetPlayer) {
                        PlayerVariables variables = getPlayerVariables(targetPlayer);
                        variables.copyFrom(message.data);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}





package net.simpleraces.procedures.race;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class RaceTargetingHelper {
    private static final String[] OWNER_UUID_KEYS = {
            "Owner", "OwnerUUID", "owner", "ownerUUID",
            "Caster", "CasterUUID", "caster", "casterUUID",
            "Summoner", "SummonerUUID", "summoner", "summonerUUID",
            "Creator", "CreatorUUID", "creator", "creatorUUID"
    };
    private static final String TEAMMOD_MANAGER_CLASS = "com.mom.teammod.TeamManager";
    @Nullable
    private static Method teamModFriendlyFireMethod;
    private static boolean teamModLookupDone = false;

    private RaceTargetingHelper() {
    }

    public static boolean isOwnersPetOrSummon(Player player, Entity target) {
        if (player == null || target == null) {
            return false;
        }

        return readOwnerUuid(resolveRootEntity(target).getPersistentData())
                .map(player.getUUID()::equals)
                .orElseGet(() -> {
                    Entity owner = resolveOwner(target);
                    return owner != null && owner.getUUID().equals(player.getUUID());
                });
    }

    public static boolean canApplyNegativeRaceEffect(@Nullable Entity source, @Nullable Entity target) {
        if (source == null || target == null) {
            return true;
        }

        Entity sourceRoot = resolveRootEntity(source);
        Entity targetRoot = resolveRootEntity(target);

        if (sourceRoot.getUUID().equals(targetRoot.getUUID())) {
            return false;
        }

        Boolean teamModBlocked = isBlockedByTeamModFriendlyFire(sourceRoot, targetRoot);
        if (Boolean.TRUE.equals(teamModBlocked)) {
            return false;
        }

        Team sourceTeam = sourceRoot.getTeam();
        Team targetTeam = targetRoot.getTeam();
        if (sourceTeam != null && targetTeam != null
                && sourceTeam.isAlliedTo(targetTeam)
                && (!sourceTeam.isAllowFriendlyFire() || !targetTeam.isAllowFriendlyFire())) {
            return false;
        }

        return true;
    }

    @Nullable
    private static Boolean isBlockedByTeamModFriendlyFire(Entity source, Entity target) {
        Method method = getTeamModFriendlyFireMethod();
        if (method == null) {
            return null;
        }

        try {
            Object result = method.invoke(null, source, target);
            return result instanceof Boolean value ? value : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @Nullable
    private static Method getTeamModFriendlyFireMethod() {
        if (teamModLookupDone) {
            return teamModFriendlyFireMethod;
        }

        teamModLookupDone = true;
        try {
            Class<?> managerClass = Class.forName(TEAMMOD_MANAGER_CLASS);
            teamModFriendlyFireMethod = managerClass.getMethod("isFriendlyFireDisabled", Entity.class, Entity.class);
        } catch (ReflectiveOperationException ignored) {
            teamModFriendlyFireMethod = null;
        }
        return teamModFriendlyFireMethod;
    }

    private static Optional<UUID> readOwnerUuid(CompoundTag tag) {
        for (String key : OWNER_UUID_KEYS) {
            UUID uuid = readUuid(tag, key);
            if (uuid != null) {
                return Optional.of(uuid);
            }
        }
        return Optional.empty();
    }

    private static Entity resolveRootEntity(Entity entity) {
        Entity current = entity;
        Set<UUID> seen = new HashSet<>();

        while (current != null && seen.add(current.getUUID())) {
            Entity owner = resolveImmediateOwner(current);
            if (owner == null) {
                return current;
            }
            current = owner;
        }

        return entity;
    }

    @Nullable
    private static Entity resolveOwner(Entity entity) {
        Entity current = entity;
        Set<UUID> seen = new HashSet<>();

        while (current != null && seen.add(current.getUUID())) {
            Entity owner = resolveImmediateOwner(current);
            if (owner == null) {
                return current == entity ? null : current;
            }
            current = owner;
        }

        return null;
    }

    @Nullable
    private static Entity resolveImmediateOwner(Entity entity) {
        if (entity instanceof TamableAnimal tamable) {
            LivingOwner owner = getTamableOwner(tamable);
            if (owner.entity != null) {
                return owner.entity;
            }
            if (owner.uuid != null) {
                return findEntityByUuid(entity, owner.uuid);
            }
        }

        if (entity instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner != null) {
                return owner;
            }

            UUID ownerUuid = ownable.getOwnerUUID();
            if (ownerUuid != null) {
                return findEntityByUuid(entity, ownerUuid);
            }
        }

        if (entity instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner != null) {
                return owner;
            }
        }

        return readOwnerUuid(entity.getPersistentData())
                .map(uuid -> findEntityByUuid(entity, uuid))
                .orElse(null);
    }

    private static LivingOwner getTamableOwner(TamableAnimal tamable) {
        Entity owner = tamable.getOwner();
        if (owner != null) {
            return new LivingOwner(owner, owner.getUUID());
        }
        UUID uuid = tamable.getOwnerUUID();
        return new LivingOwner(uuid == null ? null : findEntityByUuid(tamable, uuid), uuid);
    }

    @Nullable
    private static Entity findEntityByUuid(Entity context, UUID uuid) {
        if (context.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(uuid);
        }
        return null;
    }

    private static UUID readUuid(CompoundTag tag, String key) {
        if (tag == null || !tag.contains(key)) {
            return null;
        }

        if (tag.hasUUID(key)) {
            return tag.getUUID(key);
        }

        String raw = tag.getString(key);
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private record LivingOwner(@Nullable Entity entity, @Nullable UUID uuid) {
    }
}





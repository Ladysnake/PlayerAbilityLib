package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.PlayerAbilityUpdatedCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class PalInternals {

    private static final Map<Identifier, PlayerAbility> abilities = new HashMap<>();
    private static final Map<Identifier, AbilitySource> sources = new HashMap<>();

    public static void populate(PlayerEntity player, Map<PlayerAbility, AbilityTracker> map) {
        for (PlayerAbility ability : abilities.values()) {
            map.put(ability, ability.createTracker(player));
        }
    }

    public static PlayerAbility getAbility(Identifier id) {
        return abilities.get(id);
    }

    public static PlayerAbility registerAbility(PlayerAbility ability) {
        abilities.put(ability.getId(), ability);
        return ability;
    }

    public static AbilitySource registerSource(Identifier sourceId, Function<Identifier, AbilitySource> factory) {
        return sources.computeIfAbsent(sourceId, factory);
    }

    public static boolean isAbilityRegistered(PlayerAbility ability) {
        return abilities.containsKey(ability.getId());
    }

    public static Event<PlayerAbilityUpdatedCallback> createUpdateEvent() {
        return EventFactory.createArrayBacked(PlayerAbilityUpdatedCallback.class,
            (listeners) -> (player, nowEnabled) -> {
                for (PlayerAbilityUpdatedCallback listener : listeners) {
                    listener.onAbilityUpdated(player, nowEnabled);
                }
            });
    }
}

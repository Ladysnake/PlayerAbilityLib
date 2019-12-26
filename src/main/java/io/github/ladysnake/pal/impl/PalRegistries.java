package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.PlayerAbilityUpdatedCallback;
import io.github.ladysnake.pal.ToggleableAbility;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class PalRegistries {
    static final Map<Identifier, Function<PlayerEntity, ToggleableAbility>> abilityRegistry = new HashMap<>();
    private static final Map<Identifier, Event<PlayerAbilityUpdatedCallback>> events = new HashMap<>();

    public static void registerAbility(Identifier abilityId, Function<PlayerEntity, ToggleableAbility> factory) {
        abilityRegistry.put(abilityId, factory);
    }

    public static boolean isAbilityRegistered(Identifier abilityId) {
        return abilityRegistry.containsKey(abilityId);
    }

    public static Event<PlayerAbilityUpdatedCallback> getOrCreateUpdateEvent(Identifier id) {
        return events.computeIfAbsent(id, i -> EventFactory.createArrayBacked(PlayerAbilityUpdatedCallback.class,
            (listeners) -> (player, nowEnabled) -> {
                for (PlayerAbilityUpdatedCallback listener : listeners) {
                    listener.onAbilityUpdated(player, nowEnabled);
                }
            }));
    }
}

/*
 * PlayerAbilityLib
 * Copyright (C) 2019 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
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

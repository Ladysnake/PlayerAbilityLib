/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2022 Ladysnake
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

import com.google.common.base.Preconditions;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.PlayerAbilityUpdatedCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

public final class PalInternals {

    public static final Logger LOGGER = LogManager.getLogger("PlayerAbilityLib");
    private static boolean alwaysLogTamperWarnings = true;
    private static boolean hasLoggedTamperWarning = false;

    private static final Map<Identifier, PlayerAbility> abilities = new HashMap<>();
    private static final Map<Identifier, AbilitySource> sources = new HashMap<>();

    public static void populate(PlayerEntity player, Map<PlayerAbility, AbilityTracker> map) {
        for (PlayerAbility ability : abilities.values()) {
            map.put(ability, ability.createTracker(player));
        }
    }

    public static void logTamperWarning(PlayerAbility ability, boolean enabled, boolean expected) {
        if (alwaysLogTamperWarnings || !hasLoggedTamperWarning) {
            PalInternals.LOGGER.warn("Player ability {} was updated externally (expected {}, was {}).",
                ability.getId(), expected ? "enabled" : "disabled", enabled ? "enabled" : "disabled", new RuntimeException("stacktrace"));
            hasLoggedTamperWarning = true;
        }
    }

    public static void loadConfig() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("pal.properties");
        Properties props = new Properties();
        props.put("alwaysLogTamperWarnings", "true");

        if (Files.exists(configFile)) {
            try (Reader reader = Files.newBufferedReader(configFile)) {
                props.load(reader);
                alwaysLogTamperWarnings = Boolean.parseBoolean(props.getProperty("alwaysLogTamperWarnings"));
                return;
            } catch (IOException e) {
                PalInternals.LOGGER.error("Failed to load config file", e);
            }
        }

        try (Writer writer = Files.newBufferedWriter(configFile)) {
            props.store(writer, String.join("\n",
                "PlayerAbilityLib configuration file",
                "",
                "If alwaysLogTamperWarnings is set to false, external update messages will be logged only once per game session"
            ));
        } catch (IOException e) {
            PalInternals.LOGGER.error("Failed to create config file", e);
        }
    }

    @Contract("null -> null; !null -> _")
    public static @Nullable PlayerAbility getAbility(@Nullable Identifier id) {
        return abilities.get(id);
    }

    public static synchronized PlayerAbility registerAbility(PlayerAbility ability) {
        if (abilities.containsKey(ability.getId())) {
            throw new IllegalStateException("An ability was already registered with the id " + ability);
        }
        abilities.put(ability.getId(), ability);
        return ability;
    }

    public static AbilitySource getSource(@Nullable Identifier sourceId) {
        return sources.get(sourceId);
    }

    public static AbilitySource registerSource(Identifier sourceId, @Nullable Integer priority, BiFunction<Identifier, Integer, AbilitySource> factory) {
        Preconditions.checkNotNull(sourceId);
        AbilitySource existing = sources.get(sourceId);

        if (existing == null) {
            synchronized (sources) {
                existing = sources.get(sourceId);
                // off-chance that someone modifies the map concurrently
                if (existing == null) {
                    AbilitySource source = factory.apply(sourceId, priority == null ? AbilitySource.DEFAULT : priority);
                    sources.put(sourceId, source);
                    return source;
                }
            }
        }

        if (priority != null && existing.getPriority() != priority) {
            throw new IllegalStateException(sourceId + " has been registered twice with differing priorities: " + existing.getPriority() + ", " + priority);
        }

        return existing;
    }

    public static boolean isAbilityRegistered(Identifier abilityId) {
        return abilityId != null && abilities.containsKey(abilityId);
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

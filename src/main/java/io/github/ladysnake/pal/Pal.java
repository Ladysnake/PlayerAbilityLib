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
package io.github.ladysnake.pal;

import io.github.ladysnake.pal.impl.PalInternals;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

/**
 * Player Ability Lib's main class. Provides static methods to interact with player abilities.
 *
 * <p> Use example for a standard flight suit:
 * <pre><code>
 *  public static final AbilitySource SUIT_FLIGHT = Pal.getAbilitySource("flightsuit", "suit_flight");
 *
 *  public void onEquip(PlayerEntity player) {
 *      if (!player.world.isClient) {
 *          SUIT_FLIGHT.grantTo(player, VanillaAbilities.ALLOW_FLYING);
 *      }
 *  }
 *
 *  public void onUnequip(PlayerEntity player) {
 *      if (!player.world.isClient) {
 *          SUIT_FLIGHT.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
 *      }
 *  }
 * </code></pre>
 * Calling the {@code onEquip} and {@code onUnequip} methods of this example
 * at the right time is left as an exercise to the reader.
 *
 * @see AbilitySource
 * @see PlayerAbility
 * @see VanillaAbilities
 */
public final class Pal implements ModInitializer {

    /**
     * Registers a player ability that mods can interact with.
     *
     * <p> A registered {@link AbilityTracker} will be accessible using {@link PlayerAbility#getTracker(PlayerEntity)}
     * with the given {@code ability}. Said ability instance is attached to every player at construction time using
     * the given {@code factory}.
     *
     * @param namespace namespace of this ability's identifier
     * @param path path of this ability's identifier
     * @param factory   a factory to create {@code ToggleableAbility} instances for every player
     * @apiNote abilities must be registered during initialization.
     * @see SimpleAbilityTracker
     */
    public static PlayerAbility registerAbility(String namespace, String path, BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> factory) {
        return registerAbility(new Identifier(namespace, path), factory);
    }

    /**
     * Registers a player ability that mods can interact with.
     *
     * <p> A registered {@link AbilityTracker} will be accessible using {@link PlayerAbility#getTracker(PlayerEntity)}
     * with the given {@code abilityId}. Said ability instance is attached to every player at construction time using
     * the given {@code factory}.
     *
     * @param abilityId a unique identifier for the ability
     * @param factory   a factory to create {@code ToggleableAbility} instances for every player
     * @apiNote abilities must be registered during initialization.
     * @see SimpleAbilityTracker
     */
    public static PlayerAbility registerAbility(Identifier abilityId, BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> factory) {
        return PalInternals.registerAbility(new PlayerAbility(abilityId, factory));
    }

    public static AbilitySource getAbilitySource(String namespace, String path) {
        return getAbilitySource(new Identifier(namespace, path));
    }

    public static AbilitySource getAbilitySource(Identifier abilitySourceId) {
        return PalInternals.registerSource(abilitySourceId, AbilitySource::new);
    }

    /**
     * Returns {@code true} if an ability has been registered with the given {@code abilityId}.
     *
     * @param abilityId a unique ability identifier to check for
     * @return {@code true} if the ability has been registered
     * @see #registerAbility(Identifier, BiFunction)
     */
    public static boolean isAbilityRegistered(PlayerAbility abilityId) {
        return PalInternals.isAbilityRegistered(abilityId);
    }

    @Override
    public void onInitialize() {
        new VanillaAbilities();
    }
}

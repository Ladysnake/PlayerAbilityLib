/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2020 Ladysnake
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
import net.minecraft.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
     * Convenience overload for {@link #getAbilitySource(Identifier)} that creates an {@link Identifier}
     * from the given {@code namespace} and {@code path}.
     *
     * @param namespace the identifier namespace for the ability source
     * @param name      the identifying name of the ability source
     * @return an {@code AbilitySource} for the constructed id
     * @throws NullPointerException if any of the arguments is null
     */
    public static AbilitySource getAbilitySource(String namespace, String name) {
        return getAbilitySource(new Identifier(namespace, name));
    }

    /**
     * Returns an {@code AbilitySource} corresponding to the given {abilitySourceId}.
     *
     * <p> Calling this method multiple times with equivalent identifiers results
     * in a single instance being returned. More formally, for any two Identifiers
     * {@code i1} and {@code i2}, {code getAbilitySource(i1) == getAbilitySource(i1)}
     * is true if and only if {@code i1.equals(i2)}.
     *
     * @param abilitySourceId a unique identifier for the ability source
     * @return an {@code AbilitySource} for {@code abilitySourceId}
     * @throws NullPointerException if {@code abilitySourceId} is null
     */
    public static AbilitySource getAbilitySource(Identifier abilitySourceId) {
        return PalInternals.registerSource(abilitySourceId, AbilitySource::new);
    }

    /**
     * Convenience overload for {@link #registerAbility(Identifier, BiFunction)} that creates an {@link Identifier}
     * from the given {@code namespace} and {@code path}.
     *
     * @param namespace namespace of this ability's identifier
     * @param path      path of this ability's identifier
     * @param factory   a factory to create {@code ToggleableAbility} instances for every player
     * @return a {@code PlayerAbility} registered with the constructed id
     * @see #registerAbility(String, String, BiFunction)
     * @throws NullPointerException if any of the arguments is null
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
     * @throws IllegalStateException if {@code abilityId}
     * @apiNote abilities must be registered during initialization.
     * @see SimpleAbilityTracker
     * @throws NullPointerException if any of the arguments is null
     */
    public static PlayerAbility registerAbility(Identifier abilityId, BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> factory) {
        return PalInternals.registerAbility(new PlayerAbility(Objects.requireNonNull(abilityId), Objects.requireNonNull(factory)));
    }

    /**
     * Returns {@code true} if an ability has been registered with the given {@code abilityId}.
     *
     * @param abilityId a unique ability identifier to check for
     * @return {@code true} if the ability has been registered
     * @see #registerAbility(Identifier, BiFunction)
     */
    public static boolean isAbilityRegistered(@Nullable Identifier abilityId) {
        return PalInternals.isAbilityRegistered(abilityId);
    }

    /**
     * Returns a lazy supplier for a player ability registered with the given {@code abilityId}.
     *
     * @param abilityId the id used to register the ability
     * @return a lazy supplier for a player ability registered with the given {@code abilityId}
     * @throws NullPointerException if {@code abilityId} is null
     */
    public static Lazy<PlayerAbility> provideRegisteredAbility(Identifier abilityId) {
        return new Lazy<>(() -> Objects.requireNonNull(PalInternals.getAbility(abilityId), abilityId + " has not been registered"));
    }

    @Override
    public void onInitialize() {
        new VanillaAbilities();
    }
}

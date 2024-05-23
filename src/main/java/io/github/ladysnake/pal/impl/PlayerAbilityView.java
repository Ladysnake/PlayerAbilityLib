/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2024 Ladysnake
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

import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

/**
 * A view for mods to interact with a player's abilities.
 */
public interface PlayerAbilityView {

    /**
     * Returns a {@link PlayerAbilityView} for the given {@code player}.
     *
     * <p> The returned view can be used to query and update the status of
     * any previously {@link Pal#registerAbility(Identifier, BiFunction) registered} ability.
     *
     * @param player an initialized player to get the abilities of
     * @return a view for the player's abilities
     */
    static PlayerAbilityView of(PlayerEntity player) {
        if (player.getWorld().isClient) {
            throw new IllegalStateException("Player abilities must be accessed from the logical server (check !world.isClient)");
        }
        return (PlayerAbilityView) player;
    }

    Iterable<PlayerAbility> listPalAbilities();

    /**
     * Gets a handle for a given ability in the form of a {@link AbilityTracker}
     *
     * @param abilityId the unique identifier of the ability
     * @return a toggleable handle for the ability
     * @throws IllegalArgumentException if {@code abilityId} has not been registered
     * @see VanillaAbilities
     * @see Pal#isAbilityRegistered(Identifier)
     */
    AbilityTracker get(PlayerAbility abilityId);

    /**
     * Refreshes each ability of this player.
     *
     * @param syncVanilla {@code true} if vanilla {@link PlayerAbilities} should be synchronized as a result of this call
     * @see AbilityTracker#refresh(boolean)
     */
    void refreshAllPalAbilities(boolean syncVanilla);

}

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
package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.*;

/**
 * Custom abilities
 */
public final class PalTestAbilities {
    /** An ability that temporarily blocks creative flight when active */
    public static final PlayerAbility LIMIT_FLIGHT = Pal.registerAbility(PalTest.id("limit_flight"), SimpleAbilityTracker::new);

    public static void init() {
        PlayerAbilityUpdatedCallback.event(LIMIT_FLIGHT).register((player, nowEnabled) ->
            // Refresh the flight tracker, will call PlayerAbilityEnableCallback for every source
            VanillaAbilities.ALLOW_FLYING.getTracker(player).refresh(true));
        PlayerAbilityEnableCallback.EVENT.register((player, ability, abilitySource) -> {
            if (ability == VanillaAbilities.ALLOW_FLYING) {
                return !LIMIT_FLIGHT.isEnabledFor(player);  // block flight
            }
            return true;
        });
    }
}

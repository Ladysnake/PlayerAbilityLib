/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2021 Ladysnake
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

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Callback interface for receiving ability update events.
 *
 * @see PlayerAbilityEnableCallback
 */
@FunctionalInterface
public interface PlayerAbilityUpdatedCallback {
    static Event<PlayerAbilityUpdatedCallback> event(PlayerAbility ability) {
        return ability.updateEvent;
    }

    /**
     * Called when the tracked ability's state gets updated on the given player.
     *
     * @param player     the player on which the ability has been updated
     * @param nowEnabled the new value returned by {@link PlayerAbility#isEnabledFor(PlayerEntity)}
     */
    void onAbilityUpdated(PlayerEntity player, boolean nowEnabled);
}

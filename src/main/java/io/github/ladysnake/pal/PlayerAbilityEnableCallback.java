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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface PlayerAbilityEnableCallback {

    Event<PlayerAbilityEnableCallback> EVENT = EventFactory.createArrayBacked(PlayerAbilityEnableCallback.class,
        (listeners) -> (player, abilityId, abilitySource) -> {
            for (PlayerAbilityEnableCallback listener : listeners) {
                if (!listener.allow(player, abilityId, abilitySource)) {
                    return false;
                }
            }
            return true;
        });

    boolean allow(PlayerEntity player, PlayerAbility abilityId, AbilitySource abilitySource);
}

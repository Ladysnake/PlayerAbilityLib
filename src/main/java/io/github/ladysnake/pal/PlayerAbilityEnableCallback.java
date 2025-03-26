/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2025 Ladysnake
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

/**
 * Callback interface for receiving ability enabling events.
 *
 * @see PlayerAbilityUpdatedCallback
 */
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

    /**
     * Called when an {@code AbilitySource} attempts to enable a {@code PlayerAbility} on a player.
     *
     * <p> The callback may return {@code false} to reject the activation,
     * keeping the ability in its previous activation state.
     * Some abilities may stay {@linkplain PlayerAbility#isEnabledFor(PlayerEntity) enabled}
     * despite all sources of activation being rejected, because of intrinsic providers like the player's gamemode.
     *
     * @param player        the affected player
     * @param ability       the ability being enabled
     * @param abilitySource the source of the ability
     * @return {@code true} to let {@code abilitySource} enable the ability on {@code player},
     * and {@code false} to prevent the ability from being enabled.
     */
    boolean allow(PlayerEntity player, PlayerAbility ability, AbilitySource abilitySource);
}

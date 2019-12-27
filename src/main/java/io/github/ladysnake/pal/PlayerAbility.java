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
import io.github.ladysnake.pal.impl.PlayerAbilityView;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

/**
 * An ability that may be granted to a player.
 *
 * <p> Abilities are at their core flags that can be enabled
 * on specific players. They represent traits that can expand or limit
 * the possible actions for those players. An ability can be granted to
 * a player by one or more {@link AbilitySource}.
 *
 * <p> Instances of this class can be obtained through the methods
 * {@link Pal#registerAbility(String, String, BiFunction)} or {@link Pal#registerAbility(Identifier, BiFunction)}.
 * Instances obtained that way are safe to compare by identity.
 *
 * @see VanillaAbilities
 * @see AbilityTracker
 */
public final class PlayerAbility {
    final Event<PlayerAbilityUpdatedCallback> updateEvent = PalInternals.createUpdateEvent();
    private final BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> trackerFactory;
    private final Identifier id;

    /**
     * @see Pal#registerAbility(String, String, BiFunction)
     * @see Pal#registerAbility(Identifier, BiFunction)
     */
    PlayerAbility(Identifier id, BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> trackerFactory) {
        this.id = id;
        this.trackerFactory = trackerFactory;
    }

    /**
     * Returns the tracker for this ability on the given player.
     *
     * @param player the player to get an {@code AbilityTracker} from
     * @return the tracker for this ability on the given player.
     */
    public AbilityTracker getTracker(PlayerEntity player) {
        return PlayerAbilityView.of(player).get(this);
    }

    /**
     * Returns {@code true} if this ability is currently enabled for the given {@code player}.
     */
    public boolean isEnabledFor(PlayerEntity player) {
        return this.getTracker(player).isEnabled();
    }

    public AbilityTracker createTracker(PlayerEntity player) {
        return this.trackerFactory.apply(this, player);
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PlayerAbility[" + this.id + "]";
    }
}

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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * A source for a {@link PlayerAbility}.
 *
 * <p> Ability sources are the main way for mods to enable or disable abilities
 * on specific players. They are uniquely identified, and each represent a mean
 * for a player to activate an ability (eg. a potion and a jetpack would use
 * 2 ability sources, even if they belong to the same mod).
 *
 * <p> Instances of this class can be obtained through the methods
 * {@link Pal#getAbilitySource(Identifier)} or {@link Pal#getAbilitySource(String, String)}.
 * Instances obtained that way are safe to compare by identity.
 */
public final class AbilitySource {
    private final Identifier id;

    /**
     * @see Pal#getAbilitySource(Identifier)
     */
    AbilitySource(Identifier id) {
        this.id = id;
    }

    /**
     * Grants an ability to a player.
     *
     * <p> If no {@link PlayerAbilityEnableCallback} disallows the ability's activation,
     * {@code ability.isEnabled()} will return {@code true} after calling this method.
     *
     * @param player  the player on which to enable the ability
     * @param ability the ability to enable
     */
    public void grantTo(PlayerEntity player, PlayerAbility ability) {
        ability.getTracker(player).addSource(this);
    }

    /**
     * Revokes an ability from a player.
     *
     * <p> If no other ability source currently exist for the ability,
     * {@code ability.isEnabled()} will return {@code false} after calling this method.
     *
     * @param player  the player to revoke the ability from
     * @param ability the ability to revoke
     */
    public void revokeFrom(PlayerEntity player, PlayerAbility ability) {
        ability.getTracker(player).removeSource(this);
    }

    /**
     * Returns {@code true} if this ability source is currently granting {@code player}
     * the given {@code ability}.
     *
     * @param player the player to check on
     * @param ability an ability that may be granted by this source
     * @return {@code true} if this grants {@code player} the {@code ability}
     */
    public boolean grants(PlayerEntity player, PlayerAbility ability) {
        return ability.getTracker(player).isGrantedBy(this);
    }

    /**
     * Returns the identifier used to create this {@code AbilitySource}.
     *
     * <p> The returned identifier is unique and can be passed to {@link Pal#getAbilitySource(Identifier)}
     * to retrieve this instance.
     *
     * @return the identifier wrapped by this {@code AbilitySource}
     */
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AbilitySource@" + this.id;
    }

}

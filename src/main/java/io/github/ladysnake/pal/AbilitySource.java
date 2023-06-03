/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2023 Ladysnake
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
import org.jetbrains.annotations.NotNull;

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
public final class AbilitySource implements Comparable<AbilitySource> {
    /** A standard priority for ability sources that are free to use (e.g. active potion effects) */
    public static final int FREE = 2000;
    /** A standard priority for ability sources that cost some renewable resource to use (e.g. stamina) */
    public static final int RENEWABLE = 1000;
    /** The default priority for ability sources */
    public static final int DEFAULT = 0;
    /** A standard priority for ability sources that cost some non-renewable resource to use (e.g. item fuel) */
    public static final int CONSUMABLE = -1000;

    private final Identifier id;
    private final int priority;

    /**
     * @see Pal#getAbilitySource(String, String)
     * @see Pal#getAbilitySource(Identifier)
     * @see Pal#getAbilitySource(Identifier, int)
     */
    AbilitySource(Identifier id, int priority) {
        this.id = id;
        this.priority = priority;
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

    /**
     * Returns the priority used to create this {@code AbilitySource}.
     *
     * <p>If no priority was specified during registration, the {@linkplain #DEFAULT default priority} is used.
     *
     * @return the priority assigned to this {@code AbilitySource}
     * @see Pal#getAbilitySource(Identifier, int)
     * @see #FREE
     * @see #RENEWABLE
     * @see #DEFAULT
     * @see #CONSUMABLE
     */
    public int getPriority() {
        return priority;
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
     * @param player  the player to check on
     * @param ability an ability that may be granted by this source
     * @return {@code true} if this grants {@code player} the {@code ability}
     */
    public boolean grants(PlayerEntity player, PlayerAbility ability) {
        return ability.getTracker(player).isGrantedBy(this);
    }

    /**
     * Returns {@code true} if this ability source is the one actively granting {@code ability}
     * to {@code player}.
     *
     * <p>At most one {@code AbilitySource} can return {@code true} for a given player and ability.
     * When more than one source is granting the same ability, the one considered active is the highest
     * one according to {@link #compareTo(AbilitySource)}. This means that an {@code AbilitySource} with
     * a higher {@linkplain #getPriority() priority} is more likely to be considered "active".
     *
     * <p>This method can be used to check if side effects should trigger for a specific ability source,
     * e.g. to avoid wasting jetpack fuel when multiple sources are giving flight at the same time.
     *
     * @param player  the player to check on
     * @param ability an ability that may be granted by this source
     * @return {@code true} if this ability source is the one actively granting {@code ability}
     * to {@code player}, {@code false} otherwise.
     * @since 1.4.0
     */
    public boolean isActivelyGranting(PlayerEntity player, PlayerAbility ability) {
        return ability.getTracker(player).getActiveSource() == this;
    }

    /**
     * Compares two ability sources.
     *
     * <p>The comparison is based on the assigned {@linkplain #getPriority() priority},
     * with ties being resolved arbitrarily. Priorities are compared using their natural ordering,
     * meaning a higher priority causes the source to be considered higher.
     *
     * @param o the source to be compared.
     * @return the value {@code 0} if the argument source is equal to
     * this source; a value less than {@code 0} if this source
     * is considered less than the source argument; and a
     * value greater than {@code 0} if this source is
     * considered greater than the string argument.
     * @implNote the current way to resolve ties is through identifier comparison.
     * @since 1.4.0
     */
    @Override
    public int compareTo(@NotNull AbilitySource o) {
        int priorityOrder = Integer.compare(this.priority, o.priority);
        return priorityOrder != 0 ? priorityOrder : this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "AbilitySource@" + this.id + "+" + this.priority;
    }

}

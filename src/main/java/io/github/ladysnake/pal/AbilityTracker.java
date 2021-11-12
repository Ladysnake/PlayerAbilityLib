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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A tracker for a player ability that can be turned on or off.
 *
 * @apiNote this interface is intended to be implemented by API consumers that
 * provide new {@linkplain PlayerAbility abilities}.
 */
public interface AbilityTracker {

    /**
     * Adds a source for this tracker's ability.
     *
     * <p> If no {@link PlayerAbilityEnableCallback} disallows the ability's activation,
     * {@link #isEnabled()} will return {@code true} after calling this method.
     *
     * @param abilitySource the source granting the ability
     */
    @Contract(mutates = "this")
    void addSource(AbilitySource abilitySource);

    /**
     * Removes a source for this tracker's ability.
     *
     * <p> If no other ability source currently exist for the ability,
     * {@link #isEnabled()} will return {@code false} after calling this method.
     *
     * @param abilitySource the source granting the ability
     */
    @Contract(mutates = "this")
    void removeSource(AbilitySource abilitySource);

    /**
     * Returns {@code true} if this tracker's ability is currently granted by the given {@code abilitySource}.
     *
     * @param abilitySource the source granting the ability
     * @return {@code true} if this tracker's ability is provided by {@code abilitySource}
     */
    @Contract(pure = true)
    boolean isGrantedBy(AbilitySource abilitySource);

    /**
     * Returns the ability source actively granting this tracker's ability.
     *
     * <p>The active source is the one considered highest among the sources currently granting this tracker's ability,
     * according to {@link AbilitySource#compareTo(AbilitySource)}.
     * This means that an {@code AbilitySource} with
     * a higher {@linkplain AbilitySource#getPriority() priority} is more likely to be considered "active".
     * .
     *
     * @return the active source, or {@code null} if no source is granting the ability
     * @see Pal#getAbilitySource(Identifier, int)
     * @see AbilitySource#isActivelyGranting(PlayerEntity, PlayerAbility)
     * @see AbilitySource#compareTo(AbilitySource)
     * @since 1.4.0
     */
    @Nullable AbilitySource getActiveSource();

    /**
     * Returns {@code true} if this tracker's ability is currently enabled.
     *
     * <p> An ability may be enabled even if it is not provided by any ability source.
     * For example, most {@link VanillaAbilities} are intrinsically provided by some gamemodes.
     *
     * @return {@code true} if this ability is enabled
     */
    @Contract(pure = true)
    boolean isEnabled();

    /**
     * Refreshes this ability tracker.
     *
     * <p> For vanilla abilities, updating can be batched for grouped refreshes.
     * When that is the case, {@code syncVanilla} can be made {@code false} to avoid redundant
     * packets.
     *
     * @param syncVanilla {@code true} if vanilla abilities should be synchronized as a result of this call
     */
    void refresh(boolean syncVanilla);

    /**
     * Saves this {@code AbilityTracker} in a serialized form to {@code tag}.
     *
     * @param tag the tag to write to
     */
    @Contract(mutates = "param")
    void save(NbtCompound tag);

    /**
     * Loads a serialized form of an {@code AbilityTracker} from {@code tag} into this object.
     *
     * @param tag the tag to read from
     */
    @Contract(mutates = "this")
    void load(NbtCompound tag);
}

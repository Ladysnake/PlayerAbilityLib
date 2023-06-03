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

import io.github.ladysnake.pal.impl.PalInternals;
import io.github.ladysnake.pal.impl.VanillaAbilityTracker;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class provides a basic implementation of the {@code AbilityTracker}
 * interface, to either be used directly or minimize the effort required to implement the interface.
 *
 * <p> To implement a tracker for an externally stored ability, the programmer needs only
 * to extend this class and override the {@link #updateState(boolean)} and {@link #isEnabled()} methods.
 *
 * <p> To implement a tracker for a synchronized ability, the programmer needs to extend this class
 * and override the {@link #sync()} method with an implementation that triggers some packet sending mechanism.
 * @see AbilityTracker
 */
public class SimpleAbilityTracker implements AbilityTracker {
    protected final PlayerEntity player;
    protected final SortedSet<AbilitySource> abilitySources = new TreeSet<>();
    protected final PlayerAbility ability;

    public SimpleAbilityTracker(PlayerAbility ability, PlayerEntity player) {
        this.ability = ability;
        this.player = player;
    }

    @Override
    public void addSource(AbilitySource abilitySource) {
        boolean wasEmpty = this.abilitySources.isEmpty();
        if (this.abilitySources.add(abilitySource) && wasEmpty) {
            if (PlayerAbilityEnableCallback.EVENT.invoker().allow(this.player, this.ability, abilitySource)) {
                this.updateState(true);
                this.sync();
            }
        }
    }

    @Override
    public void removeSource(AbilitySource abilitySource) {
        if (this.abilitySources.remove(abilitySource) && this.abilitySources.isEmpty()) {
            this.updateState(false);
            this.sync();
        }
    }

    @Override
    public boolean isGrantedBy(AbilitySource abilitySource) {
        return this.abilitySources.contains(abilitySource);
    }

    @Override
    public AbilitySource getActiveSource() {
        return this.abilitySources.last();
    }

    @Override
    public void refresh(boolean syncVanilla) {
        this.updateState(this.shouldBeEnabled());
        if (syncVanilla || !(this instanceof VanillaAbilityTracker)) {
            this.sync();
        }
    }

    /**
     * Returns {@code true} if this tracker's ability should be enabled.
     *
     * <p> This is independent of the actual value returned by {@link #isEnabled()}
     * and may be used to update the latter.
     *
     * @return {@code true} if the tracked ability should currently be enabled
     */
    protected boolean shouldBeEnabled() {
        for (AbilitySource abilitySource : this.abilitySources) {
            if (PlayerAbilityEnableCallback.EVENT.invoker().allow(this.player, this.ability, abilitySource)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void save(NbtCompound tag) {
        NbtList list = new NbtList();
        for (AbilitySource abilitySource : this.abilitySources) {
            list.add(NbtString.of(abilitySource.getId().toString()));
        }
        tag.put("ability_sources", list);
    }

    @Override
    public void load(NbtCompound tag) {
        NbtList list = tag.getList("ability_sources", NbtType.STRING);
        for (int i = 0; i < list.size(); i++) {
            AbilitySource source = PalInternals.getSource(Identifier.tryParse(list.getString(i)));
            if (source != null) {
                this.addSource(source);
            } else {
                PalInternals.LOGGER.warn("Unknown ability source {} attached to {} for {}", list.getString(i), this.player, this.ability);
            }
        }
    }

    /**
     * Updates the state of this tracker's ability.
     *
     * @param enabled {@code true} if the ability should be enabled, {@code false} if it should be disabled
     */
    protected void updateState(boolean enabled) {
        PlayerAbilityUpdatedCallback.event(this.ability).invoker().onAbilityUpdated(this.player, enabled);
    }

    /**
     * Synchronizes this tracker's ability with relevant clients.
     */
    protected void sync() {
        // NO-OP
    }

    @Override
    public boolean isEnabled() {
        return !this.abilitySources.isEmpty();
    }
}

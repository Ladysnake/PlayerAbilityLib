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
package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.SimpleAbilityTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.function.Predicate;

public final class VanillaAbilityTracker extends SimpleAbilityTracker {
    private final AbilitySetter setter;
    private final Predicate<PlayerAbilities> getter;

    public VanillaAbilityTracker(PlayerAbility abilityId, PlayerEntity player, AbilitySetter setter, Predicate<PlayerAbilities> getter) {
        super(abilityId, player);
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    protected void updateState(boolean enabled) {
        super.updateState(enabled);
        updateBacking(enabled);
    }

    private void updateBacking(boolean enabled) {
        this.setter.set(getGamemode(this.player), this.player.getAbilities(), enabled);
    }

    @Override
    protected void sync() {
        this.player.sendAbilitiesUpdate();
    }

    @Override
    public boolean isEnabled() {
        return this.getter.test(this.player.getAbilities());
    }

    public void checkConflict() {
        boolean enabled = this.isEnabled();
        this.updateBacking(this.shouldBeEnabled()); // avoid false positives from gamemode changes
        boolean expected = this.isEnabled();
        if (enabled != expected) {
            // Attempt to satisfy both compliant and rogue mods
            // If the external state and the Pal state are conflicting, one of them tries to make this ability enabled
            this.updateState(true);
            PalInternals.LOGGER.warn("Player ability {} was updated externally (expected {}, was {}).",
                this.ability.getId(), expected ? "enabled" : "disabled", enabled ? "enabled" : "disabled", new RuntimeException("stacktrace"));
        }
    }

    private static GameMode getGamemode(PlayerEntity player) {
        if (player.world.isClient) {
            PlayerListEntry playerListEntry = Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerListEntry(player.getGameProfile().getId());
            return playerListEntry != null ? playerListEntry.getGameMode() : GameMode.NOT_SET;
        } else {
            return ((ServerPlayerEntity) player).interactionManager.getGameMode();
        }
    }

    @FunctionalInterface
    public interface AbilitySetter {
        @Contract(mutates = "param2")
        void set(GameMode g, PlayerAbilities abilities, boolean enabled);
    }
}

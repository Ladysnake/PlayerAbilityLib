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
package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.SimpleAbilityTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.function.Predicate;

public final class VanillaAbilityTracker extends SimpleAbilityTracker {
    private final AbilitySetter setter;
    private final Predicate<Abilities> getter;

    public VanillaAbilityTracker(PlayerAbility abilityId, Player player, AbilitySetter setter, Predicate<Abilities> getter) {
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
        this.player.onUpdateAbilities();
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
            PalInternals.logTamperWarning(this.ability, enabled, expected);
        }
    }

    private static GameType getGamemode(Player player) {
        if (player.level().isClientSide()) {
            PlayerInfo playerListEntry = Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(player.getGameProfile().id());
            return playerListEntry != null ? playerListEntry.getGameMode() : GameType.SURVIVAL;
        } else {
            return ((ServerPlayer) player).gameMode.getGameModeForPlayer();
        }
    }

    @FunctionalInterface
    public interface AbilitySetter {
        @Contract(mutates = "param2")
        void set(GameType g, Abilities abilities, boolean enabled);
    }
}

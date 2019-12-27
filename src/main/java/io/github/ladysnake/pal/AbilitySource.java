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

import io.github.ladysnake.pal.impl.PlayerAbilityView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class AbilitySource {
    private final Identifier id;

    AbilitySource(Identifier id) {
        this.id = id;
    }

    public void grantTo(PlayerEntity player, PlayerAbility ability) {
        PlayerAbilityView.of(player).get(ability).grant(this);
    }

    public void revokeFrom(PlayerEntity player, PlayerAbility ability) {
        PlayerAbilityView.of(player).get(ability).revoke(this);
    }

    public boolean grants(PlayerEntity player, PlayerAbility ability) {
        return PlayerAbilityView.of(player).get(ability).isGrantedBy(this);
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AbilitySource[" + this.id + "]";
    }

}

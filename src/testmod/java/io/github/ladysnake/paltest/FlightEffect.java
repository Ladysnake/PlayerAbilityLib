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
package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A status effect that gives creative flight to players
 */
public class FlightEffect extends StatusEffect {
    public static final AbilitySource FLIGHT_POTION = Pal.getAbilitySource(PalTest.id("potion_flight"));

    public FlightEffect(StatusEffectCategory statusEffectType, int color) {
        super(statusEffectType, color);
    }

    @Override
    public void onApplied(LivingEntity effected, int amplifier) {
        super.onApplied(effected, amplifier);
        if (effected instanceof ServerPlayerEntity sp) {
            Pal.grantAbility(sp, VanillaAbilities.ALLOW_FLYING, FLIGHT_POTION);
        }
    }

    public void onRemoved(LivingEntity effected) {
        if (effected instanceof ServerPlayerEntity sp) {
            Pal.revokeAbility(sp, VanillaAbilities.ALLOW_FLYING, FLIGHT_POTION);
        }
    }
}

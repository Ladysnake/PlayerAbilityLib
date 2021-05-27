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
package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class PalTest implements ModInitializer {

    public static Identifier id(String path) {
        return new Identifier("paltest", path);
    }

    @Override
    public void onInitialize() {
        PalTestAbilities.init();
        Registry.register(Registry.ITEM, id("bad_charm"), new BadFlightItem(new Item.Settings()));
        Registry.register(Registry.ITEM, id("flight_charm"), new AbilityToggleItem(new Item.Settings(), VanillaAbilities.ALLOW_FLYING, id("charm_flight")));
        Registry.register(Registry.ITEM, id("kryptonite"), new AbilityToggleItem(new Item.Settings(), PalTestAbilities.LIMIT_FLIGHT, id("kryptonite")));
        Registry.register(Registry.STATUS_EFFECT, id("flight"), new FlightEffect(StatusEffectType.BENEFICIAL, 0xFFFFFF));
    }

}

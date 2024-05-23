/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2024 Ladysnake
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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * An item that toggles flight without going through PAL.
 *
 * <p> PAL will make a best effort attempt to detect issues of this kind,
 * but mod incompatibility is very likely.
 *
 * <p><strong>TL;DR: this is only for testing, do not copy code from here</strong>
 *
 * @see AbilityToggleItem
 */
public class BadFlightItem extends Item {
    public BadFlightItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Direct ability access, issues abound !
            user.getAbilities().allowFlying = !user.getAbilities().allowFlying;
            user.getAbilities().flying &= user.getAbilities().allowFlying;
            user.sendAbilitiesUpdate();
            user.sendMessage(Text.literal("Flight " + (user.getAbilities().allowFlying ? "enabled" : "disabled")), true);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

}

/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2026 Ladysnake
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

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

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
    public BadFlightItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide()) {
            // Direct ability access, issues abound !
            user.getAbilities().mayfly = !user.getAbilities().mayfly;
            user.getAbilities().flying &= user.getAbilities().mayfly;
            user.onUpdateAbilities();
            user.sendOverlayMessage(Component.literal("Flight " + (user.getAbilities().mayfly ? "enabled" : "disabled")));
        }
        return InteractionResult.SUCCESS;
    }

}

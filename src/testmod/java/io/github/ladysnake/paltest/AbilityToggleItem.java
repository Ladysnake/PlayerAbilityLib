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

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * An item that toggles an arbitrary ability using PAL
 */
public class AbilityToggleItem extends Item {
    private PlayerAbility ability;
    private AbilitySource abilitySource;

    public AbilityToggleItem(Settings settings, PlayerAbility abilityId, Identifier abilitySourceId) {
        super(settings);
        this.ability = abilityId;
        this.abilitySource = Pal.getAbilitySource(abilitySourceId);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (abilitySource.grants(user, this.ability)) { // check whether the source is granting the ability
                abilitySource.revokeFrom(user, this.ability); // if it is, revoke it
            } else {
                abilitySource.grantTo(user, this.ability);  // otherwise, grant it
            }
            // Feedback message
            user.sendMessage(new LiteralText("")
                    .append(new LiteralText(abilitySource.getId().toString()).styled(s -> s.withColor(Formatting.YELLOW)))
                    .append(abilitySource.grants(user, this.ability) ? new LiteralText(" added").styled(s -> s.withColor(Formatting.GREEN)) : new LiteralText(" removed").styled(s -> s.withColor(Formatting.RED)))
                    .append(" (")
                    .append(new LiteralText(this.ability.getId().toString()).styled(s -> s.withColor(Formatting.YELLOW)))
                    .append(" is ")
                    .append(ability.isEnabledFor(user) ? new LiteralText("enabled").styled(s -> s.withColor(Formatting.GREEN)) : new LiteralText("disabled").styled(s -> s.withColor(Formatting.RED)))
                    .append(")"), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

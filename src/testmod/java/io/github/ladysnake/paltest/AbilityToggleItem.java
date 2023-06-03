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
package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * An item that toggles an arbitrary ability using PAL
 */
public class AbilityToggleItem extends Item {
    private final PlayerAbility ability;
    private final AbilitySource abilitySource;

    public AbilityToggleItem(Settings settings, PlayerAbility abilityId, Identifier abilitySourceId) {
        super(settings);
        this.ability = abilityId;
        this.abilitySource = Pal.getAbilitySource(abilitySourceId);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity sp) {
            if (abilitySource.grants(sp, this.ability)) { // check whether the source is granting the ability
                abilitySource.revokeFrom(sp, this.ability); // if it is, revoke it
            } else {
                abilitySource.grantTo(sp, this.ability);  // otherwise, grant it
            }
            // Feedback message
            user.sendMessage(Text.literal("")
                    .append(Text.literal(abilitySource.getId().toString()).styled(s -> s.withColor(Formatting.YELLOW)))
                    .append(abilitySource.grants(sp, this.ability) ? Text.literal(" added").styled(s -> s.withColor(Formatting.GREEN)) : Text.literal(" removed").styled(s -> s.withColor(Formatting.RED)))
                    .append(" (")
                    .append(Text.literal(this.ability.getId().toString()).styled(s -> s.withColor(Formatting.YELLOW)))
                    .append(" is ")
                    .append(ability.isEnabledFor(user) ? Text.literal("enabled").styled(s -> s.withColor(Formatting.GREEN)) : Text.literal("disabled").styled(s -> s.withColor(Formatting.RED)))
                    .append(")"), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

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

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * An item that toggles an arbitrary ability using PAL
 */
public class AbilityToggleItem extends Item {
    private final PlayerAbility ability;
    private final AbilitySource abilitySource;

    public AbilityToggleItem(Properties settings, PlayerAbility abilityId, Identifier abilitySourceId) {
        super(settings);
        this.ability = abilityId;
        this.abilitySource = Pal.getAbilitySource(abilitySourceId);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer sp) {
            if (abilitySource.grants(sp, this.ability)) { // check whether the source is granting the ability
                abilitySource.revokeFrom(sp, this.ability); // if it is, revoke it
            } else {
                abilitySource.grantTo(sp, this.ability);  // otherwise, grant it
            }
            // Feedback message
            user.sendSystemMessage(Component.literal("")
                    .append(Component.literal(abilitySource.getId().toString()).withStyle(s -> s.withColor(ChatFormatting.YELLOW)))
                    .append(abilitySource.grants(sp, this.ability) ? Component.literal(" added").withStyle(s -> s.withColor(ChatFormatting.GREEN)) : Component.literal(" removed").withStyle(s -> s.withColor(ChatFormatting.RED)))
                    .append(" (")
                    .append(Component.literal(this.ability.getId().toString()).withStyle(s -> s.withColor(ChatFormatting.YELLOW)))
                    .append(" is ")
                    .append(ability.isEnabledFor(user) ? Component.literal("enabled").withStyle(s -> s.withColor(ChatFormatting.GREEN)) : Component.literal("disabled").withStyle(s -> s.withColor(ChatFormatting.RED)))
                    .append(")"));
        }
        return InteractionResult.SUCCESS;
    }
}

/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2022 Ladysnake
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

import io.github.ladysnake.pal.impl.VanillaAbilityTracker;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.GameMode;

/**
 * Ability identifiers for vanilla {@link PlayerAbilities}
 */
public final class VanillaAbilities {
    /**
     * If enabled, players become invulnerable* to all damage, like in creative and spectator mode.
     *
     * <p> Note: Damage sources that {@link DamageSource#isOutOfWorld() bypass invulnerability}
     * can still damage players with this ability enabled.
     *
     * @see PlayerAbilities#invulnerable
     */
    public static final PlayerAbility INVULNERABLE = Pal.registerAbility("minecraft", "invulnerable",
        (ability, player) -> new VanillaAbilityTracker(ability, player, (g, a, e) -> a.invulnerable = e || !g.isSurvivalLike(), a -> a.invulnerable));

    /**
     * If enabled and {@link #ALLOW_FLYING} is enabled, players become able to move in any direction in the air,
     * like in creative and spectator mode.
     *
     * <p> This ability controls specifically the movement part of flying.
     * As such, it is the only vanilla ability to be controlled by the client.
     * However, in multiplayer, if a player is not allowed to fly from the server's perspective, it will be kicked
     * as soon as it actually starts flying.
     *
     * @see PlayerAbilities#flying
     */
    public static final PlayerAbility FLYING = Pal.registerAbility("minecraft", "flying",
        (ability, player) -> new VanillaAbilityTracker(ability, player, (g, a, e) -> a.flying = e || g == GameMode.SPECTATOR, a -> a.flying));

    /**
     * If enabled, players become able to double tap space to fly, like in creative mode.
     *
     * <p> Enabling this ability is the easiest way to prevent servers kicking players for modded
     * forms of flight.
     *
     * @see PlayerAbilities#allowFlying
     */
    public static final PlayerAbility ALLOW_FLYING = Pal.registerAbility("minecraft", "mayfly",
        (ability, player) -> new VanillaAbilityTracker(ability, player, (g, a, e) -> {
            a.allowFlying = e || !g.isSurvivalLike();
            a.flying &= a.allowFlying;
        }, a -> a.allowFlying));

    /**
     * If enabled, players stop consuming items and experience, and start dealing creative damage.
     *
     * @see PlayerAbilities#creativeMode
     */
    public static final PlayerAbility CREATIVE_MODE = Pal.registerAbility("minecraft", "instabuild",
        (ability, player) -> new VanillaAbilityTracker(ability, player, (g, a, e) -> a.creativeMode = e || g.isCreative(), a -> a.creativeMode));

    /**
     * If enabled, players will be denied world modifications as if they were in adventure or spectator mode.
     *
     * <p> Since players can modify the world by default, this ability is inverted from {@link PlayerAbilities#allowModifyWorld}
     * to let mods deny world modifications to players in survival mode.
     *
     * @see PlayerAbilities#allowModifyWorld
     */
    public static final PlayerAbility LIMIT_WORLD_MODIFICATIONS = Pal.registerAbility("minecraft", "maynotbuild",
        (ability, player) -> new VanillaAbilityTracker(ability, player, (g, a, e) -> a.allowModifyWorld = (!e && !g.isBlockBreakingRestricted()), a -> !a.allowModifyWorld));

    VanillaAbilities() { }
}

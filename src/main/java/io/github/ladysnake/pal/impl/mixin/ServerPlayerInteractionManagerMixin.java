/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2020 Ladysnake
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
package io.github.ladysnake.pal.impl.mixin;

import io.github.ladysnake.pal.impl.PlayerAbilityView;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Unique
    private static final ThreadLocal<Boolean> PAL_FLYING = new ThreadLocal<>();

    @Shadow
    public ServerPlayerEntity player;

    @Inject(
            method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameMode;setAbilities(Lnet/minecraft/entity/player/PlayerAbilities;)V"
            ))
    private void saveFlying(GameMode newMode, GameMode previousMode, CallbackInfo info) {
        PAL_FLYING.set(player.abilities.flying);
    }

    @Inject(
            method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameMode;setAbilities(Lnet/minecraft/entity/player/PlayerAbilities;)V",
                    shift = AFTER
            ))
    private void keepAbilities(GameMode newMode, GameMode previousMode, CallbackInfo info) {
        player.abilities.flying = PAL_FLYING.get(); // will be overruled if unworthy
        PlayerAbilityView.of(this.player).refreshAllPalAbilities(false);
    }
}

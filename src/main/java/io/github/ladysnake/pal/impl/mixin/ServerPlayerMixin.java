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
package io.github.ladysnake.pal.impl.mixin;

import com.mojang.authlib.GameProfile;
import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.VanillaAbilities;
import io.github.ladysnake.pal.impl.PalInternals;
import io.github.ladysnake.pal.impl.PlayerAbilityView;
import io.github.ladysnake.pal.impl.VanillaAbilityTracker;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerAbilityView {

    @Unique
    private final Map<PlayerAbility, AbilityTracker> palAbilities = new LinkedHashMap<>();

    public ServerPlayerMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions, CallbackInfo ci) {
        PalInternals.populate(this, this.palAbilities);
    }

    @Override
    public Iterable<PlayerAbility> listPalAbilities() {
        return this.palAbilities.keySet();
    }

    @Override
    public AbilityTracker get(PlayerAbility abilityId) {
        return this.palAbilities.get(abilityId);
    }

    @Override
    public void refreshAllPalAbilities(boolean syncVanilla) {
        for (PlayerAbility ability : this.listPalAbilities()) {
            if (ability != VanillaAbilities.FLYING) {
                this.get(ability).refresh(false);
            }
        }
        if (syncVanilla) {
            this.onUpdateAbilities();  // batch vanilla abilities updates
        }
    }

    @Inject(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;setGameModeForPlayer(Lnet/minecraft/world/level/GameType;Lnet/minecraft/world/level/GameType;)V", shift = At.Shift.AFTER))
    private void copyAbilitiesAfterRespawn(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (alive) {
            var writeView = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, oldPlayer.registryAccess());
            //noinspection ConstantConditions
            ((ServerPlayerMixin) (Object) oldPlayer).writeAbilitiesToData(writeView, null);
            this.readAbilitiesFromData(TagValueInput.create(ProblemReporter.DISCARDING, oldPlayer.registryAccess(), writeView.buildResult()), null);
        }
    }

    @Inject(method = "onUpdateAbilities", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/player/Abilities;)Lnet/minecraft/network/protocol/game/ClientboundPlayerAbilitiesPacket;"))
    private void checkAbilityConsistency(CallbackInfo ci) {
        for (PlayerAbility ability : this.listPalAbilities()) {
            AbilityTracker tracker = this.get(ability);
            if (tracker instanceof VanillaAbilityTracker && ability != VanillaAbilities.FLYING) { // flying is volatile anyway
                ((VanillaAbilityTracker) tracker).checkConflict();
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeAbilitiesToData(ValueOutput view, CallbackInfo ci) {
        var list = view.childrenList("playerabilitylib:abilities");
        for (Map.Entry<PlayerAbility, AbilityTracker> entry : this.palAbilities.entrySet()) {
            var abilityTag = list.addChild();
            abilityTag.putString("ability_id", entry.getKey().getId().toString());
            entry.getValue().save(abilityTag);
        }
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", shift = At.Shift.AFTER)
    )
    private void readAbilitiesFromData(ValueInput view, CallbackInfo ci) {
        for (var abilityTag : view.childrenListOrEmpty("playerabilitylib:abilities")) {
            String abilityId = abilityTag.getStringOr("ability_id", "");
            if (!abilityId.isEmpty()) {
                AbilityTracker tracker = this.palAbilities.get(PalInternals.getAbility(Identifier.tryParse(abilityId)));
                if (tracker != null) {
                    tracker.load(abilityTag);
                } else {
                    PalInternals.LOGGER.warn("Encountered unknown ability {} while deserializing data for {}", abilityId, this);
                }
            }
        }
    }
}

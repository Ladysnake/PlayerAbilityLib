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

import com.mojang.authlib.GameProfile;
import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.VanillaAbilities;
import io.github.ladysnake.pal.impl.PalInternals;
import io.github.ladysnake.pal.impl.PlayerAbilityView;
import io.github.ladysnake.pal.impl.VanillaAbilityTracker;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerAbilityView {

    @Unique
    private final Map<PlayerAbility, AbilityTracker> palAbilities = new LinkedHashMap<>();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
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
            this.sendAbilitiesUpdate();  // batch vanilla abilities updates
        }
    }

    @Inject(method = "sendAbilitiesUpdate", at = @At(value = "NEW", target = "net/minecraft/network/packet/s2c/play/PlayerAbilitiesS2CPacket"))
    private void checkAbilityConsistency(CallbackInfo ci) {
        for (PlayerAbility ability : this.listPalAbilities()) {
            AbilityTracker tracker = this.get(ability);
            if (tracker instanceof VanillaAbilityTracker && ability != VanillaAbilities.FLYING) { // flying is volatile anyway
                ((VanillaAbilityTracker) tracker).checkConflict();
            }
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeAbilitiesToTag(CompoundTag tag, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (Map.Entry<PlayerAbility, AbilityTracker> entry : this.palAbilities.entrySet()) {
            CompoundTag abilityTag = new CompoundTag();
            abilityTag.putString("ability_id", entry.getKey().getId().toString());
            entry.getValue().save(abilityTag);
            list.add(abilityTag);
        }
        tag.put("playerabilitylib:abilities", list);
    }

    @Inject(
        method = "readCustomDataFromTag",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER)
    )
    private void readAbilitiesFromTag(CompoundTag tag, CallbackInfo ci) {
        for (Tag t : tag.getList("playerabilitylib:abilities", NbtType.COMPOUND)) {
            CompoundTag abilityTag = ((CompoundTag) t);
            if (abilityTag.contains("ability_id")) {
                Identifier abilityId = Identifier.tryParse(abilityTag.getString("ability_id"));
                if (abilityId != null) {
                    AbilityTracker ability = this.palAbilities.get(PalInternals.getAbility(abilityId));
                    if (ability != null) {
                        ability.load(abilityTag);
                    }
                }
            }
        }
    }
}

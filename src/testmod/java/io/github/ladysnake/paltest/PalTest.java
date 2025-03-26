/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2025 Ladysnake
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
import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class PalTest implements ModInitializer {

    public static Identifier id(String path) {
        return Identifier.of("paltest", path);
    }

    @Override
    public void onInitialize() {
        PalTestAbilities.init();
        this.registerWaxWings();
        registerItem("bad_charm", BadFlightItem::new);
        registerItem("flight_charm", settings -> new AbilityToggleItem(settings, VanillaAbilities.ALLOW_FLYING, id("charm_flight")));
        registerItem("kryptonite", settings -> new AbilityToggleItem(settings, PalTestAbilities.LIMIT_FLIGHT, id("kryptonite")));
        Registry.register(Registries.STATUS_EFFECT, id("flight"), new FlightEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF));
    }

    private <I extends Item> I registerItem(String id, Function<Item.Settings, I> factory) {
        Identifier id1 = id(id);
        return Registry.register(Registries.ITEM, id1, factory.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id1))));
    }

    private void registerWaxWings() {
        Item waxWings = registerItem("wax_wings", settings -> new Item(settings.armor(ArmorMaterials.LEATHER, EquipmentType.CHESTPLATE)));
        AbilitySource source = Pal.getAbilitySource(id("wax_wings"), AbilitySource.CONSUMABLE);
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
                if (chestplate.getItem() == waxWings) {
                    source.grantTo(player, VanillaAbilities.ALLOW_FLYING);
                    if (source.isActivelyGranting(player, VanillaAbilities.ALLOW_FLYING)) {
                        chestplate.damage(1, player, EquipmentSlot.CHEST);
                    }
                } else {
                    source.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
                }
            }
        });
    }
}

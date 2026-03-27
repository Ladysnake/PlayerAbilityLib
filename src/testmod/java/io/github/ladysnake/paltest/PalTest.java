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
import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;

public final class PalTest implements ModInitializer {

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("paltest", path);
    }

    @Override
    public void onInitialize() {
        PalTestAbilities.init();
        this.registerWaxWings();
        registerItem("bad_charm", BadFlightItem::new);
        registerItem("flight_charm", settings -> new AbilityToggleItem(settings, VanillaAbilities.ALLOW_FLYING, id("charm_flight")));
        registerItem("kryptonite", settings -> new AbilityToggleItem(settings, PalTestAbilities.LIMIT_FLIGHT, id("kryptonite")));
        Registry.register(BuiltInRegistries.MOB_EFFECT, id("flight"), new FlightEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    }

    private <I extends Item> I registerItem(String id, Function<Item.Properties, I> factory) {
        Identifier id1 = id(id);
        return Registry.register(BuiltInRegistries.ITEM, id1, factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id1))));
    }

    private void registerWaxWings() {
        Item waxWings = registerItem("wax_wings", settings -> new Item(settings.humanoidArmor(ArmorMaterials.LEATHER, ArmorType.CHESTPLATE)));
        AbilitySource source = Pal.getAbilitySource(id("wax_wings"), AbilitySource.CONSUMABLE);
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                if (chestplate.getItem() == waxWings) {
                    source.grantTo(player, VanillaAbilities.ALLOW_FLYING);
                    if (source.isActivelyGranting(player, VanillaAbilities.ALLOW_FLYING)) {
                        chestplate.hurtAndBreak(1, player, EquipmentSlot.CHEST);
                    }
                } else {
                    source.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
                }
            }
        });
    }
}

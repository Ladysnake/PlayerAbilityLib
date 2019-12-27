package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class PalTest implements ModInitializer {

    public static Identifier id(String path) {
        return new Identifier("paltest", path);
    }

    @Override
    public void onInitialize() {
        PalTestAbilities.init();
        Registry.register(Registry.ITEM, id("bad_charm"), new BadFlightItem(new Item.Settings()));
        Registry.register(Registry.ITEM, id("flight_charm"), new AbilityToggleItem(new Item.Settings(), VanillaAbilities.ALLOW_FLYING, id("charm_flight")));
        Registry.register(Registry.ITEM, id("kryptonite"), new AbilityToggleItem(new Item.Settings(), PalTestAbilities.LIMIT_FLIGHT, id("kryptonite")));
        Registry.register(Registry.STATUS_EFFECT, id("flight"), new FlightEffect(StatusEffectType.BENEFICIAL, 0xFFFFFF));
    }

}

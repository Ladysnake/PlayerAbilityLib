package io.github.ladysnake.pal;

import io.github.ladysnake.pal.impl.PalAccessor;
import io.github.ladysnake.pal.impl.PalRegistries;
import io.github.ladysnake.pal.impl.VanillaToggleableAbility;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.function.Function;

/**
 * Player Ability Lib's main class. Provides static methods to interact with player abilities.
 *
 * <p> Use example for a standard flight suit:
 * <pre><code>
 *  public static final Identifier SUIT_FLIGHT = new Identifier("flightsuit", "suit_flight");
 *
 *  public void onEquip(PlayerEntity player) {
 *      if (!player.world.isClient) {
 *          Pal.getAbilities(player).get(VanillaAbilities.ALLOW_FLYING).add(SUIT_FLIGHT);
 *      }
 *  }
 *
 *  public void onUnequip(PlayerEntity player) {
 *      if (!player.world.isClient) {
 *          Pal.getAbilities(player).get(VanillaAbilities.ALLOW_FLYING).remove(SUIT_FLIGHT);
 *      }
 *  }
 * </code></pre>
 * Calling the {@code onEquip} and {@code onUnequip} methods of this example
 * at the right time is left as an exercise to the reader.
 *
 * @see PlayerAbilityView
 * @see ToggleableAbility
 * @see VanillaAbilities
 */
public final class Pal implements ModInitializer {

    /**
     * Retrieves a {@link PlayerAbilityView} for the given {@code player}.
     *
     * <p> The returned view can be used to query and update the status of
     * any previously {@link #registerAbility(Identifier, Function) registered} ability.
     *
     * @param player an initialized player to get the abilities of
     * @return a view for the player's abilities
     */
    public static PlayerAbilityView getAbilities(PlayerEntity player) {
        return ((PalAccessor) player).getPalAbilities();
    }

    /**
     * Registers a player ability that mods can interact with.
     *
     * <p> A registered {@link ToggleableAbility} will be accessible using {@link PlayerAbilityView#get(Identifier)}
     * with the given {@code abilityId}. Said ability instance is attached to every player at construction time using
     * the given {@code factory}.
     *
     * @param abilityId a unique identifier for the ability
     * @param factory   a factory to create {@code ToggleableAbility} instances for every player
     * @apiNote abilities must be registered during initialization.
     * @see SimpleToggleableAbility
     * @see #isAbilityRegistered(Identifier)
     */
    public static void registerAbility(Identifier abilityId, Function<PlayerEntity, ToggleableAbility> factory) {
        PalRegistries.registerAbility(abilityId, factory);
    }

    /**
     * Returns {@code true} if an ability has been registered with the given {@code abilityId}.
     *
     * @param abilityId a unique ability identifier to check for
     * @return {@code true} if the ability has been registered
     * @see #registerAbility(Identifier, Function)
     */
    public static boolean isAbilityRegistered(Identifier abilityId) {
        return PalRegistries.isAbilityRegistered(abilityId);
    }

    @Override
    public void onInitialize() {
        registerAbility(VanillaAbilities.INVULNERABLE, player -> new VanillaToggleableAbility(VanillaAbilities.INVULNERABLE, player, (g, a, e) -> a.invulnerable = e || !g.isSurvivalLike(), a -> a.invulnerable));
        registerAbility(VanillaAbilities.ALLOW_FLYING, player -> new VanillaToggleableAbility(VanillaAbilities.ALLOW_FLYING, player, (g, a, e) -> {
            a.allowFlying = e || !g.isSurvivalLike();
            a.flying &= a.allowFlying;
        }, a -> a.allowFlying));
        registerAbility(VanillaAbilities.FLYING, player -> new VanillaToggleableAbility(VanillaAbilities.FLYING, player, (g, a, e) -> a.flying = e || g == GameMode.SPECTATOR, a -> a.flying));
        registerAbility(VanillaAbilities.CREATIVE_MODE, player -> new VanillaToggleableAbility(VanillaAbilities.CREATIVE_MODE, player, (g, a, e) -> a.creativeMode = e || g.isCreative(), a -> a.creativeMode));
        registerAbility(VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, player -> new VanillaToggleableAbility(VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, player, (g, a, e) -> a.allowModifyWorld = !e && !g.shouldLimitWorldModification(), a -> !a.allowModifyWorld));
    }
}

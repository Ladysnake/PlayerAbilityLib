package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

/**
 * A view for mods to interact with a player's abilities.
 */
public interface PlayerAbilityView {

    /**
     * Returns a {@link PlayerAbilityView} for the given {@code player}.
     *
     * <p> The returned view can be used to query and update the status of
     * any previously {@link Pal#registerAbility(Identifier, BiFunction) registered} ability.
     *
     * @param player an initialized player to get the abilities of
     * @return a view for the player's abilities
     */
    static PlayerAbilityView of(PlayerEntity player) {
        return (PlayerAbilityView) player;
    }

    /**
     * Gets a handle for a given ability in the form of a {@link AbilityTracker}
     *
     * @param abilityId the unique identifier of the ability
     * @return a toggleable handle for the ability
     * @throws IllegalArgumentException if {@code abilityId} has not been registered
     * @see VanillaAbilities
     * @see Pal#isAbilityRegistered(PlayerAbility)
     */
    AbilityTracker get(PlayerAbility abilityId);

    /**
     * Refreshes each ability of this player.
     *
     * @param syncVanilla {@code true} if vanilla {@link PlayerAbilities} should be synchronized as a result of this call
     * @see AbilityTracker#refresh(boolean)
     */
    void refreshAll(boolean syncVanilla);
}

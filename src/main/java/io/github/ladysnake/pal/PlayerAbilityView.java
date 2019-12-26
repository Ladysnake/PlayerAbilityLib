package io.github.ladysnake.pal;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * A view for mods to interact with a player's abilities
 *
 * @see Pal#getAbilities(PlayerEntity)
 */
public interface PlayerAbilityView {

    /**
     * Gets a handle for a given ability in the form of a {@link ToggleableAbility}
     *
     * @param abilityId the unique identifier of the ability
     * @return a toggleable handle for the ability
     * @throws IllegalArgumentException if {@code abilityId} has not been registered
     * @see VanillaAbilities
     * @see Pal#isAbilityRegistered(Identifier)
     */
    ToggleableAbility get(Identifier abilityId);

    /**
     * Refreshes each ability of this player.
     *
     * @param syncVanilla {@code true} if vanilla {@link PlayerAbilities} should be synchronized as a result of this call
     * @see ToggleableAbility#refresh(boolean)
     */
    void refreshAll(boolean syncVanilla);

    /**
     * Writes this {@code PlayerAbilityView} in a serialized form to {@code tag}.
     *
     * @param tag the tag to write to
     */
    void toTag(CompoundTag tag);

    /**
     * Reads a serialized form of a {@code PlayerAbilityView} from {@code tag} into this object.
     *
     * @param tag the tag to read from
     */
    void fromTag(CompoundTag tag);
}

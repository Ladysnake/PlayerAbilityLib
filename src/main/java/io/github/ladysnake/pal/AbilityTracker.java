package io.github.ladysnake.pal;

import net.minecraft.nbt.CompoundTag;

/**
 * A tracker for a player ability that can be turned on or off.
 */
public interface AbilityTracker {

    /**
     * Adds a source for this tracker's ability.
     *
     * <p> If no {@link PlayerAbilityEnableCallback} disallows the ability's activation,
     * {@link #isEnabled()} will return {@code true} after calling this method.
     *
     * @param abilitySource the source granting the ability
     */
    void grant(AbilitySource abilitySource);

    /**
     * Removes a source for this tracker's ability.
     *
     * <p> If no other ability source currently exist for the ability,
     * {@link #isEnabled()} will return {@code false} after calling this method.
     *
     * @param abilitySource the source granting the ability
     */
    void revoke(AbilitySource abilitySource);

    /**
     * Returns {@code true} if this tracker's ability is currently provided by the given {@code abilitySource}.
     *
     * @param abilitySource the source granting the ability
     * @return {@code true} if this tracker's ability is provided by {@code abilitySource}
     */
    boolean isGrantedBy(AbilitySource abilitySource);

    /**
     * Returns {@code true} if this tracker's ability is currently enabled.
     *
     * <p> An ability may be enabled even if it is not provided by any ability source.
     * For example, most {@link VanillaAbilities} are intrinsically provided by some gamemodes.
     *
     * @return {@code true} if this ability is enabled
     */
    boolean isEnabled();

    /**
     * Refreshes this ability tracker.
     *
     * <p> For vanilla abilities, updating can be batched for grouped refreshes.
     * When that is the case, {@code syncVanilla} can be made {@code false} to avoid redundant
     * packets.
     *
     * @param syncVanilla {@code true} if vanilla abilities should be synchronized as a result of this call
     */
    void refresh(boolean syncVanilla);

    /**
     * Saves this {@code AbilityTracker} in a serialized form to {@code tag}.
     *
     * @param tag the tag to write to
     */
    void save(CompoundTag tag);

    /**
     * Loads a serialized form of an {@code AbilityTracker} from {@code tag} into this object.
     *
     * @param tag the tag to read from
     */
    void load(CompoundTag tag);
}

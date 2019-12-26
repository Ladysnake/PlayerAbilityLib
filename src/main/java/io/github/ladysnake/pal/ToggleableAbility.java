package io.github.ladysnake.pal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * A player ability that can be turned on or off.
 */
public interface ToggleableAbility {
    /**
     * Utility method to add or remove an ability source based on a boolean value
     *
     * @param abilitySource ability source to add or remove
     * @param enabled       {@code true} if the source should be added, {@code false} if it should be removed
     */
    default void set(Identifier abilitySource, boolean enabled) {
        if (enabled) this.add(abilitySource);
        else this.remove(abilitySource);
    }

    /**
     * Adds a source for this ability.
     *
     * <p> If no {@link PlayerAbilityEnableCallback} disallows this ability's activation,
     * {@link #isEnabled()} will return {@code true} after calling this method.
     *
     * @param abilitySource the id of the source
     */
    void add(Identifier abilitySource);

    /**
     * Removes a source for this ability.
     *
     * <p> If no other ability source currently exist for this ability,
     * {@link #isEnabled()} will return {@code false} after calling this method.
     *
     * @param abilitySource the id of the source
     */
    void remove(Identifier abilitySource);

    /**
     * Returns {@code true} if this ability is currently provided by the given {@code abilitySource}.
     *
     * @param abilitySource the id of the source
     * @return {@code true} if this ability is provided by {@code abilitySource}
     */
    boolean isProvidedBy(Identifier abilitySource);

    /**
     * Returns {@code true} if this ability is currently enabled.
     *
     * <p> An ability may be enabled even if it is not provided by any ability source.
     * For example, most {@link VanillaAbilities} are intrinsically provided by some gamemodes.
     *
     * @return {@code true} if this ability is enabled
     */
    boolean isEnabled();

    /**
     * @param syncVanilla {@code true} if vanilla abilities should be synchronized as a result of this call
     */
    void refresh(boolean syncVanilla);

    /**
     * Writes this {@code ToggleableAbility} in a serialized form to {@code tag}.
     *
     * @param tag the tag to write to
     */
    void toTag(CompoundTag tag);

    /**
     * Reads a serialized form of a {@code ToggleableAbility} from {@code tag} into this object.
     *
     * @param tag the tag to read from
     */
    void fromTag(CompoundTag tag);
}

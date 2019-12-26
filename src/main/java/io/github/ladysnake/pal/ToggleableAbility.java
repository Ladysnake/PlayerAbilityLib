package io.github.ladysnake.pal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * A player ability that can be turned on or off.
 */
public interface ToggleableAbility {
    default void set(Identifier abilitySource, boolean enabled) {
        if (enabled) this.add(abilitySource);
        else this.remove(abilitySource);
    }

    /**
     * Adds an ability source for this ability.
     *
     * @param abilitySource
     */
    void add(Identifier abilitySource);

    void remove(Identifier abilitySource);

    boolean has(Identifier abilitySource);

    boolean isEnabled();

    /**
     * @param syncVanilla {@code true} if vanilla abilities should be synchronized as a result of this call
     */
    void refresh(boolean syncVanilla);

    void toTag(CompoundTag tag);

    void fromTag(CompoundTag tag);
}

package io.github.ladysnake.pal;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public interface PlayerAbilityView {

    ToggleableAbility get(Identifier abilityId);

    /**
     * Refreshes each ability of this player.
     *
     * @param syncVanilla {@code true} if vanilla {@link PlayerAbilities} should be synchronized as a result of this call
     * @see ToggleableAbility#refresh(boolean)
     */
    void refreshAll(boolean syncVanilla);

    void toTag(CompoundTag tag);

    void fromTag(CompoundTag tag);
}

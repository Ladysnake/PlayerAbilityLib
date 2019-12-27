package io.github.ladysnake.pal;

import io.github.ladysnake.pal.impl.VanillaAbilityTracker;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides a basic implementation of the {@code AbilityTracker}
 * interface, to either be used directly or minimize the effort required to implement the interface.
 *
 * <p> To implement a tracker for an externally stored ability, the programmer needs only
 * to extend this class and override the {@link #updateState(boolean)} and {@link #isEnabled()} methods.
 *
 * <p> To implement a tracker for a synchronized ability, the programmer needs to extend this class
 * and override the {@link #sync()} method with an implementation that triggers some packet sending mechanism.
 * @see AbilityTracker
 */
public class SimpleAbilityTracker implements AbilityTracker {
    protected final PlayerEntity player;
    protected final Set<AbilitySource> abilitySources = new HashSet<>();
    protected final PlayerAbility ability;

    public SimpleAbilityTracker(PlayerAbility ability, PlayerEntity player) {
        this.ability = ability;
        this.player = player;
    }

    @Override
    public void grant(AbilitySource abilitySource) {
        boolean wasEmpty = this.abilitySources.isEmpty();
        if (this.abilitySources.add(abilitySource) && wasEmpty) {
            if (PlayerAbilityEnableCallback.EVENT.invoker().allow(this.player, this.ability, abilitySource)) {
                this.updateState(true);
                this.sync();
            }
        }
    }

    @Override
    public void revoke(AbilitySource abilitySource) {
        if (this.abilitySources.remove(abilitySource) && this.abilitySources.isEmpty()) {
            this.updateState(false);
            this.sync();
        }
    }

    @Override
    public boolean isGrantedBy(AbilitySource abilitySource) {
        return this.abilitySources.contains(abilitySource);
    }

    @Override
    public void refresh(boolean syncVanilla) {
        boolean enabled = false;
        for (AbilitySource abilitySource : this.abilitySources) {
            if (PlayerAbilityEnableCallback.EVENT.invoker().allow(this.player, this.ability, abilitySource)) {
                enabled = true;
                break;
            }
        }
        this.updateState(enabled);
        if (syncVanilla || !(this instanceof VanillaAbilityTracker)) {
            this.sync();
        }
    }

    @Override
    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (AbilitySource abilitySource : this.abilitySources) {
            list.add(StringTag.of(abilitySource.toString()));
        }
        tag.put("ability_sources", list);
    }

    @Override
    public void load(CompoundTag tag) {
        for (Tag id : tag.getList("ability_sources", NbtType.STRING)) {
            this.grant(Pal.getAbilitySource(new Identifier(id.asString())));
        }
    }

    /**
     * Updates the state of this tracker's ability.
     *
     * @param enabled {@code true} if the ability should be enabled, {@code false} if it should be disabled
     */
    protected void updateState(boolean enabled) {
        PlayerAbilityUpdatedCallback.event(this.ability).invoker().onAbilityUpdated(this.player, enabled);
    }

    /**
     * Synchronizes this tracker's ability with relevant clients.
     */
    protected void sync() {
        // NO-OP
    }

    @Override
    public boolean isEnabled() {
        return !this.abilitySources.isEmpty();
    }
}

package io.github.ladysnake.pal;

import io.github.ladysnake.pal.impl.VanillaToggleableAbility;
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
 * This class provides a skeletal implementation of the {@code ToggleableAbility}
 * interface, to minimize the effort required to implement this interface.
 *
 * @see ToggleableAbility
 */
public class SimpleToggleableAbility implements ToggleableAbility {
    protected final PlayerEntity player;
    protected final Set<Identifier> abilitySources = new HashSet<>();
    protected final Identifier abilityId;

    public SimpleToggleableAbility(Identifier abilityId, PlayerEntity player) {
        this.abilityId = abilityId;
        this.player = player;
    }

    @Override
    public void add(Identifier abilitySource) {
        boolean wasEmpty = this.abilitySources.isEmpty();
        if (this.abilitySources.add(abilitySource) && wasEmpty) {
            if (PlayerAbilityEnableCallback.EVENT.invoker().allow(this.player, this.abilityId, abilitySource)) {
                this.updateState(true);
                this.sync();
            }
        }
    }

    @Override
    public void remove(Identifier abilitySource) {
        if (this.abilitySources.remove(abilitySource) && this.abilitySources.isEmpty()) {
            this.updateState(false);
            this.sync();
        }
    }

    @Override
    public boolean isProvidedBy(Identifier abilitySource) {
        return this.abilitySources.contains(abilitySource);
    }

    @Override
    public void refresh(boolean syncVanilla) {
        boolean enabled = false;
        for (Identifier abilitySource : this.abilitySources) {
            if (PlayerAbilityEnableCallback.EVENT.invoker().allow(this.player, this.abilityId, abilitySource)) {
                enabled = true;
                break;
            }
        }
        this.updateState(enabled);
        if (syncVanilla || !(this instanceof VanillaToggleableAbility)) {
            this.sync();
        }
    }

    @Override
    public void toTag(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Identifier abilitySource : this.abilitySources) {
            list.add(StringTag.of(abilitySource.toString()));
        }
        tag.put("ability_sources", list);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        for (Tag id : tag.getList("ability_sources", NbtType.STRING)) {
            this.add(new Identifier(id.asString()));
        }
    }

    protected void updateState(boolean enabled) {
        PlayerAbilityUpdatedCallback.event(this.abilityId).invoker().onAbilityUpdated(this.player, enabled);
    }

    protected void sync() {
        // NO-OP
    }

    @Override
    public boolean isEnabled() {
        return !this.abilitySources.isEmpty();
    }
}

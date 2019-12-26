package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.PlayerAbilityView;
import io.github.ladysnake.pal.ToggleableAbility;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MapPlayerAbilityView implements PlayerAbilityView {
    private final Map<Identifier, ToggleableAbility> abilities = new HashMap<>();
    private final PlayerEntity player;

    public MapPlayerAbilityView(PlayerEntity player) {
        this.player = player;
        for (Map.Entry<Identifier, Function<PlayerEntity, ToggleableAbility>> entry : PalRegistries.abilityRegistry.entrySet()) {
            this.abilities.put(entry.getKey(), entry.getValue().apply(player));
        }
    }

    @Override
    public ToggleableAbility get(Identifier abilityId) {
        return this.abilities.get(abilityId);
    }

    @Override
    public void refreshAll(boolean syncVanilla) {
        for (ToggleableAbility ability : this.abilities.values()) {
            ability.refresh(false);
        }
        if (syncVanilla) {
            this.player.sendAbilitiesUpdate();  // batch vanilla abilities updates
        }
    }

    @Override
    public void toTag(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<Identifier, ToggleableAbility> entry : this.abilities.entrySet()) {
            CompoundTag abilityTag = new CompoundTag();
            abilityTag.putString("ability_id", entry.getKey().toString());
            entry.getValue().toTag(abilityTag);
            list.add(abilityTag);
        }
        tag.put("playerabilitylib:abilities", list);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        for (Tag t : tag.getList("playerabilitylib:abilities", NbtType.COMPOUND)) {
            CompoundTag abilityTag = ((CompoundTag) t);
            if (abilityTag.contains("ability_id")) {
                ToggleableAbility ability = this.abilities.get(new Identifier(abilityTag.getString("ability_id")));
                if (ability != null) {
                    ability.fromTag(abilityTag);
                }
            }
        }
    }
}

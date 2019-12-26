package io.github.ladysnake.pal;

import io.github.ladysnake.pal.impl.PalRegistries;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface PlayerAbilityUpdatedCallback {

    static Event<PlayerAbilityUpdatedCallback> event(Identifier abilityId) {
        return PalRegistries.getOrCreateUpdateEvent(abilityId);
    }

    void onAbilityUpdated(PlayerEntity player, boolean nowEnabled);
}

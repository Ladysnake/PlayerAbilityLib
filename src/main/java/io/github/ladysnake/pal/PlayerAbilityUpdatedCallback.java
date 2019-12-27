package io.github.ladysnake.pal;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface PlayerAbilityUpdatedCallback {

    static Event<PlayerAbilityUpdatedCallback> event(PlayerAbility ability) {
        return ability.updateEvent;
    }

    void onAbilityUpdated(PlayerEntity player, boolean nowEnabled);
}

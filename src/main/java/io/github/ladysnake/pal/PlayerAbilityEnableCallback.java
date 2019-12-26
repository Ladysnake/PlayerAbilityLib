package io.github.ladysnake.pal;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface PlayerAbilityEnableCallback {

    Event<PlayerAbilityEnableCallback> EVENT = EventFactory.createArrayBacked(PlayerAbilityEnableCallback.class,
        (listeners) -> (player, abilityId, abilitySource) -> {
            for (PlayerAbilityEnableCallback listener : listeners) {
                if (!listener.allow(player, abilityId, abilitySource)) {
                    return false;
                }
            }
            return true;
        });

    boolean allow(PlayerEntity player, Identifier abilityId, Identifier abilitySource);
}

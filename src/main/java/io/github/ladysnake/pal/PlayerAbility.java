package io.github.ladysnake.pal;

import io.github.ladysnake.pal.impl.PalInternals;
import io.github.ladysnake.pal.impl.PlayerAbilityView;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

public final class PlayerAbility {
    final Event<PlayerAbilityUpdatedCallback> updateEvent = PalInternals.createUpdateEvent();
    final BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> trackerFactory;
    final Identifier id;

    /**
     * @see Pal#registerAbility(String, String, BiFunction)
     * @see Pal#registerAbility(Identifier, BiFunction)
     */
    PlayerAbility(Identifier id, BiFunction<PlayerAbility, PlayerEntity, AbilityTracker> trackerFactory) {
        this.id = id;
        this.trackerFactory = trackerFactory;
    }

    public AbilityTracker getTracker(PlayerEntity player) {
        return PlayerAbilityView.of(player).get(this);
    }

    public boolean isEnabledFor(PlayerEntity player) {
        return PlayerAbilityView.of(player).get(this).isEnabled();
    }

    public AbilityTracker createTracker(PlayerEntity player) {
        return this.trackerFactory.apply(this, player);
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PlayerAbility[" + this.id + "]";
    }
}

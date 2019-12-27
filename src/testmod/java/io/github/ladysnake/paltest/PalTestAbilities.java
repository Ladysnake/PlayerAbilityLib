package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.*;

public final class PalTestAbilities {
    public static final PlayerAbility LIMIT_FLIGHT = Pal.registerAbility(PalTest.id("limit_flight"), SimpleAbilityTracker::new);

    public static void init() {
        PlayerAbilityUpdatedCallback.event(LIMIT_FLIGHT).register((player, nowEnabled) ->
            VanillaAbilities.ALLOW_FLYING.getTracker(player).refresh(true));
        PlayerAbilityEnableCallback.EVENT.register((player, abilityId, abilitySource) -> {
            if (VanillaAbilities.ALLOW_FLYING.equals(abilityId)) {
                return !LIMIT_FLIGHT.isEnabledFor(player);
            }
            return true;
        });
    }
}

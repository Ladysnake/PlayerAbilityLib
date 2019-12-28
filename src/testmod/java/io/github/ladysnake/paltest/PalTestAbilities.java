package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.*;

/**
 * Custom abilities
 */
public final class PalTestAbilities {
    /** An ability that temporarily blocks creative flight when active */
    public static final PlayerAbility LIMIT_FLIGHT = Pal.registerAbility(PalTest.id("limit_flight"), SimpleAbilityTracker::new);

    public static void init() {
        PlayerAbilityUpdatedCallback.event(LIMIT_FLIGHT).register((player, nowEnabled) ->
            // Refresh the flight tracker, will call PlayerAbilityEnableCallback for every source
            VanillaAbilities.ALLOW_FLYING.getTracker(player).refresh(true));
        PlayerAbilityEnableCallback.EVENT.register((player, ability, abilitySource) -> {
            if (ability == VanillaAbilities.ALLOW_FLYING) {
                return !LIMIT_FLIGHT.isEnabledFor(player);  // block flight
            }
            return true;
        });
    }
}

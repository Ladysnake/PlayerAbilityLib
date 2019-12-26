package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbilityEnableCallback;
import io.github.ladysnake.pal.SimpleToggleableAbility;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.util.Identifier;

public final class PalTestAbilities {
    public static final Identifier LIMIT_FLIGHT = PalTest.id("limit_flight");

    public static void init() {
        Pal.registerAbility(LIMIT_FLIGHT, p -> new SimpleToggleableAbility(LIMIT_FLIGHT, p) {
            @Override
            protected void updateState(boolean enabled) {
                super.updateState(enabled);
                Pal.getAbilities(player).get(VanillaAbilities.ALLOW_FLYING).refresh(true);
            }
        });
        PlayerAbilityEnableCallback.EVENT.register((player, abilityId, abilitySource) -> {
            if (VanillaAbilities.ALLOW_FLYING.equals(abilityId)) {
                return !Pal.getAbilities(player).get(LIMIT_FLIGHT).isEnabled();
            }
            return true;
        });
    }
}

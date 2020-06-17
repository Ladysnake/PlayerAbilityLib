package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;

/**
 * A status effect that gives creative flight to players
 */
public class FlightEffect extends StatusEffect {
    public static final AbilitySource FLIGHT_POTION = Pal.getAbilitySource(PalTest.id("potion_flight"));

    public FlightEffect(StatusEffectType statusEffectType, int color) {
        super(statusEffectType, color);
    }

    @Override
    public void onApplied(LivingEntity effected, AttributeContainer abstractEntityAttributeContainer, int amplifier) {
        super.onApplied(effected, abstractEntityAttributeContainer, amplifier);
        if (effected instanceof PlayerEntity) {
            Pal.grantAbility((PlayerEntity) effected, VanillaAbilities.ALLOW_FLYING, FLIGHT_POTION);
        }
    }

    @Override
    public void onRemoved(LivingEntity effected, AttributeContainer abstractEntityAttributeContainer, int amplifier) {
        super.onRemoved(effected, abstractEntityAttributeContainer, amplifier);
        if (effected instanceof PlayerEntity) {
            Pal.revokeAbility((PlayerEntity) effected, VanillaAbilities.ALLOW_FLYING, FLIGHT_POTION);
        }
    }
}

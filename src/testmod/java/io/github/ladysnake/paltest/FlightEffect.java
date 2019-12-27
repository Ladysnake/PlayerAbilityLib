package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;

public class FlightEffect extends StatusEffect {
    public static final AbilitySource CHARM_FLIGHT = Pal.getAbilitySource(PalTest.id("potion_flight"));

    public FlightEffect(StatusEffectType statusEffectType, int color) {
        super(statusEffectType, color);
    }

    @Override
    public void onApplied(LivingEntity effected, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int amplifier) {
        super.onApplied(effected, abstractEntityAttributeContainer, amplifier);
        if (effected instanceof PlayerEntity) {
            CHARM_FLIGHT.grantTo((PlayerEntity) effected, VanillaAbilities.ALLOW_FLYING);
        }
    }

    @Override
    public void onRemoved(LivingEntity effected, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int amplifier) {
        super.onRemoved(effected, abstractEntityAttributeContainer, amplifier);
        if (effected instanceof PlayerEntity) {
            CHARM_FLIGHT.revokeFrom((PlayerEntity) effected, VanillaAbilities.ALLOW_FLYING);
        }
    }
}

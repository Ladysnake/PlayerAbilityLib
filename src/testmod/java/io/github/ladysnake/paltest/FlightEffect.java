package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

class FlightEffect extends StatusEffect {
    public static final Identifier CHARM_FLIGHT = PalTest.id("potion_flight");

    protected FlightEffect(StatusEffectType statusEffectType, int color) {
        super(statusEffectType, color);
    }

    @Override
    public void onApplied(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        super.onApplied(livingEntity, abstractEntityAttributeContainer, i);
        if (livingEntity instanceof PlayerEntity) {
            Pal.getAbilities(((PlayerEntity) livingEntity)).get(VanillaAbilities.ALLOW_FLYING).add(CHARM_FLIGHT);
        }
    }

    @Override
    public void onRemoved(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        super.onRemoved(livingEntity, abstractEntityAttributeContainer, i);
        if (livingEntity instanceof PlayerEntity) {
            Pal.getAbilities(((PlayerEntity) livingEntity)).get(VanillaAbilities.ALLOW_FLYING).remove(CHARM_FLIGHT);
        }
    }
}

package io.github.ladysnake.paltest.mixin;

import io.github.ladysnake.paltest.FlightEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onStatusEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/attribute/AttributeContainer;)V"))
    private void callOnRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        if (effect.getEffectType() instanceof FlightEffect flightEffect) {
            flightEffect.onRemoved((LivingEntity) (Object) this);
        }
    }
}

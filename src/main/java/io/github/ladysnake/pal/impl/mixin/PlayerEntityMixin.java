package io.github.ladysnake.pal.impl.mixin;

import io.github.ladysnake.pal.PlayerAbilityView;
import io.github.ladysnake.pal.impl.MapPlayerAbilityView;
import io.github.ladysnake.pal.impl.PalAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PalAccessor {
    @Unique
    private final PlayerAbilityView palAbilities = new MapPlayerAbilityView((PlayerEntity) (Object) this);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public PlayerAbilityView getPalAbilities() {
        return this.palAbilities;
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeAbilitiesToTag(CompoundTag tag, CallbackInfo ci) {
        palAbilities.toTag(tag);
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readAbilitiesFromTag(CompoundTag tag, CallbackInfo ci) {
        palAbilities.fromTag(tag);
    }
}

package io.github.ladysnake.pal.impl.mixin;

import com.mojang.authlib.GameProfile;
import io.github.ladysnake.pal.AbilityTracker;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.impl.PalInternals;
import io.github.ladysnake.pal.impl.PlayerAbilityView;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerAbilityView {
    @Shadow
    public abstract void sendAbilitiesUpdate();

    @Unique
    private final Map<PlayerAbility, AbilityTracker> abilities = new HashMap<>();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(World world, GameProfile profile, CallbackInfo ci) {
        PalInternals.populate((PlayerEntity) (Object) this, this.abilities);
    }

    @Override
    public AbilityTracker get(PlayerAbility abilityId) {
        return this.abilities.get(abilityId);
    }

    @Override
    public void refreshAll(boolean syncVanilla) {
        for (AbilityTracker ability : this.abilities.values()) {
            ability.refresh(false);
        }
        if (syncVanilla) {
            this.sendAbilitiesUpdate();  // batch vanilla abilities updates
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeAbilitiesToTag(CompoundTag tag, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (Map.Entry<PlayerAbility, AbilityTracker> entry : this.abilities.entrySet()) {
            CompoundTag abilityTag = new CompoundTag();
            abilityTag.putString("ability_id", entry.getKey().toString());
            entry.getValue().save(abilityTag);
            list.add(abilityTag);
        }
        tag.put("playerabilitylib:abilities", list);
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readAbilitiesFromTag(CompoundTag tag, CallbackInfo ci) {
        for (Tag t : tag.getList("playerabilitylib:abilities", NbtType.COMPOUND)) {
            CompoundTag abilityTag = ((CompoundTag) t);
            if (abilityTag.contains("ability_id")) {
                Identifier abilityId = Identifier.tryParse(abilityTag.getString("ability_id"));
                if (abilityId != null) {
                    AbilityTracker ability = this.abilities.get(PalInternals.getAbility(abilityId));
                    if (ability != null) {
                        ability.load(abilityTag);
                    }
                }
            }
        }
    }
}

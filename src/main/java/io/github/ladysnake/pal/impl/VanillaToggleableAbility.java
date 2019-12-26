package io.github.ladysnake.pal.impl;

import io.github.ladysnake.pal.SimpleToggleableAbility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.UUID;
import java.util.function.Predicate;

public final class VanillaToggleableAbility extends SimpleToggleableAbility {
    private final AbilitySetter setter;
    private final Predicate<PlayerAbilities> getter;

    public VanillaToggleableAbility(Identifier abilityId, PlayerEntity player, AbilitySetter setter, Predicate<PlayerAbilities> getter) {
        super(abilityId, player);
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    protected void updateState(boolean enabled) {
        super.updateState(enabled);
        this.setter.set(getGamemode(this.player), this.player.abilities, enabled);
    }

    @Override
    protected void sync() {
        this.player.sendAbilitiesUpdate();
    }

    @Override
    public boolean isEnabled() {
        return this.getter.test(this.player.abilities);
    }

    private static GameMode getGamemode(PlayerEntity player) {
        if (player.world.isClient) {
            return getClientGameMode(player.getGameProfile().getId());
        } else {
            return ((ServerPlayerEntity) player).interactionManager.getGameMode();
        }
    }

    private static GameMode getClientGameMode(UUID uuid) {
        PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(uuid);
        return playerListEntry != null ? playerListEntry.getGameMode() : GameMode.NOT_SET;
    }

    @FunctionalInterface
    public interface AbilitySetter {
        void set(GameMode g, PlayerAbilities abilities, boolean enabled);
    }
}

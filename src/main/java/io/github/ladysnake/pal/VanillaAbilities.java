package io.github.ladysnake.pal;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.Identifier;

public final class VanillaAbilities {
    /**
     * If enabled, players become invulnerable to all damage except the void, like in creative and spectator mode.
     *
     * @see PlayerAbilities#invulnerable
     */
    public static final Identifier INVULNERABLE = new Identifier("minecraft:invulnerable");
    /**
     * If enabled and {@link #ALLOW_FLYING} is enabled, players become able to move in any direction in the air,
     * like in creative and spectator mode.
     *
     * <p> This ability controls specifically the movement part of flying.
     * As such, it is the only vanilla ability to be controlled by the client.
     * However, in multiplayer, if a player is not allowed to fly from the server's perspective, it will be kicked
     * as soon as it actually starts flying.
     *
     * @see PlayerAbilities#flying
     */
    public static final Identifier FLYING = new Identifier("minecraft:flying");
    /**
     * If enabled, players become able to double tap space to fly, like in creative mode.
     *
     * <p> Enabling this ability is the easiest way to prevent servers kicking players for modded
     * forms of flight.
     *
     * @see PlayerAbilities#allowFlying
     */
    public static final Identifier ALLOW_FLYING = new Identifier("minecraft:mayfly");
    /**
     * If enabled, players stop consuming items and experience, and start dealing creative damage.
     *
     * @see PlayerAbilities#creativeMode
     */
    public static final Identifier CREATIVE_MODE = new Identifier("minecraft:instabuild");
    /**
     * If enabled, players will be denied world modifications as if they were in adventure or spectator mode.
     *
     * <p> Since players can modify the world by default, this ability is inverted from {@link PlayerAbilities#allowModifyWorld}
     * to let mods deny world modifications to players in survival mode.
     *
     * @see PlayerAbilities#allowModifyWorld
     */
    public static final Identifier LIMIT_WORLD_MODIFICATIONS = new Identifier("minecraft:maynotbuild");

    private VanillaAbilities() {
    }
}

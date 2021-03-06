package io.github.ladysnake.paltest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * An item that toggles flight without going through PAL.
 *
 * <p> PAL will make a best effort attempt to detect issues of this kind,
 * but mod incompatibility is very likely.
 *
 * <p><strong>TL;DR: this is only for testing, do not copy code from here</strong>
 *
 * @see AbilityToggleItem
 */
public class BadFlightItem extends Item {
    public BadFlightItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Direct ability access, issues abound !
            user.getAbilities().allowFlying = !user.getAbilities().allowFlying;
            user.getAbilities().flying &= user.getAbilities().allowFlying;
            user.sendAbilitiesUpdate();
            user.sendMessage(new LiteralText("Flight " + (user.getAbilities().allowFlying ? "enabled" : "disabled")), true);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

}

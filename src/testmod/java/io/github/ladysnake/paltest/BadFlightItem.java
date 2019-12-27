package io.github.ladysnake.paltest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BadFlightItem extends Item {
    public BadFlightItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            user.abilities.allowFlying = !user.abilities.allowFlying;
            user.abilities.flying &= user.abilities.allowFlying;
            user.sendAbilitiesUpdate();
            user.addChatMessage(new LiteralText("Flight " + (user.abilities.allowFlying ? "enabled" : "disabled")), true);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

}

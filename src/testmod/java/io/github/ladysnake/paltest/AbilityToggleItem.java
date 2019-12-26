package io.github.ladysnake.paltest;

import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.ToggleableAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

class AbilityToggleItem extends Item {
    private Identifier abilityId;
    private Identifier abilitySource;

    public AbilityToggleItem(Settings settings, Identifier abilityId, Identifier abilitySource) {
        super(settings);
        this.abilityId = abilityId;
        this.abilitySource = abilitySource;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ToggleableAbility ability = Pal.getAbilities(user).get(abilityId);
        if (!world.isClient) {
            if (ability.isProvidedBy(abilitySource)) {
                ability.remove(abilitySource);
            } else {
                ability.add(abilitySource);
            }
            user.addChatMessage(new LiteralText("")
                    .append(new LiteralText(abilitySource.toString()).styled(s -> s.setColor(Formatting.YELLOW)))
                    .append(ability.isProvidedBy(abilitySource) ? new LiteralText(" added").styled(s -> s.setColor(Formatting.GREEN)) : new LiteralText(" removed").styled(s -> s.setColor(Formatting.RED)))
                    .append(" (")
                    .append(new LiteralText(abilityId.toString()).styled(s -> s.setColor(Formatting.YELLOW)))
                    .append(" is ")
                    .append(ability.isEnabled() ? new LiteralText("enabled").styled(s -> s.setColor(Formatting.GREEN)) : new LiteralText("disabled").styled(s -> s.setColor(Formatting.RED)))
                    .append(")"), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2026 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package io.github.ladysnake.paltest;

import com.mojang.authlib.GameProfile;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public final class PalTestSuite {
    private static final AbilitySource TEST_SOURCE = Pal.getAbilitySource(PalTest.id("test_source"));
    private static final AbilitySource TEST_SOURCE_2 = Pal.getAbilitySource(PalTest.id("test_source_2"));

    @GameTest()
    public void grantingWorks(GameTestHelper ctx) {
        ServerPlayer mockPlayer = createMockPlayer(ctx);
        assertTrue(mockPlayer.getAbilities().mayBuild, "Unexpected init state");
        Pal.grantAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE);
        Pal.grantAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE_2);
        assertTrue(!mockPlayer.getAbilities().mayBuild, "Ability granting did not work");
        assertTrue(TEST_SOURCE.grants(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS), "Ability granting did not work");
        assertTrue(TEST_SOURCE_2.grants(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS), "Ability granting did not work");
        Pal.revokeAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE);
        assertTrue(!mockPlayer.getAbilities().mayBuild, "Revoking one source revoked the whole ability");
        Pal.revokeAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE_2);
        assertTrue(mockPlayer.getAbilities().mayBuild, "Revoking all sources did not revoke the whole ability");
        ctx.succeed();
    }

    private static void assertTrue(boolean b, String message) {
        if (!b) throw new GameTestAssertException(Component.literal(message), 0);
    }

    private ServerPlayer createMockPlayer(GameTestHelper ctx) {
        return new ServerPlayer(ctx.getLevel().getServer(), ctx.getLevel(), new GameProfile(UUID.randomUUID(), "test-mock-player"), ClientInformation.createDefault());
    }
}

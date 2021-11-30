/*
 * PlayerAbilityLib
 * Copyright (C) 2019-2021 Ladysnake
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
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;

import java.util.UUID;

public final class PalTestSuite implements FabricGameTest {
    private static final AbilitySource TEST_SOURCE = Pal.getAbilitySource(PalTest.id("test_source"));
    private static final AbilitySource TEST_SOURCE_2 = Pal.getAbilitySource(PalTest.id("test_source_2"));

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void grantingWorks(TestContext ctx) {
        ServerPlayerEntity mockPlayer = createMockPlayer(ctx);
        assertTrue(mockPlayer.getAbilities().allowModifyWorld, "Unexpected init state");
        Pal.grantAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE);
        Pal.grantAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE_2);
        assertTrue(!mockPlayer.getAbilities().allowModifyWorld, "Ability granting did not work");
        assertTrue(TEST_SOURCE.grants(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS), "Ability granting did not work");
        assertTrue(TEST_SOURCE_2.grants(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS), "Ability granting did not work");
        Pal.revokeAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE);
        assertTrue(!mockPlayer.getAbilities().allowModifyWorld, "Revoking one source revoked the whole ability");
        Pal.revokeAbility(mockPlayer, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, TEST_SOURCE_2);
        assertTrue(mockPlayer.getAbilities().allowModifyWorld, "Revoking all sources did not revoke the whole ability");
        ctx.complete();
    }

    private static void assertTrue(boolean b, String message) {
        if (!b) throw new GameTestException(message);
    }

    private ServerPlayerEntity createMockPlayer(TestContext ctx) {
        return new ServerPlayerEntity(ctx.getWorld().getServer(), ctx.getWorld(), new GameProfile(UUID.randomUUID(), "test-mock-player"));
    }
}

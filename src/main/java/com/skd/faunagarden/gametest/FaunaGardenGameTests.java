package com.skd.faunagarden.gametest;

import com.skd.faunagarden.blocks.FaunaCropsBlock;
import com.skd.faunagarden.core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the animal fertilizer item.
 */
@GameTestHolder("fauna_garden")
@PrefixGameTestTemplate(false)
public final class FaunaGardenGameTests {

    private FaunaGardenGameTests() {}

    @GameTest(template = "empty3x3x3")
    public static void fertilizerGrowsEveryCrop(GameTestHelper helper) {
        FaunaCropsBlock[] crops = {
                Registration.CROPS.get(),
                Registration.ANEMONEMAL.get(),
                Registration.SHROOMS.get(),
                Registration.MAGNEMONE.get()
        };
        BlockPos[] positions = {
                new BlockPos(0, 1, 0),
                new BlockPos(1, 1, 0),
                new BlockPos(2, 1, 0),
                new BlockPos(0, 1, 1)
        };
        BlockPos[] soils = {
                new BlockPos(0, 0, 0),
                new BlockPos(1, 0, 0),
                new BlockPos(2, 0, 0),
                new BlockPos(0, 0, 1)
        };

        for (int i = 0; i < crops.length; i++) {
            FaunaCropsBlock crop = crops[i];
            BlockPos pos = positions[i];

            // give each plant valid soil so neighbour updates do not remove it
            helper.setBlock(soils[i], i == 1 || i == 3 ? Blocks.STONE : (i == 2 ? Blocks.SOUL_SOIL : Blocks.GRASS_BLOCK));
            BlockState start = crop.getStateForAge(0);
            helper.setBlock(pos, start);

            Player player = helper.makeMockPlayer();
            ItemStack stack = new ItemStack(Registration.FERTILIZER.get(), 3);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);

            InteractionResult result = useOn(helper, player, pos);
            int after = crop.getAge(helper.getBlockState(pos));

            helper.assertTrue(result.consumesAction(),
                    "fertilizer should succeed on " + crop.getDescriptionId());
            helper.assertTrue(after >= 1 && after <= Math.min(crop.getMaxAge(), 3),
                    "fertilizer should grow " + crop.getDescriptionId() + " by 1 to 3, got age " + after);
            helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).getCount() == 2,
                    "fertilizer should shrink the stack on " + crop.getDescriptionId());
        }

        helper.succeed();
    }

    @GameTest(template = "empty3x3x3")
    public static void fertilizerDoesNothingOnMatureCrop(GameTestHelper helper) {
        FaunaCropsBlock crop = Registration.CROPS.get();
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(pos, crop.getStateForAge(crop.getMaxAge()));

        Player player = helper.makeMockPlayer();
        ItemStack stack = new ItemStack(Registration.FERTILIZER.get(), 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        InteractionResult result = useOn(helper, player, pos);

        helper.assertTrue(!result.consumesAction(), "fertilizer should pass on a mature crop");
        helper.assertTrue(crop.getAge(helper.getBlockState(pos)) == crop.getMaxAge(),
                "mature crop age should not change");
        helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).getCount() == 3,
                "fertilizer should not be consumed on a mature crop");
        helper.succeed();
    }

    /** Right clicks the given relative position with the player's held item. */
    private static InteractionResult useOn(GameTestHelper helper, Player player, BlockPos pos) {
        BlockPos absolute = helper.absolutePos(pos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
        return player.getItemInHand(InteractionHand.MAIN_HAND)
                .useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }
}

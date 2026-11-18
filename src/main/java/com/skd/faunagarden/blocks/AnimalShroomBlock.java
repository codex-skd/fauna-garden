package com.skd.faunagarden.blocks;

import com.skd.faunagarden.core.AnimalTags;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.items.AnimalSeedsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

/**
 * Logic for nether crops.
 */
public class AnimalShroomBlock extends FaunaCropsBlock {
    public AnimalShroomBlock(Properties props, TagKey<EntityType<?>> tag) {
        super(props, tag);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(AnimalTags.SHROOM_SOIL);
    }

    // override to remove the light check
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos down = pos.below();
        return this.mayPlaceOn(level.getBlockState(down), level, down);
    }

    @Override
    protected AnimalSeedsItem getSeed() {
        return Registration.SPORES.get();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = this.getAge(state);
        if (i < this.getMaxAge() && CommonHooks.canCropGrow(level, pos, state, random.nextInt(10) == 0)) {
            state = state.setValue(AGE, i + 1);
            level.setBlock(pos, state, 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }
}

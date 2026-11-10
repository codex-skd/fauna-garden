package com.skd.faunagarden.blocks;

import com.skd.faunagarden.items.AnimalSeedsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Common logic for water and lava crops.
 */
public class AnemonemalBlock extends FaunaCropsBlock {
    private final Supplier<? extends FlowingFluid> fluid;
    private final TagKey<Fluid> fluidTag;
    private final Supplier<? extends AnimalSeedsItem> seed;

    public AnemonemalBlock(Properties props, TagKey<EntityType<?>> entityTag,
                           Supplier<? extends FlowingFluid> fluid, TagKey<Fluid> fluidTag,
                           Supplier<? extends AnimalSeedsItem> seed) {
        super(props, entityTag);
        this.fluid = fluid;
        this.fluidTag = fluidTag;
        this.seed = seed;
    }

    @Override
    protected AnimalSeedsItem getSeed() {
        return this.seed.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return fluid.is(fluidTag) && fluid.getAmount() == 8 ? super.getStateForPlacement(context) : null;
    }

    /* Fluid logic */

    @Override
    protected BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos,
                                     Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        BlockState state = super.updateShape(stateIn, level, ticks, currentPos, facing, facingPos, facingState, random);
        if (!state.isAir()) {
            Fluid fluid = this.fluid.get();
            ticks.scheduleTick(currentPos, fluid, fluid.getTickDelay(level));
        }

        return state;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return fluid.get().getSource(false);
    }
}

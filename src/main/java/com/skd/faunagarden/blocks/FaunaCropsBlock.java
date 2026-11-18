package com.skd.faunagarden.blocks;

import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.core.AnimalTags;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.core.Utils;
import com.skd.faunagarden.items.AnimalSeedsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Base crop logic, used for plains crops directly.
 */
public class FaunaCropsBlock extends CropBlock implements EntityBlock {
    protected final TagKey<EntityType<?>> entityTag;

    public FaunaCropsBlock(Properties props, TagKey<EntityType<?>> tag) {
        super(props);
        this.entityTag = tag;
    }

    /* Crop properties */

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(AnimalTags.CROP_SOIL);
    }

    /**
     * Gets the seed item for this crop.
     * @return  Item for this crop
     */
    protected AnimalSeedsItem getSeed() {
        return Registration.SEEDS.get();
    }

    /* Seed logic */

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FaunaCropsBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // set the crop's entity
        if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity animal) {
            Utils.getEntityID(stack).ifPresent(animal::setEntity);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // if the block changed, spawn the animal
        if (state.getBlock() != newState.getBlock()) {
            // assuming we have the block entity to use
            if (getAge(state) >= getMaxAge()) {
                if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity animal) {
                    animal.spawnAnimal();
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
            // otherwise, if the age lowered from max, spawn the animal
            // for right click harvest
        } else if (state.getBlock() == this && getAge(state) >= getMaxAge() && getAge(newState) < getMaxAge()) {
            if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity animal) {
                animal.spawnAnimal();
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(getSeed());
        if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity animal) {
            Utils.setEntityId(stack, animal.getEntityId());
        }
        return stack;
    }

    /* Bonemeal */

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return Config.canBonemeal.get();
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.getRandom(), 1, 3);
    }
}

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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
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
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack stack = new ItemStack(getSeed());
        if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity animal) {
            Utils.setEntityId(stack, animal.getEntityId());
        }
        return stack;
    }

    /* Bonemeal */

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return Config.canBonemeal.get();
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.getRandom(), 1, 3);
    }
}

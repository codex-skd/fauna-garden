package com.skd.faunagarden.items;

import com.skd.faunagarden.blocks.FaunaCropsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.List;

/**
 * Fertilizer that advances the growth of Fauna Garden crops, regardless of the
 * vanilla bone meal config option.
 */
public class AnimalFertilizerItem extends Item {

    public AnimalFertilizerItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        // only act on this mod's growing blocks
        if (!(state.getBlock() instanceof FaunaCropsBlock crop) || crop.isMaxAge(state)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            int age = Math.min(crop.getMaxAge(), crop.getAge(state) + Mth.nextInt(level.getRandom(), 1, 3));
            // flag 2 keeps the block entity data while syncing the new age
            level.setBlock(pos, crop.getStateForAge(age), 2);
            // vanilla bone meal particles and sound, broadcast to clients
            level.levelEvent(1505, pos, 15);

            Player player = context.getPlayer();
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}

package com.skd.faunagarden.items;

import com.skd.faunagarden.core.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * Item that allows planting the crop.
 */
public class AnimalSeedsItem extends BlockItem {

    public AnimalSeedsItem(Block crops, Properties props) {
        super(crops, props);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Utils.getEntityID(stack)
                .flatMap(id -> BuiltInRegistries.ENTITY_TYPE.getOptional(id))
                .map(EntityType::getDescriptionId)
                .map(key -> Component.translatable(this.getDescriptionId(), Component.translatable(key)))
                .orElseGet(() -> Component.translatable(this.getDescriptionId() + ".default"));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}

package com.skd.faunagarden.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class Utils {
    private Utils() {}

    /**
     * Gets the entity ID stored on the given stack.
     * @param stack  Stack to read the component from
     * @return  Entity resource location, if present
     */
    public static Optional<ResourceLocation> getEntityID(ItemStack stack) {
        return Optional.ofNullable(stack.get(Registration.ENTITY.get()));
    }

    /**
     * Sets the entity ID for a given item stack.
     * @param stack   Stack to set the component on, will be modified
     * @param entity  Entity resource location
     * @return  Stack with the component set
     */
    public static ItemStack setEntityId(ItemStack stack, @Nullable ResourceLocation entity) {
        if (entity == null) {
            return stack;
        }
        stack.set(Registration.ENTITY.get(), entity);
        return stack;
    }

    /**
     * Fills a stack of containers, shrinking it by 1.
     * @param player     Player to give the item to and for creative checks
     * @param container  Container stack, may contain more than 1
     * @param filled     Filled container stack
     * @return  Filled stack if 1 container, leftover container if more than 1, dropping the filled
     */
    public static ItemStack fillContainer(Player player, ItemStack container, ItemStack filled) {
        container = container.copy();
        if (!player.isCreative()) {
            container.shrink(1);
            if (container.isEmpty()) {
                return filled;
            }
        }
        if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
        return container;
    }
}

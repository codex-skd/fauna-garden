package com.skd.faunagarden.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.skd.faunagarden.core.Utils;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Item tint source for seeds and spores: tints the stack with the spawn egg color of the animal it
 * stores, like the crop block models do. Index 0 is the primary color, index 1 the secondary color.
 * Registered as {@code fauna_garden:entity_egg} and used by the item definitions.
 */
public record EntityEggTintSource(int index) implements ItemTintSource {
    public static final MapCodec<EntityEggTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("index").forGetter(EntityEggTintSource::index)
    ).apply(instance, EntityEggTintSource::new));

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        return CropTintSource.colorFor(Utils.getEntityID(stack).orElse(null), this.index);
    }

    @Override
    public MapCodec<EntityEggTintSource> type() {
        return CODEC;
    }
}

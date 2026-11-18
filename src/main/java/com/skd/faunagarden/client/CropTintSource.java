package com.skd.faunagarden.client;

import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Tint source for the crop block models.
 * <p>
 * In 26.x spawn egg colors are baked into their textures and are no longer exposed through
 * {@code SpawnEggItem}, so the vanilla egg colors are kept in {@link EggColors}; entities without one
 * fall back to an entity-derived color on the overlay layer (tint index 1) and an untinted base layer.
 */
public final class CropTintSource implements BlockTintSource {
    private final int tintIndex;

    public CropTintSource(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity crops) {
            return colorFor(crops.getEntityId(), this.tintIndex);
        }
        return -1;
    }

    /**
     * Vanilla spawn egg color of the entity (primary for tint index 0, secondary for index 1). Entities
     * without a known egg color keep the base layer untinted and get a stable, entity-derived color on
     * the overlay layer.
     */
    static int colorFor(@Nullable Identifier entityId, int tintIndex) {
        if (entityId == null) {
            return -1;
        }
        int egg = EggColors.get(entityId.toString(), tintIndex);
        if (egg != -1) {
            return egg;
        }
        if (tintIndex == 0) {
            return -1;
        }
        int hash = entityId.hashCode();
        float hue = (hash & 0xFF) / 255.0F;
        float saturation = 0.45F + ((hash >> 8) & 0x3F) / 63.0F * 0.35F;
        float value = 0.80F + ((hash >> 16) & 0x3F) / 63.0F * 0.20F;
        return Mth.hsvToArgb(hue, saturation, value, 255);
    }

    @Override
    public Set<Property<?>> relevantProperties() {
        return Set.of();
    }
}

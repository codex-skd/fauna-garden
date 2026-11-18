package com.skd.faunagarden.datagen;

import com.skd.faunagarden.core.AnimalTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Generates block tags used by the mod.
 */
public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(AnimalTags.CROP_SOIL).add(Blocks.GRASS_BLOCK, Blocks.PODZOL);
        this.tag(AnimalTags.SHROOM_SOIL).add(Blocks.SOUL_SOIL, Blocks.SOUL_SAND);
    }
}

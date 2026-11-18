package com.skd.faunagarden.datagen;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.AnimalTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Generates block tags used by the mod.
 */
public class ModBlockTagsProvider extends VanillaBlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FaunaGarden.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(AnimalTags.CROP_SOIL).add(
                Blocks.GRASS_BLOCK.builtInRegistryHolder().key(),
                Blocks.PODZOL.builtInRegistryHolder().key());
        this.tag(AnimalTags.SHROOM_SOIL).add(
                Blocks.SOUL_SOIL.builtInRegistryHolder().key(),
                Blocks.SOUL_SAND.builtInRegistryHolder().key());
    }
}

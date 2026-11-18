package com.skd.faunagarden.datagen;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.AnimalTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Generates entity type tags used by the mod.
 */
public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FaunaGarden.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(AnimalTags.PLANTABLE).addTags(AnimalTags.ANIMAL_CROPS, AnimalTags.ANEMONEMAL,
                AnimalTags.ANIMAL_SHROOMS, AnimalTags.MAGNEMONES);

        // these tags directly contain things plantable but not droppable
        this.tag(AnimalTags.ANIMAL_CROPS).addTag(AnimalTags.DROPPABLE_ANIMAL_CROPS);
        this.tag(AnimalTags.ANEMONEMAL).addTag(AnimalTags.DROPPABLE_ANEMONEMAL);
        this.tag(AnimalTags.ANIMAL_SHROOMS).addTag(AnimalTags.DROPPABLE_ANIMAL_SHROOMS);
        this.tag(AnimalTags.MAGNEMONES).addTag(AnimalTags.DROPPABLE_MAGNEMONES);

        // overworld
        this.tag(AnimalTags.DROPPABLE_ANIMAL_CROPS).add(
                        EntityType.CHICKEN, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.RABBIT,
                        EntityType.SHEEP, EntityType.BEE, EntityType.CAT, EntityType.FOX, EntityType.OCELOT,
                        EntityType.PARROT, EntityType.WOLF, EntityType.DONKEY, EntityType.HORSE, EntityType.MULE,
                        EntityType.LLAMA, EntityType.PANDA, EntityType.POLAR_BEAR, EntityType.VILLAGER)
                .addOptional(ResourceLocation.fromNamespaceAndPath("waddles", "adelie_penguin"));
        this.tag(AnimalTags.DROPPABLE_ANEMONEMAL).add(
                EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH,
                EntityType.DOLPHIN, EntityType.SQUID, EntityType.TURTLE);
        // nether
        this.tag(AnimalTags.DROPPABLE_ANIMAL_SHROOMS).add(EntityType.HOGLIN, EntityType.PIGLIN);
        this.tag(AnimalTags.DROPPABLE_MAGNEMONES).add(EntityType.STRIDER);

        // allows limiting pollen to just some mobs
        this.tag(AnimalTags.POLLEN_REACTIVE).addTag(AnimalTags.PLANTABLE);
    }
}

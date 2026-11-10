package com.skd.faunagarden.datagen;

import com.skd.faunagarden.core.AnimalTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypeIds;

import java.util.concurrent.CompletableFuture;

/**
 * Generates entity type tags used by the mod.
 */
public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
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
                EntityTypeIds.CHICKEN, EntityTypeIds.COW, EntityTypeIds.MOOSHROOM, EntityTypeIds.PIG, EntityTypeIds.RABBIT,
                EntityTypeIds.SHEEP, EntityTypeIds.BEE, EntityTypeIds.CAT, EntityTypeIds.FOX, EntityTypeIds.OCELOT,
                EntityTypeIds.PARROT, EntityTypeIds.WOLF, EntityTypeIds.DONKEY, EntityTypeIds.HORSE, EntityTypeIds.MULE,
                EntityTypeIds.LLAMA, EntityTypeIds.PANDA, EntityTypeIds.POLAR_BEAR, EntityTypeIds.VILLAGER);
        // Optional compat entry: the mod is not required to be present.
        this.getOrCreateRawBuilder(AnimalTags.DROPPABLE_ANIMAL_CROPS)
                .addOptionalElement(Identifier.fromNamespaceAndPath("waddles", "adelie_penguin"));
        this.tag(AnimalTags.DROPPABLE_ANEMONEMAL).add(
                EntityTypeIds.COD, EntityTypeIds.PUFFERFISH, EntityTypeIds.SALMON, EntityTypeIds.TROPICAL_FISH,
                EntityTypeIds.DOLPHIN, EntityTypeIds.SQUID, EntityTypeIds.TURTLE);
        // nether
        this.tag(AnimalTags.DROPPABLE_ANIMAL_SHROOMS).add(EntityTypeIds.HOGLIN, EntityTypeIds.PIGLIN);
        this.tag(AnimalTags.DROPPABLE_MAGNEMONES).add(EntityTypeIds.STRIDER);

        // allows limiting pollen to just some mobs
        this.tag(AnimalTags.POLLEN_REACTIVE).addTag(AnimalTags.PLANTABLE);
    }
}

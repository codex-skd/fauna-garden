package com.skd.faunagarden.core;

import com.skd.faunagarden.items.AnimalPollenItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import javax.annotation.Nullable;

public class Config {

    private static final Builder BUILDER;
    public static final ForgeConfigSpec SPEC;

    // types
    public static final AnimalCropType animalCropDrops;
    public static final AnimalCropType anemonemals;
    public static final AnimalCropType animalShrooms;
    public static final AnimalCropType magnemones;
    // general
    public static final BooleanValue canBonemeal;
    public static final EnumValue<AnimalPollenItem.Action> pollenAction;
    // grass drops
    public static final BooleanValue dropAnimalPollen;

    static {
        BUILDER = new Builder();

        // crop
        BUILDER.push("general");
        {
            canBonemeal = BUILDER
                    .comment("Determines if bonemeal can be applied to the animal crops, anemonemals, animal shrooms, and magnemones")
                    .define("bonemeal", false);
            pollenAction = BUILDER
                    .comment("If CONSUME, the entity is killed when pollen are used, though no items are dropped",
                                      "If DAMAGE, the entity will take 2 hearts of damage when pollen are used")
                    .defineEnum("action", AnimalPollenItem.Action.DAMAGE);
        }
        BUILDER.pop();

        // grass drops
        BUILDER.push("grassDrops");
        dropAnimalPollen = BUILDER
                .comment("If true, grass will rarely drop animal pollen")
                .define("animal_pollen", true);
        BooleanValue dropAnimalSeeds = BUILDER
                .comment("If true, grass will rarely drop a random animal seed")
                .define("animal_seeds", false);
        BooleanValue dropAnemonemals = BUILDER
                .comment("If true, sea grass will rarely drop a random anemonemal")
                .define("anemonemal", false);
        BooleanValue dropAnimalShrooms = BUILDER
                .comment("If true, nether sprouts will rarely drop a random animal shroom")
                .define("animal_shrooms", false);
        BooleanValue dropMagnemones = BUILDER
                .comment("If true, nether sprouts will rarely drop a random magnemones")
                .define("magnemones", false);
        BUILDER.pop();

        animalCropDrops = new AnimalCropType(AnimalTags.DROPPABLE_ANIMAL_CROPS, dropAnimalSeeds);
        anemonemals   = new AnimalCropType(AnimalTags.DROPPABLE_ANEMONEMAL, dropAnemonemals);
        animalShrooms = new AnimalCropType(AnimalTags.DROPPABLE_ANIMAL_SHROOMS, dropAnimalShrooms);
        magnemones    = new AnimalCropType(AnimalTags.DROPPABLE_MAGNEMONES, dropMagnemones);

        SPEC = BUILDER.build();
    }

    // registered in FaunaGarden
    public static void configChanged(final ModConfigEvent event) {}

    /** Config setup for each type of animal crops */
    public static class AnimalCropType {
        private final TagKey<EntityType<?>> tag;
        private final BooleanValue drop;

        protected AnimalCropType(TagKey<EntityType<?>> tag, BooleanValue drop) {
            this.tag = tag;
            this.drop = drop;
        }

        /**
         * Returns true if this type of animal crops drops.
         */
        public boolean doesDrop() {
            if (!drop.get()) {
                return false;
            }
            return BuiltInRegistries.ENTITY_TYPE.getTag(tag).map(holders -> holders.size() > 0).orElse(false);
        }

        /** Gets a random value of this crop drop type */
        @Nullable
        public EntityType<?> getRandomValue(RandomSource random) {
            if (doesDrop()) {
                return BuiltInRegistries.ENTITY_TYPE.getTag(tag)
                        .flatMap(holders -> holders.getRandomElement(random))
                        .map(Holder::value)
                        .orElse(null);
            }
            return null;
        }
    }
}

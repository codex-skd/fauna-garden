package com.skd.faunagarden.core;

import com.mojang.serialization.MapCodec;
import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.blocks.AnemonemalBlock;
import com.skd.faunagarden.blocks.AnimalShroomBlock;
import com.skd.faunagarden.blocks.FaunaCropsBlock;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.items.AnimalFertilizerItem;
import com.skd.faunagarden.items.AnimalPollenItem;
import com.skd.faunagarden.items.AnimalSeedsItem;
import com.skd.faunagarden.json.ConfigCondition;
import com.skd.faunagarden.json.RandomAnimalLootFunction;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.Set;

/**
 * Static holders for every registry object added by the mod.
 *
 * <p>Fabric has no {@code DeferredRegister}; everything is registered eagerly with
 * {@link Registry#register} during {@link #register()}, which is called from the mod
 * initialiser. Registration order matters: blocks first, then items that reference them,
 * then the block entity type that references the blocks.
 */
public final class Registration {
    /** Component storing the entity id shown/planted by a seed or spore item. */
    public static DataComponentType<Identifier> ENTITY;

    /** Condition matching a config property. */
    public static MapCodec<? extends LootItemCondition> CONFIG_CONDITION;
    /** Function setting a random entity of the configured type. */
    public static MapCodec<? extends LootItemFunction> RANDOM_ANIMAL;

    // Blocks
    public static FaunaCropsBlock CROPS;
    public static AnemonemalBlock ANEMONEMAL;
    public static AnimalShroomBlock SHROOMS;
    public static AnemonemalBlock MAGNEMONE;

    // Block entity type
    public static BlockEntityType<FaunaCropsBlockEntity> CROPS_BE;

    // Items
    public static AnimalSeedsItem SEEDS;
    public static AnimalSeedsItem ANEMONEMAL_SEEDS;
    public static AnimalSeedsItem SPORES;
    public static AnimalSeedsItem MAGNEMONE_SPORES;
    public static AnimalPollenItem POLLEN;
    public static AnimalFertilizerItem FERTILIZER;

    // Creative tab
    public static CreativeModeTab TAB;

    private Registration() {}

    /** Registers all mod content. Called once from the mod initialiser. */
    public static void register() {
        ENTITY = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, getResource("entity"),
                DataComponentType.<Identifier>builder()
                        .persistent(Identifier.CODEC)
                        .networkSynchronized(Identifier.STREAM_CODEC)
                        .cacheEncoding()
                        .build());

        CONFIG_CONDITION = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, getResource("config"),
                ConfigCondition.CODEC);
        RANDOM_ANIMAL = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, getResource("random_animal"),
                RandomAnimalLootFunction.CODEC);

        // Blocks first: items and the block entity type reference them.
        CROPS = registerBlock("crops",
                new FaunaCropsBlock(plantProperties("crops"), AnimalTags.ANIMAL_CROPS));
        ANEMONEMAL = registerBlock("anemonemal",
                new AnemonemalBlock(plantProperties("anemonemal"), AnimalTags.ANEMONEMAL,
                        () -> net.minecraft.world.level.material.Fluids.WATER, net.minecraft.tags.FluidTags.WATER,
                        () -> Registration.ANEMONEMAL_SEEDS));
        SHROOMS = registerBlock("shrooms",
                new AnimalShroomBlock(netherProperties("shrooms"), AnimalTags.ANIMAL_SHROOMS));
        MAGNEMONE = registerBlock("magnemone",
                new AnemonemalBlock(netherProperties("magnemone"), AnimalTags.MAGNEMONES,
                        () -> net.minecraft.world.level.material.Fluids.LAVA, net.minecraft.tags.FluidTags.LAVA,
                        () -> Registration.MAGNEMONE_SPORES));

        SEEDS = registerItem("seeds", new AnimalSeedsItem(CROPS, seedProperties("seeds")));
        ANEMONEMAL_SEEDS = registerItem("anemonemal", new AnimalSeedsItem(ANEMONEMAL, seedProperties("anemonemal")));
        SPORES = registerItem("spores", new AnimalSeedsItem(SHROOMS, seedProperties("spores")));
        MAGNEMONE_SPORES = registerItem("magnemone", new AnimalSeedsItem(MAGNEMONE, seedProperties("magnemone")));
        POLLEN = registerItem("pollen", new AnimalPollenItem(itemProperties("pollen")
                .compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
        FERTILIZER = registerItem("fertilizer", new AnimalFertilizerItem(itemProperties("fertilizer")
                .compostable(ContextIntProviders.COMPOSTABLE_LOW)));

        CROPS_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, getResource("crops"),
                new BlockEntityType<>(FaunaCropsBlockEntity::new, Set.of(CROPS, ANEMONEMAL, SHROOMS, MAGNEMONE)));

        TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, getResource("fauna_garden"),
                FabricCreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.fauna_garden"))
                        .icon(() -> new ItemStack(SEEDS))
                        .displayItems((parameters, output) -> {
                            addVariants(output, SEEDS, AnimalTags.ANIMAL_CROPS);
                            addVariants(output, ANEMONEMAL_SEEDS, AnimalTags.ANEMONEMAL);
                            addVariants(output, SPORES, AnimalTags.ANIMAL_SHROOMS);
                            addVariants(output, MAGNEMONE_SPORES, AnimalTags.MAGNEMONES);
                            output.accept(POLLEN);
                            output.accept(FERTILIZER);
                        })
                        .build());

    }

    private static void addVariants(CreativeModeTab.Output output, Item item, TagKey<EntityType<?>> tag) {
        for (Holder<EntityType<?>> holder : BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(tag)) {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(holder.value());
            if (id != null) {
                output.accept(Utils.setEntityId(new ItemStack(item), id));
            }
        }
    }

    private static <T extends Block> T registerBlock(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, getResource(name), block);
    }

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, getResource(name), item);
    }

    /** Gets a resource location in the Fauna Garden domain. */
    public static Identifier getResource(String name) {
        return Identifier.fromNamespaceAndPath(FaunaGarden.MOD_ID, name);
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, getResource(name));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, getResource(name));
    }

    private static Item.Properties itemProperties(String name) {
        return new Item.Properties().setId(itemKey(name));
    }

    private static Item.Properties seedProperties(String name) {
        return new Item.Properties().setId(itemKey(name)).useItemDescriptionPrefix()
                .compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM);
    }

    private static BlockBehaviour.Properties plantProperties(String name) {
        return BlockBehaviour.Properties.of()
                .setId(blockKey(name))
                .mapColor(MapColor.PLANT)
                .randomTicks()
                .strength(0)
                .sound(SoundType.CROP)
                .noCollision()
                .pushReaction(PushReaction.POPPED);
    }

    private static BlockBehaviour.Properties netherProperties(String name) {
        return BlockBehaviour.Properties.of()
                .setId(blockKey(name))
                .mapColor(MapColor.COLOR_RED)
                .randomTicks()
                .strength(0)
                .sound(SoundType.NETHER_WART)
                .noCollision()
                .pushReaction(PushReaction.POPPED);
    }
}

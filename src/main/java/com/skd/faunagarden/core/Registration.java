package com.skd.faunagarden.core;

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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

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
    public static DataComponentType<ResourceLocation> ENTITY;

    /** Condition matching a config property. */
    public static LootItemConditionType CONFIG_CONDITION;
    /** Function setting a random entity of the configured type. */
    public static LootItemFunctionType<RandomAnimalLootFunction> RANDOM_ANIMAL;

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
                DataComponentType.<ResourceLocation>builder()
                        .persistent(ResourceLocation.CODEC)
                        .networkSynchronized(ResourceLocation.STREAM_CODEC)
                        .cacheEncoding()
                        .build());

        CONFIG_CONDITION = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, getResource("config"),
                new LootItemConditionType(ConfigCondition.CODEC));
        RANDOM_ANIMAL = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, getResource("random_animal"),
                RandomAnimalLootFunction.TYPE);

        // Blocks first: items and the block entity type reference them.
        CROPS = registerBlock("crops",
                new FaunaCropsBlock(plantProperties(), AnimalTags.ANIMAL_CROPS));
        ANEMONEMAL = registerBlock("anemonemal",
                new AnemonemalBlock(plantProperties(), AnimalTags.ANEMONEMAL,
                        () -> net.minecraft.world.level.material.Fluids.WATER, net.minecraft.tags.FluidTags.WATER,
                        () -> Registration.ANEMONEMAL_SEEDS));
        SHROOMS = registerBlock("shrooms",
                new AnimalShroomBlock(netherProperties(), AnimalTags.ANIMAL_SHROOMS));
        MAGNEMONE = registerBlock("magnemone",
                new AnemonemalBlock(netherProperties(), AnimalTags.MAGNEMONES,
                        () -> net.minecraft.world.level.material.Fluids.LAVA, net.minecraft.tags.FluidTags.LAVA,
                        () -> Registration.MAGNEMONE_SPORES));

        SEEDS = registerItem("seeds", new AnimalSeedsItem(CROPS, new Item.Properties()));
        ANEMONEMAL_SEEDS = registerItem("anemonemal", new AnimalSeedsItem(ANEMONEMAL, new Item.Properties()));
        SPORES = registerItem("spores", new AnimalSeedsItem(SHROOMS, new Item.Properties()));
        MAGNEMONE_SPORES = registerItem("magnemone", new AnimalSeedsItem(MAGNEMONE, new Item.Properties()));
        POLLEN = registerItem("pollen", new AnimalPollenItem(new Item.Properties()));
        FERTILIZER = registerItem("fertilizer", new AnimalFertilizerItem(new Item.Properties()));

        CROPS_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, getResource("crops"),
                BlockEntityType.Builder.of(FaunaCropsBlockEntity::new, CROPS, ANEMONEMAL, SHROOMS, MAGNEMONE)
                        .build(null));

        TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, getResource("fauna_garden"),
                FabricItemGroup.builder()
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup.fauna_garden"))
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

        registerComposting();
    }

    /** Registers the composter values, matching the previous loader implementation. */
    private static void registerComposting() {
        CompostingChanceRegistry.INSTANCE.add(SEEDS, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(ANEMONEMAL_SEEDS, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(SPORES, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(MAGNEMONE_SPORES, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(POLLEN, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(FERTILIZER, 0.3f);
    }

    private static void addVariants(CreativeModeTab.Output output, Item item, TagKey<EntityType<?>> tag) {
        BuiltInRegistries.ENTITY_TYPE.getTag(tag).ifPresent(holders -> holders.forEach(holder -> {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(holder.value());
            if (id != null) {
                output.accept(Utils.setEntityId(new ItemStack(item), id));
            }
        }));
    }

    private static <T extends net.minecraft.world.level.block.Block> T registerBlock(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, getResource(name), block);
    }

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, getResource(name), item);
    }

    /** Gets a resource location in the Fauna Garden domain. */
    public static ResourceLocation getResource(String name) {
        return ResourceLocation.fromNamespaceAndPath(FaunaGarden.MOD_ID, name);
    }

    private static BlockBehaviour.Properties plantProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .randomTicks()
                .strength(0)
                .sound(SoundType.CROP)
                .noCollission()
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties netherProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .randomTicks()
                .strength(0)
                .sound(SoundType.NETHER_WART)
                .noCollission()
                .pushReaction(PushReaction.DESTROY);
    }
}

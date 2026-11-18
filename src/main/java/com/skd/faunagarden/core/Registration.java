package com.skd.faunagarden.core;

import com.mojang.serialization.Codec;
import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.blocks.AnemonemalBlock;
import com.skd.faunagarden.blocks.FaunaCropsBlock;
import com.skd.faunagarden.blocks.AnimalShroomBlock;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.items.AnimalFertilizerItem;
import com.skd.faunagarden.items.AnimalPollenItem;
import com.skd.faunagarden.items.AnimalSeedsItem;
import com.skd.faunagarden.json.AddEntryLootModifier;
import com.skd.faunagarden.json.ConfigCondition;
import com.skd.faunagarden.json.RandomAnimalLootFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class Registration {
    // Loot types
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, FaunaGarden.MOD_ID);
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, FaunaGarden.MOD_ID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, FaunaGarden.MOD_ID);

    /** Condition matching a config property. */
    public static final RegistryObject<LootItemConditionType> CONFIG_CONDITION = LOOT_CONDITION_TYPES.register("config",
            () -> new LootItemConditionType(ConfigCondition.SERIALIZER));
    /** Function setting a random entity of the configured type. */
    public static final RegistryObject<LootItemFunctionType> RANDOM_ANIMAL = LOOT_FUNCTION_TYPES.register("random_animal",
            () -> new LootItemFunctionType(RandomAnimalLootFunction.SERIALIZER));
    /** Global loot modifier adding an extra entry to a loot table. */
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ENTRY = LOOT_MODIFIER_SERIALIZERS.register("add_entry",
            () -> AddEntryLootModifier.CODEC);

    // Blocks
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, FaunaGarden.MOD_ID);

    public static final RegistryObject<FaunaCropsBlock> CROPS = BLOCKS.register("crops",
            () -> new FaunaCropsBlock(plantProperties(), AnimalTags.ANIMAL_CROPS));
    public static final RegistryObject<AnemonemalBlock> ANEMONEMAL = BLOCKS.register("anemonemal",
            () -> new AnemonemalBlock(plantProperties(), AnimalTags.ANEMONEMAL,
                    () -> net.minecraft.world.level.material.Fluids.WATER, net.minecraft.tags.FluidTags.WATER,
                    () -> Registration.ANEMONEMAL_SEEDS.get()));
    public static final RegistryObject<AnimalShroomBlock> SHROOMS = BLOCKS.register("shrooms",
            () -> new AnimalShroomBlock(netherProperties(), AnimalTags.ANIMAL_SHROOMS));
    public static final RegistryObject<AnemonemalBlock> MAGNEMONE = BLOCKS.register("magnemone",
            () -> new AnemonemalBlock(netherProperties(), AnimalTags.MAGNEMONES,
                    () -> net.minecraft.world.level.material.Fluids.LAVA, net.minecraft.tags.FluidTags.LAVA,
                    () -> Registration.MAGNEMONE_SPORES.get()));

    // Block entity type
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FaunaGarden.MOD_ID);

    public static final RegistryObject<BlockEntityType<FaunaCropsBlockEntity>> CROPS_BE = BLOCK_ENTITIES.register("crops",
            () -> BlockEntityType.Builder.of(FaunaCropsBlockEntity::new,
                    CROPS.get(), ANEMONEMAL.get(), SHROOMS.get(), MAGNEMONE.get()).build(null));

    // Items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FaunaGarden.MOD_ID);

    public static final RegistryObject<AnimalSeedsItem> SEEDS = ITEMS.register("seeds",
            () -> new AnimalSeedsItem(CROPS.get(), new Item.Properties()));
    public static final RegistryObject<AnimalSeedsItem> ANEMONEMAL_SEEDS = ITEMS.register("anemonemal",
            () -> new AnimalSeedsItem(ANEMONEMAL.get(), new Item.Properties()));
    public static final RegistryObject<AnimalSeedsItem> SPORES = ITEMS.register("spores",
            () -> new AnimalSeedsItem(SHROOMS.get(), new Item.Properties()));
    public static final RegistryObject<AnimalSeedsItem> MAGNEMONE_SPORES = ITEMS.register("magnemone",
            () -> new AnimalSeedsItem(MAGNEMONE.get(), new Item.Properties()));
    public static final RegistryObject<AnimalPollenItem> POLLEN = ITEMS.register("pollen",
            () -> new AnimalPollenItem(new Item.Properties()));
    public static final RegistryObject<AnimalFertilizerItem> FERTILIZER = ITEMS.register("fertilizer",
            () -> new AnimalFertilizerItem(new Item.Properties()));

    // Creative tab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FaunaGarden.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("fauna_garden",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fauna_garden"))
                    .icon(() -> new ItemStack(SEEDS.get()))
                    .build());

    private Registration() {}

    /** Registers all deferred registers with the mod event bus. */
    public static void register(IEventBus modBus) {
        LOOT_CONDITION_TYPES.register(modBus);
        LOOT_FUNCTION_TYPES.register(modBus);
        LOOT_MODIFIER_SERIALIZERS.register(modBus);
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);
    }

    /** Common setup work, such as composter values. */
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(SEEDS.get(), 0.5f);
            ComposterBlock.COMPOSTABLES.put(ANEMONEMAL_SEEDS.get(), 0.5f);
            ComposterBlock.COMPOSTABLES.put(SPORES.get(), 0.5f);
            ComposterBlock.COMPOSTABLES.put(MAGNEMONE_SPORES.get(), 0.5f);
            ComposterBlock.COMPOSTABLES.put(POLLEN.get(), 0.5f);
            ComposterBlock.COMPOSTABLES.put(FERTILIZER.get(), 0.3f);
        });
    }

    /** Adds this mod's content to its creative tab. */
    public static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() != TAB.get()) {
            return;
        }
        addVariants(event, SEEDS.get(), AnimalTags.ANIMAL_CROPS);
        addVariants(event, ANEMONEMAL_SEEDS.get(), AnimalTags.ANEMONEMAL);
        addVariants(event, SPORES.get(), AnimalTags.ANIMAL_SHROOMS);
        addVariants(event, MAGNEMONE_SPORES.get(), AnimalTags.MAGNEMONES);
        event.accept(POLLEN.get());
        event.accept(FERTILIZER.get());
    }

    private static void addVariants(CreativeModeTab.Output output, Item item, TagKey<EntityType<?>> tag) {
        BuiltInRegistries.ENTITY_TYPE.getTag(tag).ifPresent(holders -> holders.forEach(holder -> {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(holder.value());
            if (id != null) {
                output.accept(Utils.setEntityId(new ItemStack(item), id));
            }
        }));
    }

    /** Gets a resource location in the Fauna Garden domain. */
    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(FaunaGarden.MOD_ID, name);
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

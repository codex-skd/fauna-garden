package com.skd.faunagarden.json;

import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

/**
 * Reproduces the old Forge/NeoForge global loot modifiers with Fabric's
 * {@link LootTableEvents#MODIFY} event.
 *
 * <p>The former modifiers (grass, sea grass and nether sprouts) were applied to every loot
 * table and used a block-state condition to decide whether to run. On Fabric a pool is added
 * only to the vanilla tables that those blocks use, with the same chances, entries, functions
 * and config gates as the old JSON.
 *
 * <p>The old {@code require_empty} flag cannot be expressed at pool level on Fabric. For these
 * blocks the only vanilla drop is the block itself when broken with shears, and the modifier
 * already ignored shears with an inverted {@code match_tool} condition, so the shears check is
 * the closest and behaviourally equivalent replacement.
 */
public final class LootModifications {
    /** Tag used by the old modifiers to ignore shears. Fabric API ships this convention tag. */
    private static final TagKey<Item> SHEARS = TagKey.create(Registries.ITEM,
            net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "tools/shear"));

    private LootModifications() {}

    /** Registers the loot table modifications. Called once during mod initialisation. */
    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (matches(key, Blocks.SHORT_GRASS) || matches(key, Blocks.FERN)) {
                tableBuilder.withPool(grassPool(registries));
            } else if (matches(key, Blocks.TALL_GRASS)) {
                tableBuilder.withPool(grassPool(registries)
                        .when(lowerHalf(registries, Blocks.TALL_GRASS)));
            } else if (matches(key, Blocks.LARGE_FERN)) {
                tableBuilder.withPool(grassPool(registries)
                        .when(lowerHalf(registries, Blocks.LARGE_FERN)));
            } else if (matches(key, Blocks.SEAGRASS)) {
                tableBuilder.withPool(seaGrassPool(registries));
            } else if (matches(key, Blocks.TALL_SEAGRASS)) {
                tableBuilder.withPool(seaGrassPool(registries)
                        .when(lowerHalf(registries, Blocks.TALL_SEAGRASS)));
            } else if (matches(key, Blocks.NETHER_SPROUTS)) {
                tableBuilder.withPool(netherSproutsPool(registries));
            }
        });
    }

    private static boolean matches(ResourceKey<LootTable> key, Block block) {
        return block.getLootTable().map(key::equals).orElse(false);
    }

    /** Grass, fern, tall grass and large fern drops. */
    private static LootPool.Builder grassPool(HolderLookup.Provider registries) {
        LootPoolEntryContainer.Builder<?> entry = AlternativesEntry.alternatives(
                LootItem.lootTableItem(Registration.SEEDS)
                        .when(ConfigCondition.builder("seeds"))
                        .when(LootItemRandomChanceCondition.randomChance(0.01f))
                        .apply(RandomAnimalLootFunction.builder(Config.animalCropDrops)),
                LootItem.lootTableItem(Registration.POLLEN)
                        .when(ConfigCondition.builder("pollen"))
                        .when(LootItemRandomChanceCondition.randomChance(0.01f))
                        .apply(ApplyBonusCount.addUniformBonusCount(
                                registries.lookupOrThrow(Registries.ENCHANTMENT)
                                        .getOrThrow(Enchantments.FORTUNE),
                                2)));

        return LootPool.lootPool()
                .add(entry)
                .when(notShears(registries))
                .apply(ApplyExplosionDecay.explosionDecay());
    }

    /** Sea grass anemonemal drops. */
    private static LootPool.Builder seaGrassPool(HolderLookup.Provider registries) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(Registration.ANEMONEMAL_SEEDS)
                .apply(RandomAnimalLootFunction.builder(Config.anemonemals));

        return LootPool.lootPool()
                .add(entry)
                .when(ConfigCondition.builder("anemonemal"))
                .when(notShears(registries))
                .when(ExplosionCondition.survivesExplosion())
                .when(LootItemRandomChanceCondition.randomChance(0.02f));
    }

    /** Nether sprouts animal shroom and magnemone drops. */
    private static LootPool.Builder netherSproutsPool(HolderLookup.Provider registries) {
        LootPoolEntryContainer.Builder<?> entry = AlternativesEntry.alternatives(
                LootItem.lootTableItem(Registration.SPORES)
                        .when(ConfigCondition.builder("shrooms"))
                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                        .apply(RandomAnimalLootFunction.builder(Config.animalShrooms)),
                LootItem.lootTableItem(Registration.MAGNEMONE_SPORES)
                        .when(ConfigCondition.builder("magnemone"))
                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                        .apply(RandomAnimalLootFunction.builder(Config.magnemones)));

        return LootPool.lootPool()
                .add(entry)
                .when(notShears(registries))
                .when(ExplosionCondition.survivesExplosion());
    }

    private static LootItemCondition.Builder notShears(HolderLookup.Provider registries) {
        return InvertedLootItemCondition.invert(MatchTool.toolMatches(
                ItemPredicate.Builder.item().of(itemGetter(registries), SHEARS)));
    }

    private static HolderGetter<Item> itemGetter(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ITEM);
    }

    private static LootItemCondition.Builder lowerHalf(HolderLookup.Provider registries, Block block) {
        return MatchBlock.blockMatches(blockGetter(registries), block,
                StatePropertiesPredicate.Builder.properties()
                        .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
    }

    private static HolderGetter<Block> blockGetter(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.BLOCK);
    }
}

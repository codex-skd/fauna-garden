package com.skd.faunagarden.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Loot modifier that adds a loot entry to an existing loot table
 */
public class AddEntryLootModifier extends LootModifier {
    public static final MapCodec<AddEntryLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance)
                    .and(instance.group(
                            LootPoolEntries.CODEC.fieldOf("entry").forGetter(modifier -> modifier.entry),
                            LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of())
                                    .forGetter(modifier -> modifier.functions),
                            Codec.BOOL.optionalFieldOf("require_empty", false)
                                    .forGetter(modifier -> modifier.requireEmpty)
                    ))
                    .apply(instance, AddEntryLootModifier::new));

    private final LootPoolEntryContainer entry;
    private final List<LootItemFunction> functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
    private final boolean requireEmpty;

    protected AddEntryLootModifier(LootItemCondition[] conditions, int priority, LootPoolEntryContainer entry,
                                   List<LootItemFunction> functions, boolean requireEmpty) {
        super(conditions, priority);
        this.entry = entry;
        this.functions = functions;
        this.combinedFunctions = LootItemFunctions.compose(functions);
        this.requireEmpty = requireEmpty;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!this.requireEmpty || generatedLoot.isEmpty()) {
            Consumer<ItemStack> consumer = LootItemFunction.decorate(this.combinedFunctions, generatedLoot::add, context);
            this.entry.expand(context, entry -> entry.createItemStack(consumer, context));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}

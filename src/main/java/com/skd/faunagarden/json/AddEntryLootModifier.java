package com.skd.faunagarden.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Loot modifier that adds a loot entry to an existing loot table
 */
public class AddEntryLootModifier extends LootModifier {
    private static final Gson GSON = Deserializers.createLootTableSerializer().create();

    public static final Codec<AddEntryLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance)
                    .and(instance.group(
                            Codec.PASSTHROUGH.fieldOf("entry").forGetter(modifier -> toDynamic(entryJson(modifier.entry))),
                            Codec.PASSTHROUGH.optionalFieldOf("functions")
                                    .forGetter(modifier -> modifier.functions.length == 0
                                            ? Optional.empty()
                                            : Optional.of(toDynamic(functionsJson(modifier.functions)))),
                            Codec.BOOL.optionalFieldOf("require_empty", false)
                                    .forGetter(modifier -> modifier.requireEmpty)
                    ))
                    .apply(instance, AddEntryLootModifier::new));

    private final LootPoolEntryContainer entry;
    private final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
    private final boolean requireEmpty;

    protected AddEntryLootModifier(LootItemCondition[] conditions, Dynamic<?> entry,
                                   Optional<Dynamic<?>> functions, boolean requireEmpty) {
        super(conditions);
        this.entry = GSON.fromJson(IGlobalLootModifier.getJson(entry), LootPoolEntryContainer.class);
        this.functions = functions
                .map(dynamic -> GSON.fromJson(IGlobalLootModifier.getJson(dynamic), LootItemFunction[].class))
                .orElseGet(() -> new LootItemFunction[0]);
        this.combinedFunctions = LootItemFunctions.compose(this.functions);
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
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    /* JSON helpers, the 1.20.1 loot entry/function API is still Gson based */

    private static Dynamic<?> toDynamic(JsonElement element) {
        return new Dynamic<>(JsonOps.INSTANCE, element);
    }

    private static JsonElement entryJson(LootPoolEntryContainer entry) {
        return GSON.toJsonTree(entry, LootPoolEntryContainer.class);
    }

    private static JsonElement functionsJson(LootItemFunction[] functions) {
        return GSON.toJsonTree(functions, LootItemFunction[].class);
    }
}

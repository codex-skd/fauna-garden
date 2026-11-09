package com.skd.faunagarden.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Config.AnimalCropType;
import com.skd.faunagarden.core.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Locale;

/**
 * Sets an item's entity to a random value of the configured type.
 */
public class RandomAnimalLootFunction extends LootItemConditionalFunction {
    private static final Codec<AnimalCropType> TYPE_CODEC = Codec.STRING.comapFlatMap(
            RandomAnimalLootFunction::parseType,
            RandomAnimalLootFunction::nameOf);

    public static final MapCodec<RandomAnimalLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(TYPE_CODEC.fieldOf("type").forGetter(function -> function.animalType))
                    .apply(instance, RandomAnimalLootFunction::new));

    /** Type registered for this function */
    public static final LootItemFunctionType<RandomAnimalLootFunction> TYPE = new LootItemFunctionType<>(CODEC);

    private final AnimalCropType animalType;

    private RandomAnimalLootFunction(List<LootItemCondition> conditions, AnimalCropType animalType) {
        super(conditions);
        this.animalType = animalType;
    }

    /** Creates a builder for this function, used by the Java-built loot pools. */
    public static LootItemFunction.Builder builder(AnimalCropType animalType) {
        return () -> new RandomAnimalLootFunction(List.of(), animalType);
    }

    @Override
    public LootItemFunctionType<RandomAnimalLootFunction> getType() {
        return TYPE;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        EntityType<?> type = animalType.getRandomValue(context.getRandom());
        // prevent crash if empty, the config condition handles this in the loot tables
        if (type == null) {
            FaunaGarden.LOGGER.error("Received empty animal list for {}, a condition is missing in the loot table", animalType);
        } else {
            Utils.setEntityId(stack, BuiltInRegistries.ENTITY_TYPE.getKey(type));
        }
        return stack;
    }

    private static DataResult<AnimalCropType> parseType(String name) {
        AnimalCropType type = switch (name.toLowerCase(Locale.ROOT)) {
            case "crops" -> Config.animalCropDrops;
            case "anemonemal" -> Config.anemonemals;
            case "shrooms" -> Config.animalShrooms;
            case "magnemone" -> Config.magnemones;
            default -> null;
        };
        if (type == null) {
            return DataResult.error(() -> "Invalid animal type '" + name + "'");
        }
        return DataResult.success(type);
    }

    private static String nameOf(AnimalCropType type) {
        if (type == Config.animalCropDrops) {
            return "crops";
        }
        if (type == Config.anemonemals) {
            return "anemonemal";
        }
        if (type == Config.animalShrooms) {
            return "shrooms";
        }
        return "magnemone";
    }
}

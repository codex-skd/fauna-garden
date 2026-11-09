package com.skd.faunagarden.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Config.AnimalCropType;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.core.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Locale;

/**
 * Sets an item's entity to a random value of the configured type.
 */
public class RandomAnimalLootFunction extends LootItemConditionalFunction {
    public static final Serializer SERIALIZER = new Serializer();

    private final AnimalCropType animalType;

    private RandomAnimalLootFunction(LootItemCondition[] conditions, AnimalCropType animalType) {
        super(conditions);
        this.animalType = animalType;
    }

    @Override
    public LootItemFunctionType getType() {
        return Registration.RANDOM_ANIMAL.get();
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

    private static AnimalCropType parseType(String name) {
        AnimalCropType type = switch (name.toLowerCase(Locale.ROOT)) {
            case "crops" -> Config.animalCropDrops;
            case "anemonemal" -> Config.anemonemals;
            case "shrooms" -> Config.animalShrooms;
            case "magnemone" -> Config.magnemones;
            default -> null;
        };
        if (type == null) {
            throw new JsonSyntaxException("Invalid animal type '" + name + "'");
        }
        return type;
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

    /** Serializer for the random animal function. */
    public static class Serializer extends LootItemConditionalFunction.Serializer<RandomAnimalLootFunction> {
        @Override
        public RandomAnimalLootFunction deserialize(JsonObject json, JsonDeserializationContext context,
                                                    LootItemCondition[] conditions) {
            return new RandomAnimalLootFunction(conditions, parseType(GsonHelper.getAsString(json, "type")));
        }

        @Override
        public void serialize(JsonObject json, RandomAnimalLootFunction function, JsonSerializationContext context) {
            super.serialize(json, function, context);
            json.addProperty("type", nameOf(function.animalType));
        }
    }
}

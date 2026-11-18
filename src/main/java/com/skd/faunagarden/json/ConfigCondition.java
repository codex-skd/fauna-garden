package com.skd.faunagarden.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Locale;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Conditions a loot table entry based on config
 */
public class ConfigCondition implements LootItemCondition {
    private static final Set<String> PROPS = Set.of("seeds", "anemonemal", "shrooms", "magnemone", "pollen");

    private static final Codec<String> PROP_CODEC = Codec.STRING.comapFlatMap(prop -> {
        if (PROPS.contains(prop.toLowerCase(Locale.ROOT))) {
            return DataResult.success(prop);
        }
        return DataResult.error(() -> "Invalid config property name '" + prop + "'");
    }, prop -> prop);

    public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PROP_CODEC.fieldOf("prop").forGetter(condition -> condition.prop))
                    .apply(instance, ConfigCondition::new));

    private final String prop;

    private ConfigCondition(String prop) {
        this.prop = prop;
    }

    @Override
    public LootItemConditionType getType() {
        return Registration.CONFIG_CONDITION.get();
    }

    @Override
    public boolean test(LootContext context) {
        BooleanSupplier supplier = switch (prop.toLowerCase(Locale.ROOT)) {
            case "seeds" -> Config.animalCropDrops::doesDrop;
            case "anemonemal" -> Config.anemonemals::doesDrop;
            case "shrooms" -> Config.animalShrooms::doesDrop;
            case "magnemone" -> Config.magnemones::doesDrop;
            case "pollen" -> Config.dropAnimalPollen::get;
            default -> throw new IllegalStateException("Invalid config property name '" + prop + "'");
        };
        return supplier.getAsBoolean();
    }
}

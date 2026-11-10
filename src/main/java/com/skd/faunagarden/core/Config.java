package com.skd.faunagarden.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.items.AnimalPollenItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

/**
 * Mod configuration, stored as {@code config/fauna_garden.json}.
 *
 * <p>Fabric has no built-in config system, so this class reads and writes a small JSON file
 * directly. The file is read once at startup on both the client and the server. Missing keys
 * fall back to their defaults and an unreadable file only logs an error, so a broken config
 * never prevents the game from starting.
 */
public final class Config {
    /** Name of the config file inside the Fabric config directory. */
    private static final String FILE_NAME = "fauna_garden.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // crop types
    public static final AnimalCropType animalCropDrops;
    public static final AnimalCropType anemonemals;
    public static final AnimalCropType animalShrooms;
    public static final AnimalCropType magnemones;

    // general
    public static final BooleanValue canBonemeal = new BooleanValue(false);
    public static final EnumValue<AnimalPollenItem.Action> pollenAction =
            new EnumValue<>(AnimalPollenItem.Action.DAMAGE);

    // grass drops
    public static final BooleanValue dropAnimalPollen = new BooleanValue(true);
    private static final BooleanValue dropAnimalSeeds = new BooleanValue(false);
    private static final BooleanValue dropAnemonemals = new BooleanValue(false);
    private static final BooleanValue dropAnimalShrooms = new BooleanValue(false);
    private static final BooleanValue dropMagnemones = new BooleanValue(false);

    static {
        animalCropDrops = new AnimalCropType(AnimalTags.DROPPABLE_ANIMAL_CROPS, dropAnimalSeeds);
        anemonemals = new AnimalCropType(AnimalTags.DROPPABLE_ANEMONEMAL, dropAnemonemals);
        animalShrooms = new AnimalCropType(AnimalTags.DROPPABLE_ANIMAL_SHROOMS, dropAnimalShrooms);
        magnemones = new AnimalCropType(AnimalTags.DROPPABLE_MAGNEMONES, dropMagnemones);
    }

    private Config() {}

    /**
     * Reads the config file, creating it with defaults when it does not exist yet.
     * Must be called once during mod initialisation on both sides.
     */
    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        JsonObject root = null;

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element != null && element.isJsonObject()) {
                    root = element.getAsJsonObject();
                } else {
                    FaunaGarden.LOGGER.warn("Config file {} is not a JSON object, using defaults", path);
                }
            } catch (Exception e) {
                FaunaGarden.LOGGER.error("Could not read config file {}, using defaults", path, e);
            }
        } else {
            try {
                Files.createDirectories(path.getParent());
                try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                    GSON.toJson(defaults(), writer);
                }
                FaunaGarden.LOGGER.info("Created default config file {}", path);
            } catch (IOException e) {
                FaunaGarden.LOGGER.error("Could not create default config file {}", path, e);
            }
        }

        apply(root != null ? root : new JsonObject());
    }

    /** Builds the JSON written to disk when no config file exists yet. */
    private static JsonObject defaults() {
        JsonObject root = new JsonObject();

        JsonObject general = new JsonObject();
        general.addProperty("bonemeal", false);
        general.addProperty("action", AnimalPollenItem.Action.DAMAGE.name());
        root.add("general", general);

        JsonObject grass = new JsonObject();
        grass.addProperty("animal_pollen", true);
        grass.addProperty("animal_seeds", false);
        grass.addProperty("anemonemal", false);
        grass.addProperty("animal_shrooms", false);
        grass.addProperty("magnemones", false);
        root.add("grassDrops", grass);

        return root;
    }

    /** Applies the values read from disk, keeping defaults for any missing or invalid key. */
    private static void apply(JsonObject root) {
        JsonObject general = getObject(root, "general");
        canBonemeal.set(getBoolean(general, "bonemeal", false));
        pollenAction.set(getEnum(general, "action", AnimalPollenItem.Action.DAMAGE, AnimalPollenItem.Action.class));

        JsonObject grass = getObject(root, "grassDrops");
        dropAnimalPollen.set(getBoolean(grass, "animal_pollen", true));
        dropAnimalSeeds.set(getBoolean(grass, "animal_seeds", false));
        dropAnemonemals.set(getBoolean(grass, "anemonemal", false));
        dropAnimalShrooms.set(getBoolean(grass, "animal_shrooms", false));
        dropMagnemones.set(getBoolean(grass, "magnemones", false));
    }

    private static JsonObject getObject(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        return new JsonObject();
    }

    private static boolean getBoolean(JsonObject parent, String key, boolean def) {
        JsonElement element = parent.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        }
        return def;
    }

    private static <T extends Enum<T>> T getEnum(JsonObject parent, String key, T def, Class<T> type) {
        JsonElement element = parent.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            try {
                return Enum.valueOf(type, element.getAsString().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                FaunaGarden.LOGGER.warn("Invalid value '{}' for config key '{}', using {}", element.getAsString(), key, def);
            }
        }
        return def;
    }

    /** Simple mutable boolean config value. */
    public static final class BooleanValue {
        private boolean value;

        private BooleanValue(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return this.value;
        }

        private void set(boolean value) {
            this.value = value;
        }
    }

    /** Simple mutable enum config value. */
    public static final class EnumValue<T extends Enum<T>> {
        private T value;

        private EnumValue(T value) {
            this.value = value;
        }

        public T get() {
            return this.value;
        }

        private void set(T value) {
            this.value = value;
        }
    }

    /** Config setup for each type of animal crops. */
    public static final class AnimalCropType {
        private final TagKey<EntityType<?>> tag;
        private final BooleanValue drop;

        private AnimalCropType(TagKey<EntityType<?>> tag, BooleanValue drop) {
            this.tag = tag;
            this.drop = drop;
        }

        /** Gets the loaded entity tag, if any. */
        private Optional<HolderSet.Named<EntityType<?>>> getTag() {
            return BuiltInRegistries.ENTITY_TYPE.getTags()
                    .filter(holders -> holders.key().equals(this.tag))
                    .findFirst();
        }

        /**
         * Returns true if this type of animal crops drops.
         */
        public boolean doesDrop() {
            if (!this.drop.get()) {
                return false;
            }
            return getTag().map(holders -> holders.size() > 0).orElse(false);
        }

        /** Gets a random value of this crop drop type. */
        @Nullable
        public EntityType<?> getRandomValue(RandomSource random) {
            if (doesDrop()) {
                return getTag()
                        .flatMap(holders -> holders.getRandomElement(random))
                        .map(Holder::value)
                        .orElse(null);
            }
            return null;
        }
    }
}

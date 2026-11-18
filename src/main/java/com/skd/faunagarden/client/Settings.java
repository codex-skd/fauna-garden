package com.skd.faunagarden.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.Registration;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Client settings read from the top-most {@code fauna_garden:models/settings.json} resource.
 * Controls whether each crop kind renders its growing entity.
 */
public class Settings implements ResourceManagerReloadListener {
    /** Location of the settings file. */
    private static final Identifier LOCATION =
            Identifier.fromNamespaceAndPath(FaunaGarden.MOD_ID, "models/settings.json");
    /** JSON keys for the properties. */
    private static final String CROP_ENTITY_KEY = "render_crop_entity";
    private static final String ANEMONEMAL_ENTITY_KEY = "render_anemonemal_entity";
    private static final String SHROOM_ENTITY_KEY = "render_shroom_entity";
    private static final String MAGNEMONE_ENTITY_KEY = "render_magnemone_entity";

    private boolean renderCropEntity = true;
    private boolean renderAnemonemalEntity = true;
    private boolean renderShroomEntity = true;
    private boolean renderMagnemoneEntity = true;

    public static final Settings INSTANCE = new Settings();

    private Settings() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        List<JsonObject> jsonFiles = new ArrayList<>();
        for (Resource resource : manager.getResourceStack(LOCATION)) {
            JsonObject json = getJson(resource);
            if (json != null) {
                jsonFiles.add(json);
            }
        }

        renderCropEntity = getTopBoolean(jsonFiles, CROP_ENTITY_KEY, true);
        renderAnemonemalEntity = getTopBoolean(jsonFiles, ANEMONEMAL_ENTITY_KEY, true);
        renderShroomEntity = getTopBoolean(jsonFiles, SHROOM_ENTITY_KEY, true);
        renderMagnemoneEntity = getTopBoolean(jsonFiles, MAGNEMONE_ENTITY_KEY, true);
    }

    /**
     * Gets the boolean from the highest priority pack that defines it.
     * @param list List of JSON objects, ordered from lowest to highest priority
     * @param key  Key to fetch
     * @param def  Default value
     * @return Value from the top-most pack, or the default
     */
    private static boolean getTopBoolean(@Nonnull List<JsonObject> list, @Nonnull String key, boolean def) {
        // getResourceStack keeps the top-most pack at the end of the list
        for (int i = list.size() - 1; i >= 0; --i) {
            JsonObject json = list.get(i);
            if (GsonHelper.isBooleanValue(json, key)) {
                return GsonHelper.getAsBoolean(json, key);
            }
        }
        return def;
    }

    @Nullable
    private static JsonObject getJson(Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            return GsonHelper.parse(reader);
        } catch (JsonParseException | IOException e) {
            FaunaGarden.LOGGER.error("Failed to load fauna garden model settings file", e);
            return null;
        }
    }

    /**
     * Checks whether the given crop block should render its growing entity.
     * @param block Block being rendered
     * @return True if the entity model should render
     */
    public static boolean shouldRenderEntity(Block block) {
        if (block == Registration.CROPS.get()) {
            return INSTANCE.renderCropEntity;
        }
        if (block == Registration.ANEMONEMAL.get()) {
            return INSTANCE.renderAnemonemalEntity;
        }
        if (block == Registration.SHROOMS.get()) {
            return INSTANCE.renderShroomEntity;
        }
        if (block == Registration.MAGNEMONE.get()) {
            return INSTANCE.renderMagnemoneEntity;
        }
        // fallback in case another mod extends this
        return true;
    }
}

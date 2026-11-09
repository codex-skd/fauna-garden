package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Built-in optional resource pack replacing the fancy crop models with simple ones.
 * It lives in the mod jar under {@code resourcepacks/simple_crops} and is never enabled
 * by default.
 */
public final class SimpleCropsPack {
    /** Internal pack id, also used as the folder name under {@code resourcepacks/}. */
    public static final String PACK_ID = "simple_crops";

    private SimpleCropsPack() {}

    /** Registers the built-in pack with Fabric's resource loader. */
    public static void register(ModContainer container) {
        boolean registered = ResourceManagerHelper.registerBuiltinResourcePack(
                ResourceLocation.fromNamespaceAndPath(FaunaGarden.MOD_ID, PACK_ID),
                container,
                Component.translatable("resourcePack.fauna_garden.simple_crops"),
                ResourcePackActivationType.NORMAL);
        if (!registered) {
            FaunaGarden.LOGGER.error("Could not register built-in resource pack at resourcepacks/{}", PACK_ID);
        }
    }
}

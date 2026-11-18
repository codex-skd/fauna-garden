package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.Registration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Client-only setup. Only referenced from the client entrypoint, so dedicated
 * servers never load any rendering code.
 */
@Environment(EnvType.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    /** Registers renderers, tints, the settings reload listener and the built-in resource pack. */
    public static void init() {
        // Item tint sources have no Fabric API: register in the vanilla late-bound id mapper.
        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(FaunaGarden.MOD_ID, "entity_egg"),
                EntityEggTintSource.CODEC);

        BlockEntityRendererRegistry.register(Registration.CROPS_BE, RenderFaunaCrops::new);

        // Registered unconditionally: the simple crops pack may be toggled later.
        Block[] crops = {
                Registration.CROPS,
                Registration.ANEMONEMAL,
                Registration.SHROOMS,
                Registration.MAGNEMONE
        };
        List<BlockTintSource> sources = List.of(new CropTintSource(0), new CropTintSource(1));
        BlockColorRegistry.register(sources, crops);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(Settings.INSTANCE);

        SimpleCropsPack.register(FabricLoader.getInstance().getModContainer(FaunaGarden.MOD_ID).orElseThrow());
    }
}

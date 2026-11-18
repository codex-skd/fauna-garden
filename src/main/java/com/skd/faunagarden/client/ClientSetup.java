package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.core.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.SpawnEggItem;

import org.jetbrains.annotations.Nullable;

/**
 * Client-only setup. Only referenced from the client entrypoint, so dedicated
 * servers never load any rendering code.
 */
@Environment(EnvType.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    /** Registers renderers, colors, the settings reload listener and the built-in resource pack. */
    public static void init() {
        BlockEntityRenderers.register(Registration.CROPS_BE, RenderFaunaCrops::new);

        // Crop models have cut-out pixels; vanilla model render_type also handles this,
        // but registering the layers explicitly keeps them correct on every resource pack.
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                Registration.CROPS, Registration.ANEMONEMAL, Registration.SHROOMS, Registration.MAGNEMONE);

        // Registered unconditionally: the simple crops pack may be toggled later.
        ColorProviderRegistry.BLOCK.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) {
                return -1;
            }
            if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity crops) {
                return getEggColor(crops.getEntityId(), tintIndex);
            }
            return -1;
        }, Registration.CROPS, Registration.ANEMONEMAL, Registration.SHROOMS, Registration.MAGNEMONE);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                        getEggColor(Utils.getEntityID(stack).orElse(null), tintIndex),
                Registration.SEEDS, Registration.ANEMONEMAL_SEEDS, Registration.SPORES,
                Registration.MAGNEMONE_SPORES);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(Settings.INSTANCE);

        SimpleCropsPack.register(FabricLoader.getInstance().getModContainer(FaunaGarden.MOD_ID).orElseThrow());
    }

    /**
     * Resolves the spawn egg color for the given entity id and tint index.
     * @param entityId  Entity id, may be null
     * @param tintIndex Tint index
     * @return Egg color, or -1 when unknown
     */
    private static int getEggColor(@Nullable ResourceLocation entityId, int tintIndex) {
        if (entityId == null) {
            return -1;
        }
        return BuiltInRegistries.ENTITY_TYPE.getOptional(entityId)
                .map(SpawnEggItem::byId)
                .map(egg -> egg.getColor(tintIndex))
                .orElse(-1);
    }
}

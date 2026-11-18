package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.core.Registration;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Client-only setup. The whole class is only loaded on the client distribution,
 * so dedicated servers never touch the client rendering code.
 */
@Mod.EventBusSubscriber(modid = FaunaGarden.MOD_ID, bus = Mod.EventBusSubscriber.Bus.BOTH, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    static {
        // Item tint sources have no mod event on Forge: register in the vanilla late-bound id mapper.
        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(FaunaGarden.MOD_ID, "entity_egg"),
                EntityEggTintSource.CODEC);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.CROPS_BE.get(), (BlockEntityRendererProvider.Context context) -> new RenderFaunaCrops(context));
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Registered unconditionally: the simple crops pack may be toggled later.
        Block[] crops = {
                Registration.CROPS.get(),
                Registration.ANEMONEMAL.get(),
                Registration.SHROOMS.get(),
                Registration.MAGNEMONE.get()
        };
        List<BlockTintSource> sources = List.of(new CropTintSource(0), new CropTintSource(1));
        event.register(sources, crops);
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(Settings.INSTANCE);
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(SimpleCropsPack.INSTANCE);
        }
    }
}

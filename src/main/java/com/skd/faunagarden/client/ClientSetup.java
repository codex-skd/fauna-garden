package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.core.Registration;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.List;

/**
 * Client-only setup. The whole class is only loaded on the client distribution,
 * so dedicated servers never touch the client rendering code.
 */
@EventBusSubscriber(modid = FaunaGarden.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.CROPS_BE.get(), (BlockEntityRendererProvider.Context context) -> new RenderFaunaCrops(context));
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        // Registered unconditionally: the simple crops pack may be toggled later.
        net.minecraft.world.level.block.Block[] crops = {
                Registration.CROPS.get(),
                Registration.ANEMONEMAL.get(),
                Registration.SHROOMS.get(),
                Registration.MAGNEMONE.get()
        };
        List<BlockTintSource> sources = List.of(new CropTintSource(0), new CropTintSource(1));
        event.register(sources, crops);
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Registration.getResource("entity_egg"), EntityEggTintSource.CODEC);
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(Registration.getResource("settings"), Settings.INSTANCE);
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        SimpleCropsPack.addTo(event);
    }
}

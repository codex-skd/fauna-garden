package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.core.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;

/**
 * Client-only setup. The whole class is only loaded on the client distribution,
 * so dedicated servers never touch the client rendering code.
 */
@EventBusSubscriber(modid = FaunaGarden.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.CROPS_BE.get(), RenderFaunaCrops::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Registered unconditionally: the simple crops pack may be toggled later.
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) {
                return -1;
            }
            if (level.getBlockEntity(pos) instanceof FaunaCropsBlockEntity crops) {
                return getEggColor(crops.getEntityId(), tintIndex);
            }
            return -1;
        }, Registration.CROPS.get(), Registration.ANEMONEMAL.get(), Registration.SHROOMS.get(),
                Registration.MAGNEMONE.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> getEggColor(Utils.getEntityID(stack).orElse(null), tintIndex),
                Registration.SEEDS.get(), Registration.ANEMONEMAL_SEEDS.get(), Registration.SPORES.get(),
                Registration.MAGNEMONE_SPORES.get());
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

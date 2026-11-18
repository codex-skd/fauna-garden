package com.skd.faunagarden;

import com.mojang.logging.LogUtils;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.datagen.ModBlockTagsProvider;
import com.skd.faunagarden.datagen.ModEntityTypeTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(FaunaGarden.MOD_ID)
public class FaunaGarden {
    public static final String MOD_ID = "fauna_garden";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FaunaGarden(IEventBus modBus, ModContainer container) {
        Registration.register(modBus);
        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        modBus.addListener(Config::configChanged);
        modBus.addListener(Registration::commonSetup);
        modBus.addListener(Registration::buildTabContents);
        modBus.addListener(FaunaGarden::gatherData);
        LOGGER.info("Fauna Garden is loading");
    }

    /** Registers the data generators. */
    private static void gatherData(GatherDataEvent.Server event) {
        event.createProvider((PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) ->
                new ModBlockTagsProvider(output, lookupProvider, FaunaGarden.MOD_ID));
        event.createProvider(ModEntityTypeTagsProvider::new);
    }
}

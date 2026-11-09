package com.skd.faunagarden;

import com.mojang.logging.LogUtils;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.datagen.ModBlockTagsProvider;
import com.skd.faunagarden.datagen.ModEntityTypeTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FaunaGarden.MOD_ID)
public class FaunaGarden {
    public static final String MOD_ID = "fauna_garden";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FaunaGarden() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registration.register(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        modBus.addListener(Config::configChanged);
        modBus.addListener(Registration::commonSetup);
        modBus.addListener(Registration::buildTabContents);
        modBus.addListener(FaunaGarden::gatherData);
        LOGGER.info("Fauna Garden is loading");
    }

    /** Registers the data generators. */
    private static void gatherData(GatherDataEvent event) {
        if (!event.includeServer()) {
            return;
        }
        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, new ModBlockTagsProvider(generator.getPackOutput(),
                event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(true, new ModEntityTypeTagsProvider(generator.getPackOutput(),
                event.getLookupProvider(), event.getExistingFileHelper()));
    }
}

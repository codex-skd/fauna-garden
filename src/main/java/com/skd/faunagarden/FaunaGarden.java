package com.skd.faunagarden;

import com.mojang.logging.LogUtils;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.json.LootModifications;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class FaunaGarden implements ModInitializer {
    public static final String MOD_ID = "fauna_garden";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        Config.load();
        Registration.register();
        LootModifications.register();
        LOGGER.info("Fauna Garden is loading");
    }
}

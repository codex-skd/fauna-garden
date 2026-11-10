package com.skd.faunagarden.client;

import net.fabricmc.api.ClientModInitializer;

public class FaunaGardenClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.init();
    }
}

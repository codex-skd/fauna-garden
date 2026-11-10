package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * Built-in optional resource pack replacing the fancy crop models with simple ones.
 * It lives in the mod jar under {@code resourcepacks/simple_crops}.
 */
public final class SimpleCropsPack {
    /** Jar location of the pack, relative to the resources root. */
    public static final Identifier LOCATION = Identifier.fromNamespaceAndPath(FaunaGarden.MOD_ID, "resourcepacks/simple_crops");

    private SimpleCropsPack() {}

    /** Adds the built-in pack to the client resource pack list. */
    public static void addTo(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        event.addPackFinders(LOCATION, PackType.CLIENT_RESOURCES,
                Component.translatable("resourcePack.fauna_garden.simple_crops"),
                PackSource.BUILT_IN, false, Pack.Position.TOP);
    }
}

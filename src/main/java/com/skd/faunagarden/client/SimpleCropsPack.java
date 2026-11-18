package com.skd.faunagarden.client;

import com.skd.faunagarden.FaunaGarden;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModList;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Built-in optional resource pack replacing the fancy crop models with simple ones.
 * It lives in the mod jar under {@code resourcepacks/simple_crops}.
 */
public final class SimpleCropsPack implements RepositorySource {
    /** Internal pack id. */
    public static final String PACK_ID = "mod/" + FaunaGarden.MOD_ID + ":simple_crops";
    /** Jar folder holding the pack. */
    private static final String PACK_FOLDER = "simple_crops";

    public static final SimpleCropsPack INSTANCE = new SimpleCropsPack();

    private SimpleCropsPack() {}

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        Path path = ModList.get().getModFileById(FaunaGarden.MOD_ID).getFile()
                .findResource("resourcepacks", PACK_FOLDER);
        if (path == null) {
            FaunaGarden.LOGGER.error("Could not find built-in resource pack at resourcepacks/{}", PACK_FOLDER);
            return;
        }

        Pack.ResourcesSupplier supplier = id -> new PathPackResources(PACK_ID, path, true);
        Pack pack = Pack.readMetaAndCreate(PACK_ID,
                Component.translatable("resourcePack.fauna_garden.simple_crops"), false, supplier,
                PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
        if (pack == null) {
            FaunaGarden.LOGGER.error("Could not read built-in resource pack at {}", path);
            return;
        }
        consumer.accept(pack);
    }
}

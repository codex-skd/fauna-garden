package com.skd.faunagarden.compat.jei;

import com.skd.faunagarden.core.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * Optional JEI integration. This class is only loaded by JEI when the mod is present,
 * so the rest of the mod never references JEI classes.
 */
@JeiPlugin
public final class FaunaGardenJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_UID =
            Identifier.fromNamespaceAndPath("fauna_garden", "jei");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        register(registration, Registration.SEEDS);
        register(registration, Registration.ANEMONEMAL_SEEDS);
        register(registration, Registration.SPORES);
        register(registration, Registration.MAGNEMONE_SPORES);
    }

    /** Distinct subtype per stored {@code fauna_garden:entity} component value. */
    private static void register(ISubtypeRegistration registration, Item item) {
        registration.registerFromDataComponentTypes(item, Registration.ENTITY);
    }
}

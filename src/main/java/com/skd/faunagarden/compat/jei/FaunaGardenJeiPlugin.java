package com.skd.faunagarden.compat.jei;

import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.core.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Optional JEI integration. This class is only loaded by JEI when the mod is present,
 * so the rest of the mod never references JEI classes.
 */
@JeiPlugin
@SuppressWarnings({"deprecation", "removal"})
public final class FaunaGardenJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath("fauna_garden", "jei");

    private static final IIngredientSubtypeInterpreter<ItemStack> ENTITY_SUBTYPE =
            (stack, context) -> Utils.getEntityID(stack)
                    .map(ResourceLocation::toString)
                    .orElse(IIngredientSubtypeInterpreter.NONE);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        register(registration, Registration.SEEDS.get());
        register(registration, Registration.ANEMONEMAL_SEEDS.get());
        register(registration, Registration.SPORES.get());
        register(registration, Registration.MAGNEMONE_SPORES.get());
    }

    private static void register(ISubtypeRegistration registration, Item item) {
        registration.registerSubtypeInterpreter(item, ENTITY_SUBTYPE);
    }
}

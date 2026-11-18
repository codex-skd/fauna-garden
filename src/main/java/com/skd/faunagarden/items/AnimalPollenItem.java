package com.skd.faunagarden.items;

import com.skd.faunagarden.core.AnimalTags;
import com.skd.faunagarden.core.Config;
import com.skd.faunagarden.core.Registration;
import com.skd.faunagarden.core.Utils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Item to convert mobs into seeds.
 */
public class AnimalPollenItem extends Item {

    public AnimalPollenItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        EntityType<?> type = entity.getType();
        if (type.builtInRegistryHolder().is(AnimalTags.POLLEN_REACTIVE)) {
            // next, check which type of seed we are grabbing
            Item item = null;
            if (type.builtInRegistryHolder().is(AnimalTags.ANIMAL_CROPS)) {
                item = Registration.SEEDS;
            } else if (type.builtInRegistryHolder().is(AnimalTags.ANEMONEMAL)) {
                item = Registration.ANEMONEMAL_SEEDS;
            } else if (type.builtInRegistryHolder().is(AnimalTags.ANIMAL_SHROOMS)) {
                item = Registration.SPORES;
            } else if (type.builtInRegistryHolder().is(AnimalTags.MAGNEMONES)) {
                item = Registration.MAGNEMONE_SPORES;
            }
            // its possible the type matches none because someone used tags wrongly
            if (item != null) {
                // create the seed item
                ItemStack seeds = new ItemStack(item);
                Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
                Utils.setEntityId(seeds, id);
                player.setItemInHand(hand, Utils.fillContainer(player, stack, seeds));

                // effects
                entity.playSound(SoundEvents.NETHER_WART_BREAK, 1.0F, 0.8F);
                spawnEntityParticles(entity, ParticleTypes.MYCELIUM, 15);
                switch (Config.pollenAction.get()) {
                    case CONSUME -> {
                        // spawn death particles and remove the entity
                        spawnEntityParticles(entity, ParticleTypes.POOF, 20);
                        if (entity.level() instanceof ServerLevel) {
                            entity.remove(RemovalReason.KILLED);
                        }
                    }
                    case DAMAGE -> {
                        if (entity.level() instanceof ServerLevel serverLevel) {
                            entity.hurtServer(serverLevel, entity.damageSources().cactus(), 4);
                        }
                        spawnEntityParticles(entity, ParticleTypes.DAMAGE_INDICATOR, 2);
                    }
                    default -> {
                        // no action against the entity
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        // tell the player why nothing happened
        player.sendOverlayMessage(Component.translatable(this.getDescriptionId() + ".invalid", type.getDescription()));
        return InteractionResult.SUCCESS;
    }

    /**
     * Spawns particles around an entity, relative to its size.
     * @param entity    Entity
     * @param particle  Particle to spawn
     * @param count     Number to spawn
     */
    private static void spawnEntityParticles(LivingEntity entity, ParticleOptions particle, int count) {
        Level world = entity.level();
        for (int k = 0; k < count; k++) {
            double speedX = world.getRandom().nextGaussian() * 0.02D;
            double speedY = world.getRandom().nextGaussian() * 0.02D;
            double speedZ = world.getRandom().nextGaussian() * 0.02D;
            world.addParticle(particle, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D), speedX, speedY, speedZ);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    /** Valid actions for pollen, as set by the config */
    public enum Action {
        /** Entity is consumed in a cloud of smoke */
        CONSUME,
        /** Entity takes damage, but remains in the world */
        DAMAGE,
        /** No action against entity */
        NONE
    }
}

package com.skd.faunagarden.blocks.entity;

import com.skd.faunagarden.FaunaGarden;
import com.skd.faunagarden.core.AnimalTags;
import com.skd.faunagarden.core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FaunaCropsBlockEntity extends BlockEntity {
    /** Tag used to store the random direction */
    public static final String TAG_DIRECTION = "direction";
    /** Tag used to store the entity id */
    public static final String TAG_ENTITY = "entity";

    /** Entity id displayed and spawned by this crop, if any */
    @Nullable
    private ResourceLocation entityId;
    /** Random direction used to orient the displayed entity */
    private int direction;

    /**
     * Cached entity from {@link #getEntity(boolean)}.
     * Typically null except on the client side (only cached during fancy crop rendering).
     */
    @Nullable
    private LivingEntity entity;

    public FaunaCropsBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CROPS_BE, pos, state);
    }

    /* Getters and setters */

    /**
     * Sets the entity into the block entity.
     * @param entityID  Entity ID to set
     */
    public void setEntity(ResourceLocation entityID) {
        // only set if valid
        if (!entityValid(entityID)) {
            return;
        }

        this.entityId = entityID;
        this.entity = null;
        if (this.level != null && !this.level.isClientSide()) {
            this.direction = this.level.getRandom().nextInt(4);
        }
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    /** Gets the entity id stored in this crop, if any. */
    @Nullable
    public ResourceLocation getEntityId() {
        return this.entityId;
    }

    /**
     * Gets the entity for this crop.
     * @param cacheEntity  If true, pull the entity from the cache and cache the result. Typically only used client side
     * @return  The entity for this crop
     */
    @Nullable
    public LivingEntity getEntity(boolean cacheEntity) {
        if (cacheEntity && this.entity != null) {
            return this.entity;
        }
        if (this.level == null || this.entityId == null || !entityValid(this.entityId)) {
            return null;
        }

        // create the entity from the stored data
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityId);
        Entity created = type.create(this.level);
        if (created == null) {
            return null;
        }

        // if the entity is not a living entity, discard it
        // should not happen as all spawn eggs are living entities
        if (!(created instanceof LivingEntity living)) {
            created.remove(RemovalReason.DISCARDED);
            FaunaGarden.LOGGER.error("Attempted to create invalid non-living entity " + created.getType());
            return null;
        }

        // set the water state for aquatic entities
        if (getBlockState().getFluidState().getType() == Fluids.WATER) {
            living.wasTouchingWater = true;
        }

        float angle = this.getAngle();
        living.setYRot(angle);
        living.yRotO = angle;
        living.yHeadRotO = living.yHeadRot = angle;
        living.yBodyRotO = living.yBodyRot = angle;

        if (living instanceof Mob mob) {
            mob.setBaby(true);
        }

        // cache the entity if requested
        if (cacheEntity) {
            this.entity = living;
        }
        return living;
    }

    /**
     * Gets the angle for the given random crop direction.
     * @return  Angle in 90 degree increments
     */
    public float getAngle() {
        int index = this.direction;
        if (index < 0 || index > 3) {
            return Direction.SOUTH.toYRot();
        }
        return Direction.from2DDataValue(index).toYRot();
    }

    /**
     * Spawns the current entity into the world then clears it.
     */
    public void spawnAnimal() {
        // if we have no entity, give up
        LivingEntity entity = getEntity(false);
        if (entity == null) {
            return;
        }

        // set position
        entity.setPos(this.worldPosition.getX() + 0.5, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5);

        // set entity data where relevant
        Mob mob = null;
        if (this.level instanceof ServerLevelAccessor accessor && entity instanceof Mob spawnedMob) {
            mob = spawnedMob;
            mob.finalizeSpawn(accessor, this.level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.SPAWN_EGG, null);
        }

        // slime sizes should not be bigger than 2
        if (entity instanceof Slime slime && slime.getSize() > 2) {
            slime.setSize(2, true);
        }

        // spawn
        if (this.level != null) {
            this.level.addFreshEntity(entity);
        }
        if (mob != null) {
            mob.playAmbientSound();
        }
    }

    /* NBT */

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.entityId != null) {
            tag.putString(TAG_ENTITY, this.entityId.toString());
        }
        tag.putInt(TAG_DIRECTION, this.direction);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.entityId = null;
        if (tag.contains(TAG_ENTITY)) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(TAG_ENTITY));
            if (parsed != null) {
                this.entityId = parsed;
            }
        }
        this.direction = tag.getInt(TAG_DIRECTION);
        this.entity = null;
    }

    /* Components */

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        ResourceLocation id = input.get(Registration.ENTITY);
        if (id != null && entityValid(id)) {
            this.entityId = id;
            this.entity = null;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (this.entityId != null) {
            builder.set(Registration.ENTITY, this.entityId);
        }
    }

    /* Helpers */

    /**
     * Checks if the entity is valid for this crop block.
     * @param entityID  ID to check
     * @return  True if the ID is valid, false otherwise
     */
    private static boolean entityValid(ResourceLocation entityID) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(entityID)
                .map(type -> type.is(AnimalTags.PLANTABLE))
                .orElse(false);
    }
}

package com.skd.faunagarden.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Renders the growing entity inside a fauna crop block.
 */
public class RenderFaunaCrops implements BlockEntityRenderer<FaunaCropsBlockEntity, FaunaCropsRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public RenderFaunaCrops(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public FaunaCropsRenderState createRenderState() {
        return new FaunaCropsRenderState();
    }

    @Override
    public void extractRenderState(FaunaCropsBlockEntity be, FaunaCropsRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

        BlockState blockState = be.getBlockState();
        // the settings file decides whether this block renders its block entity
        if (!Settings.shouldRenderEntity(blockState.getBlock())) {
            return;
        }
        int age = blockState.getValue(CropBlock.AGE);
        if (age == 0) {
            return;
        }

        LivingEntity entity = be.getEntity(true);
        if (entity == null) {
            return;
        }

        state.displayEntity = this.entityRenderer.extractEntity(entity, partialTick);
        if (state.displayEntity != null) {
            state.displayEntity.lightCoords = state.lightCoords;
        }
        state.scale = age < CropBlock.MAX_AGE ? age / (float) CropBlock.MAX_AGE : 1.0F;
    }

    @Override
    public void submit(FaunaCropsRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        if (state.displayEntity == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.scale(state.scale, state.scale, state.scale);
        this.entityRenderer.submit(state.displayEntity, camera, 0.0, 0.0, 0.0, poseStack, collector);
        poseStack.popPose();
    }
}

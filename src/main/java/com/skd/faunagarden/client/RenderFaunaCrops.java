package com.skd.faunagarden.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skd.faunagarden.blocks.entity.FaunaCropsBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the growing entity inside a fauna crop block.
 */
public class RenderFaunaCrops implements BlockEntityRenderer<FaunaCropsBlockEntity> {
    private static final Minecraft MC = Minecraft.getInstance();

    public RenderFaunaCrops(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(FaunaCropsBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        // the settings file decides whether this block renders its block entity
        if (!Settings.shouldRenderEntity(state.getBlock())) {
            return;
        }
        int age = state.getValue(CropBlock.AGE);
        if (age == 0) {
            return;
        }

        LivingEntity entity = be.getEntity(true);
        if (entity == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        if (age < 7) {
            float scale = age / 7f;
            poseStack.scale(scale, scale, scale);
        }
        MC.getEntityRenderDispatcher().render(entity, 0, 0, 0, 0f, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}

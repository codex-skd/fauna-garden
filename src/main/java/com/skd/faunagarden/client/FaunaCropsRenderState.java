package com.skd.faunagarden.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * Render state for a fauna crop: the extracted state of the animal being grown plus the
 * scale applied for its current growth stage.
 */
public class FaunaCropsRenderState extends BlockEntityRenderState {
    public EntityRenderState displayEntity;
    public float scale = 1.0F;
}

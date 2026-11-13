package com.misterd.encasedfans.client.ber;

import com.misterd.encasedfans.blockentity.custom.TrayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TrayBlockEntityRenderer implements BlockEntityRenderer<TrayBlockEntity, TrayRenderState> {
    private static final float SURFACE_HEIGHT = 10.0F / 16.0F;
    private static final float INPUT_SCALE = 0.5F;
    private static final float OUTPUT_SCALE = 0.3F;
    private static final float OUTPUT_OFFSET = 0.28F;
    private static final float[][] OUTPUT_CORNERS = {
            {-OUTPUT_OFFSET, -OUTPUT_OFFSET},
            {OUTPUT_OFFSET, -OUTPUT_OFFSET},
            {-OUTPUT_OFFSET, OUTPUT_OFFSET},
            {OUTPUT_OFFSET, OUTPUT_OFFSET}
    };

    private final ItemModelResolver itemModelResolver;

    public TrayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public TrayRenderState createRenderState() {
        return new TrayRenderState();
    }

    @Override
    public void extractRenderState(TrayBlockEntity tray, TrayRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tray, state, partialTicks, cameraPosition, breakProgress);
        int seed = (int) tray.getBlockPos().asLong();
        state.items.clear();
        for (int slot = 0; slot <= TrayBlockEntity.OUTPUT_SLOTS; slot++) {
            ItemStackRenderState itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, tray.getStack(slot), ItemDisplayContext.FIXED, tray.getLevel(), null, seed + slot);
            state.items.add(itemState);
        }
    }

    @Override
    public void submit(TrayRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (int slot = 0; slot < state.items.size(); slot++) {
            ItemStackRenderState itemState = state.items.get(slot);
            if (itemState.isEmpty()) {
                continue;
            }
            float x = 0;
            float z = 0;
            float scale = INPUT_SCALE;
            float height = SURFACE_HEIGHT + 0.01F;
            if (slot > TrayBlockEntity.INPUT_SLOT) {
                float[] corner = OUTPUT_CORNERS[slot - 1];
                x = corner[0];
                z = corner[1];
                scale = OUTPUT_SCALE;
                height += 0.005F * slot;
            }
            poseStack.pushPose();
            poseStack.translate(0.5F + x, height, 0.5F + z);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(scale, scale, scale);
            itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
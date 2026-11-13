package com.misterd.encasedfans.client.ber;

import com.misterd.encasedfans.blockentity.custom.TrayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TrayBlockEntityRenderer implements BlockEntityRenderer<TrayBlockEntity, TrayRenderState> {
    private static final float SURFACE_HEIGHT = 10.0F / 16.0F;
    private static final float ITEM_SCALE = 0.5F;
    private static final int MAIN_OUTPUT_SLOT = TrayBlockEntity.INPUT_SLOT + 1;

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
        ItemStack shown = tray.getStack(TrayBlockEntity.INPUT_SLOT);
        int slot = TrayBlockEntity.INPUT_SLOT;
        if (shown.isEmpty()) {
            shown = tray.getStack(MAIN_OUTPUT_SLOT);
            slot = MAIN_OUTPUT_SLOT;
        }
        itemModelResolver.updateForTopItem(state.item, shown, ItemDisplayContext.FIXED, tray.getLevel(), null, (int) tray.getBlockPos().asLong() + slot);
    }

    @Override
    public void submit(TrayRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.item.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, SURFACE_HEIGHT + 0.01F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
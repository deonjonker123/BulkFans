package com.misterd.bulkfans.client.ber;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.block.custom.IndustrialFanBlock;
import com.misterd.bulkfans.blockentity.custom.IndustrialFanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Quadrant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IndustrialFanBlockEntityRenderer implements BlockEntityRenderer<IndustrialFanBlockEntity, IndustrialFanRenderState> {
    public static final Identifier PROPELLER_MODEL = Identifier.fromNamespaceAndPath(BulkFans.MODID, "block/fan_prop");
    public static final StandaloneModelKey<BlockStateModelPart> PROPELLER_KEY = new StandaloneModelKey<>(PROPELLER_MODEL::toString);

    private static final int[] NO_TINTS = new int[0];
    private static final Map<Direction, Matrix4fc> FACING_ROTATIONS = new EnumMap<>(Direction.class);

    static {
        FACING_ROTATIONS.put(Direction.NORTH, rotation(Quadrant.R0, Quadrant.R0));
        FACING_ROTATIONS.put(Direction.EAST, rotation(Quadrant.R0, Quadrant.R90));
        FACING_ROTATIONS.put(Direction.SOUTH, rotation(Quadrant.R0, Quadrant.R180));
        FACING_ROTATIONS.put(Direction.WEST, rotation(Quadrant.R0, Quadrant.R270));
        FACING_ROTATIONS.put(Direction.UP, rotation(Quadrant.R270, Quadrant.R0));
        FACING_ROTATIONS.put(Direction.DOWN, rotation(Quadrant.R90, Quadrant.R0));
    }

    public IndustrialFanBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static Matrix4fc rotation(Quadrant x, Quadrant y) {
        return BlockModelRotation.get(Quadrant.fromXYZAngles(x, y, Quadrant.R0)).transformation().getMatrix();
    }

    @Override
    public IndustrialFanRenderState createRenderState() {
        return new IndustrialFanRenderState();
    }

    @Override
    public void extractRenderState(IndustrialFanBlockEntity fan, IndustrialFanRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(fan, state, partialTicks, cameraPosition, breakProgress);
        state.facing = fan.getBlockState().getValue(IndustrialFanBlock.FACING);
        state.angle = fan.getAngle(partialTicks);
        if (fan.getLevel() != null) {
            int frontLight = LevelRenderer.getLightCoords(fan.getLevel(), fan.getBlockPos().relative(state.facing));
            state.lightCoords = LightCoordsUtil.max(state.lightCoords, frontLight);
        }
    }

    @Override
    public void submit(IndustrialFanRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        BlockStateModelPart propeller = Minecraft.getInstance().getModelManager().getStandaloneModel(PROPELLER_KEY);
        if (propeller == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(FACING_ROTATIONS.get(state.facing));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.angle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        collector.submitMultiLayerBlockModel(poseStack, List.of(propeller), false, NO_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}

package com.misterd.encasedfans.client.ber;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class EncasedFanRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float angle;
}

package com.misterd.encasedfans.client.ber;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;
import java.util.List;

public class TrayRenderState extends BlockEntityRenderState {
    public final List<ItemStackRenderState> items = new ArrayList<>();
}
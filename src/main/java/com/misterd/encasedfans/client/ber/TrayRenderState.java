package com.misterd.encasedfans.client.ber;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class TrayRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
}
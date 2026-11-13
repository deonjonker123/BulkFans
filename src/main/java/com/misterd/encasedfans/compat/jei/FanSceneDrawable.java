package com.misterd.encasedfans.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class FanSceneDrawable implements IDrawable {
    private final ItemStack fan;
    private final ItemStack catalyst;
    private final float scale;

    public FanSceneDrawable(ItemStack fan, ItemStack catalyst, float scale) {
        this.fan = fan;
        this.catalyst = catalyst;
        this.scale = scale;
    }

    @Override
    public int getWidth() {
        return (int) (24 * scale);
    }

    @Override
    public int getHeight() {
        return (int) (20 * scale);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.item(fan, 0, 0);
        graphics.item(catalyst, 8, 4);
        graphics.pose().popMatrix();
    }
}
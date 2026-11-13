package com.misterd.encasedfans.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class FanIconDrawable implements IDrawable {
    private final ItemStack fan;
    private final ItemStack decorator;

    public FanIconDrawable(ItemStack fan, ItemStack decorator) {
        this.fan = fan;
        this.decorator = decorator;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.item(fan, x, y);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + 8, y + 8);
        graphics.pose().scale(0.5F, 0.5F);
        graphics.item(decorator, 0, 0);
        graphics.pose().popMatrix();
    }
}
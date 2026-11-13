package com.misterd.bulkfans.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ThinArrowDrawable implements IDrawable {
    private static final int COLOR = 0xFF8B8B8B;
    private static final int HEAD = 4;

    private final int width;

    public ThinArrowDrawable(int width) {
        this.width = width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return HEAD * 2 + 1;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int x, int y) {
        int middle = y + HEAD;
        graphics.fill(x, middle - 1, x + width - HEAD, middle + 2, COLOR);
        for (int i = 0; i < HEAD; i++) {
            int column = x + width - HEAD + i;
            graphics.fill(column, middle - HEAD + i, column + 1, middle + HEAD - i + 1, COLOR);
        }
    }
}
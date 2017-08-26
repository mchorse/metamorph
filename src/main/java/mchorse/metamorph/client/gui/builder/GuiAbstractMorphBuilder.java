package mchorse.metamorph.client.gui.builder;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class GuiAbstractMorphBuilder implements IGuiMorphBuilder
{
    public AbstractMorph cached;
    public FontRenderer font;

    public int x;
    public int y;
    public int w;
    public int h;

    public GuiAbstractMorphBuilder()
    {
        this.font = Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public void update(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public AbstractMorph getMorph()
    {
        return this.cached;
    }
}
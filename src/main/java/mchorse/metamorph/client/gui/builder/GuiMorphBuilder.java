package mchorse.metamorph.client.gui.builder;

import java.util.HashMap;
import java.util.Map;

import mchorse.metamorph.client.gui.GuiCreativeMenu;

/**
 * Morph builder 
 */
public class GuiMorphBuilder
{
    /**
     * Registry of morph builder panels
     */
    public static final Map<String, IGuiMorphBuilder> BUILDERS = new HashMap<String, IGuiMorphBuilder>();

    /**
     * Parent view references 
     */
    public GuiCreativeMenu parent;

    /**
     * Currently used builder
     */
    public IGuiMorphBuilder currentBuilder;

    public int x;
    public int y;
    public int w;
    public int h;

    public GuiMorphBuilder(GuiCreativeMenu parent)
    {
        this.parent = parent;
    }

    public void update(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.currentBuilder.update(x, y, w, h);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.currentBuilder.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.currentBuilder.mouseReleased(mouseX, mouseY, state);
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        this.currentBuilder.keyTyped(typedChar, keyCode);
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.currentBuilder.draw(mouseX, mouseY, partialTicks);
    }
}
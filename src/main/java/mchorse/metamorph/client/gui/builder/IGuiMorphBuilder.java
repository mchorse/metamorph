package mchorse.metamorph.client.gui.builder;

import mchorse.metamorph.api.morphs.AbstractMorph;

/**
 * Morph builder GUI interface
 * 
 * This interface is responsible for providing essential hooks for 
 * basic event handling and also some methods for generating morphs.
 */
public interface IGuiMorphBuilder
{
    /**
     * Update this GUI module with given frame 
     */
    public void update(int x, int y, int w, int h);

    /**
     * Get the morph 
     */
    public AbstractMorph getMorph();

    /**
     * Mouse down 
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton);

    /**
     * Mouse up 
     */
    public void mouseReleased(int mouseX, int mouseY, int state);

    /**
     * Keyboard key was pressed 
     */
    public void keyTyped(char typedChar, int keyCode);

    /**
     * Draw on the screen 
     */
    public void draw(int mouseX, int mouseY, float partialTicks);
}
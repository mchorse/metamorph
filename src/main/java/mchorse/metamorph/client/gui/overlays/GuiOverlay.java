package mchorse.metamorph.client.gui.overlays;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

/**
 * GUI acquired morph overlay class
 * 
 * This class is responsible for displaying acquired morph in a more graphic
 * way than with chat message.
 */
public class GuiOverlay extends Gui
{
    /**
     * List of acquired morphs 
     */
    public List<AcquiredMorph> morphs = new ArrayList<AcquiredMorph>();

    /**
     * Disappearing cap 
     */
    public final int cap = 60;

    /**
     * Render acquired morph overlays 
     */
    public void render(int width, int height)
    {
        if (this.morphs.size() == 0)
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        Iterator<AcquiredMorph> iterator = this.morphs.iterator();

        while (iterator.hasNext())
        {
            AcquiredMorph morph = iterator.next();
            String string = I18n.format("metamorph.gui.acquired");

            /* Let's calculate some stuff */
            boolean disappear = morph.timer <= this.cap;

            int progress = this.cap - morph.timer;
            int alpha = (int) (255 * morph.timer / this.cap);
            int y = height - 10 + (disappear ? (int) (40 * (float) progress / this.cap) : 0);
            int color = disappear ? 0x00ffffff + (alpha << 24) : 0xffffffff;

            MorphUtils.renderOnScreen(morph.morph, mc.player, 15, y, 15, (float) alpha / 255);

            /* Render overlay */
            font.drawString(string, 30, y - 7, color);

            morph.timer--;

            /* Remove the morph is the timer is run out */
            if (morph.timer <= 0)
            {
                iterator.remove();
            }
        }
    }

    /**
     * Add an acquired morph to this overlay. 
     */
    public void add(AbstractMorph acquired)
    {
        for (AcquiredMorph morph : this.morphs)
        {
            if (morph.timer > this.cap)
            {
                morph.timer = this.cap;
            }
        }

        this.morphs.add(new AcquiredMorph(acquired));
    }

    /**
     * Acquired morph class
     * 
     * This class is responsible for containing information about currently 
     * acquired morph.
     */
    public static class AcquiredMorph
    {
        public AbstractMorph morph;
        public int timer = 240;

        public AcquiredMorph(AbstractMorph morph)
        {
            this.morph = morph;
        }
    }
}
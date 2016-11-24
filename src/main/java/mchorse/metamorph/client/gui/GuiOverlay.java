package mchorse.metamorph.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

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
        FontRenderer font = mc.fontRendererObj;
        Iterator<AcquiredMorph> iterator = this.morphs.iterator();

        while (iterator.hasNext())
        {
            AcquiredMorph morph = iterator.next();

            /* Let's calculate some stuff */
            boolean disappear = morph.timer <= this.cap;

            int progress = this.cap - morph.timer;
            int alpha = (int) (255 * morph.timer / this.cap);
            int x = width - 36;
            int y = height - 6 + (disappear ? (int) (40 * (float) progress / this.cap) : 0);
            int color = disappear ? 0x00ffffff + (alpha << 24) : 0xffffffff;

            /* Prepare the model */
            Model data = MorphManager.INSTANCE.morphs.get(morph.morph).model;
            ModelCustom model = ModelCustom.MODELS.get(morph.morph);
            String string = "Acquired";

            model.pose = model.model.poses.get("standing");
            model.swingProgress = 0;

            /* Render overlay */
            mc.renderEngine.bindTexture(data.defaultTexture);
            GuiMenu.drawModel(model, mc.thePlayer, x + 18, y, 15, (float) alpha / 255);
            font.drawString(string, x - font.getStringWidth(string), y - 10, color);

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
    public void add(String name)
    {
        for (AcquiredMorph morph : this.morphs)
        {
            if (morph.timer > 60)
            {
                morph.timer = 60;
            }
        }

        this.morphs.add(new AcquiredMorph(name));
    }

    /**
     * Acquired morph class
     * 
     * This class is responsible for containing information about currently 
     * acquired morph.
     */
    public static class AcquiredMorph
    {
        public String morph;
        public int timer = 240;

        public AcquiredMorph(String morph)
        {
            this.morph = morph;
        }
    }
}
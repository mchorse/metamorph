package mchorse.metamorph.bodypart;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

/**
 * Body part list which displays body parts
 */
public class GuiBodyPartListElement extends GuiListElement<BodyPart>
{
    public GuiBodyPartListElement(Minecraft mc, Consumer<List<BodyPart>> callback)
    {
        super(mc, callback);

        this.scroll.scrollItemSize = 24;
    }

    @Override
    protected void drawElementPart(BodyPart element, int i, int x, int y, boolean hover, boolean selected)
    {
        GuiContext context = GuiBase.getCurrent();

        if (!element.morph.isEmpty())
        {
            GuiDraw.scissor(x, y, this.scroll.w, this.scroll.scrollItemSize, context);
            element.morph.get().renderOnScreen(this.mc.player, x + this.scroll.w - 16, y + 30, 20, 1);
            GuiDraw.unscissor(context);
        }

        super.drawElementPart(element, i, x, y, hover, selected);
    }

    @Override
    protected String elementToString(BodyPart element)
    {
        String label = element.limb.isEmpty() ? "_" : element.limb;

        if (!element.morph.isEmpty())
        {
            AbstractMorph morph = element.morph.get();

            if (morph.hasCustomName())
            {
                label += " - " + element.morph.get().getDisplayName();
            }
        }

        return label;
    }
}
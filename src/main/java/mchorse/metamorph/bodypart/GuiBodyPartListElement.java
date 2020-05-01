package mchorse.metamorph.bodypart;

import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
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

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected String elementToString(BodyPart element)
    {
        String label = element.morph.isEmpty() ? "" : element.morph.get().getDisplayName();

        if (!element.limb.isEmpty())
        {
            label += (!label.isEmpty() ? " - " + element.limb : element.limb);
        }

        return label;
    }
}
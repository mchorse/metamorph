package mchorse.metamorph.client.gui.elements;

import java.util.ArrayList;
import java.util.List;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class GuiAbstractMorph extends GuiElement
{
    /**
     * List of available morph editors 
     */
    public static final List<GuiAbstractMorph> EDITORS = new ArrayList<GuiAbstractMorph>();

    public GuiTextElement data;

    private AbstractMorph morph;
    private boolean error;

    /**
     * Prepare and return a morph editor based on given morph 
     */
    public static GuiAbstractMorph fromMorph(AbstractMorph morph)
    {
        for (int i = EDITORS.size() - 1; i >= 0; i--)
        {
            GuiAbstractMorph editor = EDITORS.get(i);

            if (editor.canEdit(morph))
            {
                editor.startEdit(morph);

                return editor;
            }
        }

        return null;
    }

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.createChildren();
        this.data = new GuiTextElement(mc, 10000, (str) -> this.editNbt(str));
        this.data.resizer().parent(this.area).set(10, 0, 0, 20).w(1, -20).y(1, -30);

        this.children.add(this.data);
    }

    public boolean canEdit(AbstractMorph morph)
    {
        return morph != null;
    }

    public void startEdit(AbstractMorph morph)
    {
        this.morph = morph;
        this.error = false;

        NBTTagCompound tag = new NBTTagCompound();

        morph.toNBT(tag);
        this.data.setText(tag.toString());
    }

    private void editNbt(String str)
    {
        try
        {
            this.morph.fromNBT(JsonToNBT.getTagFromJson(str));
            this.error = false;
        }
        catch (Exception e)
        {
            this.error = true;
        }
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.drawMorph(mouseX, mouseY, partialTicks);

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        this.font.drawStringWithShadow("NBT data", this.data.area.x, this.data.area.y - 12, this.error ? 0xffff3355 : 0xffffff);
    }

    private void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            this.morph.renderOnScreen(this.mc.thePlayer, this.area.getX(0.5F), this.area.getY(0.66F), this.area.h / 3, 1);
        }
        catch (Exception e)
        {}
    }
}
package mchorse.metamorph.client.gui.elements;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAbstractMorph extends GuiElement
{
    public GuiTextElement data;

    protected AbstractMorph morph;
    protected boolean error;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.createChildren();
        this.data = new GuiTextElement(mc, 10000, (str) -> this.editNBT(str));
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

        this.updateNBT();
    }

    public void finishEdit()
    {}

    protected void editNBT(String str)
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

    protected void updateNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        morph.toNBT(tag);
        this.data.setText(tag.toString());
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.drawMorph(mouseX, mouseY, partialTicks);

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (this.data.isVisible())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.nbt_data"), this.data.area.x, this.data.area.y - 12, this.error ? 0xffff3355 : 0xffffff);
        }
    }

    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            this.morph.renderOnScreen(this.mc.player, this.area.getX(0.5F), this.area.getY(0.66F), this.area.h / 3, 1);
        }
        catch (Exception e)
        {}
    }
}
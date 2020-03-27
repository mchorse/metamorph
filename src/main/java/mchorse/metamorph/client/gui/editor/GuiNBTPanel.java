package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("rawtypes")
public class GuiNBTPanel extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
    public GuiTextElement data;
    public boolean error;

    public GuiNBTPanel(Minecraft mc, GuiAbstractMorph editor)
    {
        super(mc, editor);

        this.data = new GuiTextElement(mc, 1000000, (str) -> this.editNBT(str));
        this.data.flex().parent(this.area).set(10, 0, 0, 20).w(1, -20).y(1, -30);

        this.add(this.data);
    }

    public void updateNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        this.morph.toNBT(tag);
        this.data.setText(tag.toString());
    }

    public void editNBT(String str)
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
    public void startEditing()
    {
        this.error = false;

        this.updateNBT();
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        if (this.data.isVisible())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.nbt_data"), this.data.area.x, this.data.area.y - 12, this.error ? 0xffff3355 : 0xffffff);
        }
    }
}
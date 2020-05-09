package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("rawtypes")
public class GuiMorphPanel<T extends AbstractMorph, E extends GuiAbstractMorph> extends GuiElement
{
    public E editor;
    public T morph;

    public GuiMorphPanel(Minecraft mc, E editor)
    {
        super(mc);

        this.editor = editor;
    }

    public void startEditing()
    {}

    public void finishEditing()
    {}

    public void fillData(T morph)
    {
        this.morph = morph;
    }

    public void fromNBT(NBTTagCompound tag)
    {}

    public NBTTagCompound toNBT()
    {
        return new NBTTagCompound();
    }
}
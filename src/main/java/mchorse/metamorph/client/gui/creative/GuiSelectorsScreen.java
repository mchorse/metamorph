package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.sections.UserSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.render.EntitySelector;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class GuiSelectorsScreen extends GuiBase
{
    public GuiSelectorEditor editor;
    public GuiCreativeMorphsMenu menu;

    public GuiSelectorsScreen(Minecraft mc)
    {
        this.editor = new GuiSelectorEditor(mc, true);
        this.menu = new GuiCreativeMorphsMenu(mc, true, this::setMorph);
        this.menu.setVisible(true);

        this.editor.flex().relative(this.viewport).wTo(this.menu.flex()).h(1F);
        this.menu.flex().relative(this.viewport).x(140).h(1F).wTo(this.root.flex(), 1F);

        this.root.add(this.menu, this.editor);
    }

    private void setMorph(AbstractMorph morph)
    {
        EntitySelector selector = this.editor.getSelector();
        NBTTagCompound tag =null;

        if (selector != null)
        {
            tag = selector.morph;
        }

        this.editor.setMorph(morph);

        if (tag != null)
        {
            AbstractMorph oldMorph = MorphManager.INSTANCE.morphFromNBT(tag);

            if (oldMorph != null)
            {
                this.addToRecent(oldMorph);
            }
        }
    }

    private void addToRecent(AbstractMorph oldMorph)
    {
        for (MorphSection section : MorphManager.INSTANCE.list.sections)
        {
            if (section instanceof UserSection)
            {
                UserSection user = (UserSection) section;

                if (user.recent.getEqual(oldMorph) == null)
                {
                    user.recent.add(oldMorph);

                    return;
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return Metamorph.pauseGUIInSP.get();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GuiDraw.drawCustomBackground(0, 0, this.width, this.height);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
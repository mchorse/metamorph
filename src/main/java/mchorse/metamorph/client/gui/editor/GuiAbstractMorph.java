package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiAbstractMorph<T extends AbstractMorph> extends GuiPanelBase<GuiMorphPanel>
{
    public GuiButtonElement finish;
    public GuiNBTPanel nbt;
    public GuiSettingsPanel settings;

    protected GuiMorphPanel defaultPanel;

    public T morph;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.finish = new GuiButtonElement(mc, I18n.format("metamorph.gui.finish"), null);
        this.finish.flex().parent(this.area).set(0, 10, 55, 20).x(1, -65);
        this.settings = new GuiSettingsPanel(mc, this);
        this.defaultPanel = this.nbt = new GuiNBTPanel(mc, this);

        this.registerPanel(this.settings, I18n.format("metamorph.gui.panels.settings"), MMIcons.PROPERTIES);
        this.registerPanel(this.nbt, I18n.format("metamorph.gui.panels.nbt"), MMIcons.CODE);
        this.getChildren().add(2, this.finish);
    }

    /**
     * Switch current morph panel to given one
     */
    @Override
    public void setPanel(GuiMorphPanel panel)
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.finishEditing();
        }

        super.setPanel(panel);
        panel.startEditing();
    }

    public boolean canEdit(AbstractMorph morph)
    {
        return morph != null;
    }

    public void startEdit(T morph)
    {
        this.morph = morph;

        for (GuiMorphPanel panel : this.panels)
        {
            panel.fillData(morph);
        }

        this.setPanel(this.defaultPanel);
    }

    public void finishEdit()
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.finishEditing();
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        this.drawMorph(context);

        super.draw(context);
    }

    protected void drawMorph(GuiContext context)
    {
        try
        {
            this.morph.renderOnScreen(this.mc.player, this.area.mx(), this.area.y(0.66F), this.area.h / 3, 1);
        }
        catch (Exception e)
        {}
    }
}
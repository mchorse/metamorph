package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiAbstractMorph<T extends AbstractMorph> extends GuiPanelBase<GuiMorphPanel>
{
    public static final ResourceLocation PANEL_ICONS = new ResourceLocation("metamorph:textures/gui/icons.png");

    public GuiButtonElement<GuiButton> finish;
    public GuiNBTPanel nbt;

    protected GuiMorphPanel defaultPanel;

    public T morph;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.createChildren();

        this.finish = GuiButtonElement.button(mc, I18n.format("metamorph.gui.finish"), null);
        this.finish.resizer().parent(this.area).set(0, 10, 55, 20).x(1, -65);
        this.defaultPanel = this.nbt = new GuiNBTPanel(mc, this);

        this.registerPanel(this.nbt, PANEL_ICONS, I18n.format("metamorph.gui.panels.nbt"), 0, 0, 0, 16);
        this.children.elements.add(2, this.finish);
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
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.drawMorph(mouseX, mouseY, partialTicks);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
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
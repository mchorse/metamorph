package mchorse.metamorph.client.gui.editor;

import java.util.ArrayList;
import java.util.List;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiAbstractMorph<T extends AbstractMorph> extends GuiElement
{
    public static final ResourceLocation PANEL_ICONS = new ResourceLocation("metamorph:textures/gui/icons.png");

    public GuiButtonElement<GuiButton> finish;
    public GuiDelegateElement<GuiMorphPanel> view;
    public GuiNBTPanel nbt;

    public GuiElements<GuiButtonElement<GuiTextureButton>> buttons;
    public List<GuiMorphPanel> panels = new ArrayList<GuiMorphPanel>();

    protected GuiMorphPanel defaultPanel;

    public T morph;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.createChildren();

        this.finish = GuiButtonElement.button(mc, I18n.format("metamorph.gui.finish"), null);
        this.finish.resizer().parent(this.area).set(0, 10, 55, 20).x(1, -65);

        this.defaultPanel = this.nbt = new GuiNBTPanel(mc, this);

        this.view = new GuiDelegateElement<GuiMorphPanel>(mc, this.nbt);
        this.view.resizer().parent(this.area).set(0, 0, 1, 1, Measure.RELATIVE).h(1, -20);

        this.buttons = new GuiElements<GuiButtonElement<GuiTextureButton>>();
        GuiDrawable drawable = new GuiDrawable((v) ->
        {
            for (int i = 0, c = this.panels.size(); i < c; i++)
            {
                if (this.view.delegate == this.panels.get(i))
                {
                    Area area = this.buttons.elements.get(i).area;

                    Gui.drawRect(area.x - 2, area.y - 2, area.getX(1) + 2, area.getY(1) + 2, 0x880088ff);
                }
            }
        });

        this.registerPanel(this.nbt, PANEL_ICONS, I18n.format("metamorph.gui.panels.nbt"), 0, 0, 0, 16);
        this.children.add(drawable, this.buttons, this.finish, this.view);
    }

    /**
     * Register a panel with given texture and tooltip 
     */
    public void registerPanel(GuiMorphPanel panel, ResourceLocation texture, String tooltip, int x, int y, int ax, int ay)
    {
        GuiButtonElement<GuiTextureButton> button = GuiButtonElement.icon(this.mc, texture, x, y, ax, ay, (b) -> this.setPanel(panel));

        if (tooltip != null && !tooltip.isEmpty())
        {
            button.tooltip(tooltip, TooltipDirection.TOP);
        }

        if (this.buttons.elements.isEmpty())
        {
            button.resizer().parent(this.area).set(0, 0, 16, 16).x(1, -18).y(1, -18);
        }
        else
        {
            GuiButtonElement<GuiTextureButton> last = this.buttons.elements.get(this.buttons.elements.size() - 1);

            button.resizer().relative(last.resizer()).set(-20, 0, 16, 16);
        }

        this.panels.add(panel);
        this.buttons.add(button);
    }

    /**
     * Switch current morph panel to given one
     */
    public void setPanel(GuiMorphPanel panel)
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.finishEditing();
        }

        this.view.setDelegate(panel);
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
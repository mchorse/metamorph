package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphs;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiAbstractMorph<T extends AbstractMorph> extends GuiPanelBase<GuiMorphPanel>
{
    public GuiButtonElement finish;
    public GuiSettingsPanel settings;

    protected GuiMorphPanel defaultPanel;

    public T morph;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.finish = new GuiButtonElement(mc, I18n.format("metamorph.gui.finish"), null);
        this.finish.flex().relative(this.area).set(0, 0, 60, 20).y(1F, -20);
        this.defaultPanel = this.settings = new GuiSettingsPanel(mc, this);

        this.registerPanel(this.settings, I18n.format("metamorph.gui.panels.settings"), MMIcons.PROPERTIES);
        this.getChildren().add(2, this.finish);

        this.keys().register("Finish editing", Keyboard.KEY_F, () ->
        {
            this.finish.clickItself(GuiBase.getCurrent());
            return true;
        });
    }

    /**
     * Get presets
     */
    public List<Label<NBTTagCompound>> getPresets(T morph)
    {
        return Collections.emptyList();
    }

    protected void addPreset(List<Label<NBTTagCompound>> list, String label, String json)
    {
        try
        {
            list.add(new Label<NBTTagCompound>(label, JsonToNBT.getTagFromJson(json)));
        }
        catch (Exception e)
        {}
    }

    /**
     * Get quick access editing fields
     */
    public List<GuiElement> getFields(Minecraft mc, GuiCreativeMorphs morphs, T morph)
    {
        List<GuiElement> elements = new ArrayList<GuiElement>();
        GuiTextElement displayName = new GuiTextElement(mc, (name) ->
        {
            morphs.getSelected().displayName = name;
            morphs.markDirty();
        });

        displayName.setText(morph.displayName);
        elements.add(GuiLabel.create("Display name", this.font.FONT_HEIGHT));
        elements.add(displayName);

        return elements;
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
        Gui.drawRect(this.area.x, this.area.ey() - 20, this.area.ex(), this.area.ey(), 0xee000000);

        super.draw(context);
    }

    protected void drawMorph(GuiContext context)
    {
        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);

        try
        {
            this.morph.renderOnScreen(this.mc.player, this.area.mx(), this.area.y(0.66F), this.area.h / 3, 1);
        }
        catch (Exception e)
        {}

        GuiDraw.unscissor(context);
    }
}
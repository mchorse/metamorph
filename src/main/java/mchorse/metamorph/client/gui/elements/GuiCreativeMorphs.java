package mchorse.metamorph.client.gui.elements;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.resizers.Resizer;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.MorphList;
import mchorse.metamorph.api.creative.MorphSection;
import mchorse.metamorph.api.events.ReloadMorphs;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Scroll list of available morphs
 * 
 * More morphs than presented in this menu are available, but the problem that 
 * it's impossible to list all variation of those morphs. iChun probably knows 
 * it, that's why he doesn't bother with a GUI of all available morphs.
 */
public class GuiCreativeMorphs extends GuiElement
{
    /**
     * Cached previous filter. Used for avoiding double recalculations 
     */
    private String previousFilter = "";

    /**
     * Morph consumer 
     */
    public Consumer<AbstractMorph> callback;

    /**
     * Available morph editors 
     */
    private List<GuiAbstractMorph> editors;

    /**
     * Morph editor 
     */
    public GuiDelegateElement<GuiAbstractMorph> editor;

    public GuiTextElement search;
    public GuiButtonElement edit;

    public GuiScrollElement morphs;

    private GuiMorphSection selected;

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiCreativeMorphs(Minecraft mc)
    {
        super(mc);

        MorphList list = MorphManager.INSTANCE.list;

        list.update(mc.world);
        MinecraftForge.EVENT_BUS.post(new ReloadMorphs());

        this.editor = new GuiDelegateElement<GuiAbstractMorph>(mc, null);
        this.editor.flex().parent(this.area).set(0, 0, 1, 1, Resizer.Measure.RELATIVE);

        this.search = new GuiTextElement(mc, (filter) -> this.setFilter(filter));
        this.search.focus(GuiBase.getCurrent());
        this.search.flex().parent(this.area).set(10, 0, 0, 20).w(1, -85).y(1, -30);

        this.edit = new GuiButtonElement(mc, I18n.format("metamorph.gui.edit"), (b) -> this.toggleEditMode());
        this.edit.flex().parent(this.area).set(0, 0, 60, 20).x(1, -70).y(1, - 30);

        this.morphs = new GuiScrollElement(mc);
        this.morphs.flex().parent(this.area).wh(1F, 1F);

        GuiElement previous = null;

        for (MorphSection section : list.sections)
        {
            GuiElement element = section.getGUI(mc, this::setMorph);

            if (previous == null)
            {
                element.flex().parent(this.morphs.area).w(1, 0);
            }
            else
            {
                element.flex().relative(previous.resizer()).y(1, 0).w(1, 0);
            }

            previous = element;

            this.morphs.add(element);
        }

        this.add(this.morphs, this.edit, this.search, this.editor);
    }

    public boolean isEditMode()
    {
        return this.editor.delegate != null;
    }

    public void toggleEditMode()
    {
        AbstractMorph morph = this.getSelected();

        if (!this.isEditMode())
        {
            if (morph != null)
            {
                morph = morph.clone(true);
            }

            GuiAbstractMorph editor = this.getMorphEditor(morph);

            if (editor != null)
            {
                editor.finish.callback = this.getToggleCallback();

                this.editor.setDelegate(editor);
                this.setSelected(morph, false, false);
                this.setMorph(morph);
            }
        }
        else
        {
            this.editor.delegate.finishEdit();
            this.editor.setDelegate(null);

            this.setMorph(morph);
        }

        boolean hide = this.editor.delegate == null;

        this.search.setVisible(hide);
        this.edit.setVisible(hide);
        this.morphs.setVisible(hide);
    }

    public void finish()
    {
        if (this.isEditMode())
        {
            this.editor.delegate.finishEdit();
        }
    }

    protected Consumer<GuiButtonElement> getToggleCallback()
    {
        return this.edit.callback;
    }

    private GuiAbstractMorph getMorphEditor(AbstractMorph morph)
    {
        if (this.editors == null)
        {
            this.editors = new ArrayList<GuiAbstractMorph>();
            MorphManager.INSTANCE.registerMorphEditors(this.mc, this.editors);
        }

        for (GuiAbstractMorph editor : this.editors)
        {
            if (editor.canEdit(morph))
            {
                editor.startEdit(morph);

                return editor;
            }
        }

        return null;
    }

    /**
     * Added for compatibility 
     */
    public void setFilter(String filter)
    {
        this.setFilter(filter, true);
    }

    /**
     * Set filter for search
     * 
     * This method is responsible for recalculating the hidden flag of the 
     * individual cells and changing heights and y position of each category.
     */
    public void setFilter(String filter, boolean select)
    {
        if (filter.equals(this.previousFilter))
        {
            return;
        }

        String lcfilter = filter.toLowerCase();

        /* TODO: search */
    }

    public void setSelected(AbstractMorph morph)
    {
        this.setSelected(morph, true, true);
    }

    /**
     * Set selected morph 
     */
    public void setSelected(AbstractMorph morph, boolean restore, boolean compare)
    {
        // this.initiateCategories(morph, compare);

        /* Make sure to restore the state from previous time */
        if (restore)
        {
            String prevFilter = this.previousFilter;

            if (!prevFilter.isEmpty())
            {
                this.previousFilter = "";
                this.setFilter(prevFilter, false);
            }
        }
    }

    /**
     * Get currently selected morph 
     */
    public AbstractMorph getSelected()
    {
        return this.selected == null ? null : this.selected.morph;
    }

    public void setMorph(GuiMorphSection selected)
    {
        if (this.selected != null && selected != this.selected)
        {
            this.selected.morph = null;
        }

        this.selected = selected;
        this.setMorph(selected.morph);
    }

    public void setMorph(AbstractMorph morph)
    {
        if (this.callback != null)
        {
            this.callback.accept(morph);
        }
    }

    @Override
    public void resize()
    {
        super.resize();

        List<IGuiElement> children = this.morphs.getChildren();

        int firstY = ((GuiElement) children.get(0)).area.y;
        int lastY = ((GuiElement) children.get(children.size() - 1)).area.ey();

        this.morphs.scroll.scrollSize = lastY - firstY + 30;
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        if (!this.isEditMode() && !this.search.field.isFocused() && this.search.field.getText().isEmpty())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.search"), this.search.area.x + 5, this.search.area.y + 6, 0x888888);
        }
    }
}
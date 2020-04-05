package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.resizers.layout.ColumnResizer;
import mchorse.mclib.client.gui.utils.resizers.layout.RowResizer;
import mchorse.mclib.utils.Timer;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.MorphList;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.sections.UserSection;
import mchorse.metamorph.api.events.ReloadMorphs;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

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

    public GuiElement bar;
    public GuiTextElement search;

    public GuiElement screen;
    public GuiQuickEditor quickEditor;
    public GuiScrollElement morphs;

    public UserSection user;
    private List<GuiMorphSection> sections = new ArrayList<GuiMorphSection>();
    private GuiMorphSection userSection;
    private GuiMorphSection selected;
    private boolean scrollTo;

    private Timer timer = new Timer(100);

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiCreativeMorphs(Minecraft mc, Consumer<AbstractMorph> callback)
    {
        super(mc);

        MorphList list = MorphManager.INSTANCE.list;

        list.update(mc.world);
        MinecraftForge.EVENT_BUS.post(new ReloadMorphs());

        this.callback = callback;
        this.editor = new GuiDelegateElement<GuiAbstractMorph>(mc, null);
        this.editor.flex().relative(this.area).wh(1F, 1F);

        this.screen = new GuiElement(mc);
        this.screen.flex().relative(this.area).wh(1F, 1F);

        /* Create quick editor */
        this.quickEditor = new GuiQuickEditor(mc, this);
        this.quickEditor.flex().relative(this.area).x(1F, -200).wTo(this.flex(), 1F).h(1F);
        this.quickEditor.setVisible(false);

        /* Create morph panels */
        this.morphs = new GuiScrollElement(mc);
        this.morphs.flex().relative(this.area).wh(1F, 1F);
        ColumnResizer.apply(this.morphs, 0).vertical().stretch().scroll();
        this.setupMorphs(list);

        /* Initiate bottom bar */
        this.bar = new GuiElement(mc);
        this.search = new GuiTextElement(mc, this::setFilter);

        this.bar.flex().relative(this.morphs.area).set(10, 0, 0, 20).y(1, -30).w(1, -20);
        RowResizer.apply(this.bar, 5).preferred(1).height(20);
        this.bar.add(this.search);

        this.screen.add(this.morphs, this.bar, this.quickEditor);
        this.add(this.screen, new GuiDrawable(this::drawOverlay), this.editor);

        /* Morph editor keybinds */
        this.morphs.keys()
            .register("Edit", Keyboard.KEY_E, () ->
            {
                this.toggleEditMode();
                return true;
            })
            .register("Quick edit", Keyboard.KEY_Q, () ->
            {
                this.toggleQuickEdit();
                return true;
            });
    }

    private void setupMorphs(MorphList list)
    {
        for (MorphSection section : list.sections)
        {
            GuiMorphSection element = section.getGUI(this.mc, this, this::setMorph);

            if (section instanceof UserSection)
            {
                this.user = (UserSection) section;
                this.userSection = element;
            }

            element.flex();
            this.sections.add(element);
            this.morphs.add(element);
        }

        this.sections.get(this.sections.size() - 1).last = true;
    }

    public void markDirty()
    {
        this.timer.mark();
    }

    public void disableDirty()
    {
        if (this.timer.enabled)
        {
            this.timer.enabled = false;
            this.syncSelected();
        }
    }

    /* Editing modes */

    public boolean isEditMode()
    {
        return this.editor.delegate != null;
    }

    public void toggleQuickEdit()
    {
        if (this.isEditMode())
        {
            return;
        }

        this.quickEditor.toggleVisible();

        if (this.quickEditor.isVisible())
        {
            AbstractMorph morph = this.getSelected();

            if (morph != null)
            {
                this.quickEditor.setMorph(morph, this.getMorphEditor(morph));
            }
            else
            {
                return;
            }

            this.morphs.flex().wTo(this.quickEditor.flex());
        }
        else
        {
            this.morphs.flex().w(1F);
        }

        this.resize();
    }

    public void toggleEditMode()
    {
        AbstractMorph morph = this.getSelected();

        if (!this.isEditMode())
        {
            this.disableDirty();

            if (!this.isUserSectionSelected())
            {
                if (morph != null)
                {
                    morph = morph.clone(true);
                }
            }

            GuiAbstractMorph editor = this.getMorphEditor(morph);

            if (editor != null)
            {
                editor.finish.callback = this.getToggleCallback();

                this.editor.setDelegate(editor);
                this.setMorph(morph);
            }
        }
        else
        {
            AbstractMorph edited = this.editor.delegate.morph;

            if (edited != null)
            {
                this.setSelected(edited);
            }

            this.editor.delegate.finishEdit();
            this.syncSelected();

            this.editor.setDelegate(null);
            this.setMorph(morph);
        }

        boolean hide = this.editor.delegate == null;

        this.screen.setVisible(hide);
    }

    public void finish()
    {
        this.disableDirty();

        if (this.isEditMode())
        {
            this.editor.delegate.finishEdit();
            this.syncSelected();
        }
    }

    public boolean isUserSectionSelected()
    {
        return this.selected == this.userSection;
    }

    public void syncSelected()
    {
        AbstractMorph morph = this.getSelected();

        if (morph != null)
        {
            this.selected.category.edit(morph);
        }
    }

    protected Consumer<GuiButtonElement> getToggleCallback()
    {
        return (button) -> this.toggleEditMode();
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

    /* Morph selection and filtering */

    /**
     * Get currently selected morph
     */
    public AbstractMorph getSelected()
    {
        if (this.isEditMode())
        {
            AbstractMorph morph = this.editor.delegate.morph;

            if (morph != null)
            {
                return morph;
            }
        }

        return this.selected == null ? null : this.selected.morph;
    }

    public void setMorph(GuiMorphSection selected)
    {
        this.disableDirty();

        if (this.selected != null && selected != this.selected)
        {
            this.selected.reset();
        }

        this.selected = selected;
        this.setMorph(selected.morph);
        this.syncQuickEditor();
    }

    public void setMorph(AbstractMorph morph)
    {
        if (this.callback != null)
        {
            this.callback.accept(morph);
        }
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

        String lcfilter = filter.toLowerCase().trim();

        for (GuiMorphSection section : this.sections)
        {
            section.setFilter(lcfilter);
        }

        this.previousFilter = lcfilter;
    }

    /**
     * Set selected morph 
     */
    public AbstractMorph setSelected(AbstractMorph morph)
    {
        this.disableDirty();

        if (this.selected != null)
        {
            this.selected.reset();
        }

        if (morph != null)
        {
            AbstractMorph found = this.user.recent.getEqual(morph);
            MorphCategory selectedCategory = null;
            GuiMorphSection selectedSection = null;

            searchForMorph:
            for (GuiMorphSection section : this.sections)
            {
                for (MorphCategory category : section.section.categories)
                {
                    found = category.getEqual(morph);

                    if (found != null)
                    {
                        selectedCategory = category;
                        selectedSection = section;

                        break searchForMorph;
                    }
                }
            }

            if (found == null)
            {
                this.user.recent.add(morph);
                found = morph;
                selectedCategory = this.user.recent;
                selectedSection = this.userSection;
            }

            this.selected = selectedSection;
            selectedSection.morph = found;
            selectedSection.category = selectedCategory;

            this.scrollTo = true;
        }
        else
        {
            this.selected = null;
        }

        this.syncQuickEditor();

        return this.getSelected();
    }

    protected void syncQuickEditor()
    {
        if (this.quickEditor.isVisible())
        {
            AbstractMorph morph = this.getSelected();

            if (morph != null)
            {
                this.quickEditor.setMorph(morph, this.getMorphEditor(morph));
            }
            else
            {
                this.toggleQuickEdit();
            }
        }
    }

    /**
     * Scroll to the selected morph
     *
     * This method heavily relies on the elements drawn before hand
     */
    public void scrollTo()
    {
        AbstractMorph morph = this.getSelected();

        if (morph == null)
        {
            return;
        }

        int y = 0;

        for (GuiMorphSection section : this.sections)
        {
            if (section.morph == morph)
            {
                this.morphs.scroll.scrollIntoView(y + section.selectedY, section.cellHeight + 30);

                break;
            }

            y += section.height;
        }
    }

    /* Element overrides */

    @Override
    public void draw(GuiContext context)
    {
        if (this.timer.checkReset())
        {
            this.syncSelected();
        }

        super.draw(context);

        if (this.scrollTo)
        {
            this.scrollTo();
            this.scrollTo = false;
        }
    }

    private void drawOverlay(GuiContext context)
    {
        /* Draw the name of the morph */
        if (!this.isEditMode())
        {
            AbstractMorph morph = this.getSelected();
            String selected = morph != null ? morph.getDisplayName() : I18n.format("metamorph.gui.no_morph");
            Area area = this.search.area;
            int w = Math.max(this.font.getStringWidth(selected), morph != null ? this.font.getStringWidth(morph.name) : 0);

            if (morph != null)
            {
                Gui.drawRect(area.x, area.y - 26, area.x + w + 8, area.y, 0x88000000);
                this.font.drawStringWithShadow(selected, area.x + 4, area.y - 22, 0xffffffff);
                this.font.drawStringWithShadow(morph.name, area.x + 4, area.y - 11, 0x888888);
            }
            else
            {
                Gui.drawRect(area.x, area.y - 16, area.x + w + 8, area.y, 0x88000000);
                this.font.drawStringWithShadow(selected, area.x + 4, area.y - 11, 0xffffffff);
            }
        }

        if (!this.isEditMode() && !this.search.field.isFocused() && this.search.field.getText().isEmpty())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.search"), this.search.area.x + 5, this.search.area.y + 6, 0x888888);
        }
    }
}
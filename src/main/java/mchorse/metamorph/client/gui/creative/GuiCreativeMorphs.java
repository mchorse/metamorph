package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Keybind;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Timer;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
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
import java.util.Stack;
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
    private String filter = "";

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
    public GuiButtonElement edit;

    public GuiElement screen;
    public GuiQuickEditor quickEditor;
    public GuiScrollElement morphs;

    public UserSection user;
    private List<GuiMorphSection> sections = new ArrayList<GuiMorphSection>();
    private GuiMorphSection userSection;
    private GuiMorphSection selected;
    private boolean scrollTo;

    private Timer timer = new Timer(100);
    private Stack<NestedEdit> nestedEdits = new Stack<NestedEdit>();

    protected Keybind exitKey;

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiCreativeMorphs(Minecraft mc, Consumer<AbstractMorph> callback)
    {
        super(mc);

        this.callback = callback;
        this.editor = new GuiDelegateElement<GuiAbstractMorph>(mc, null);
        this.editor.flex().relative(this).wh(1F, 1F);

        this.screen = new GuiElement(mc);
        this.screen.flex().relative(this).wh(1F, 1F);

        /* Create quick editor */
        this.quickEditor = new GuiQuickEditor(mc, this);
        this.quickEditor.flex().relative(this).x(1F, -200).wTo(this.flex(), 1F).h(1F);
        this.quickEditor.setVisible(false);

        /* Create morph panels */
        this.morphs = new GuiScrollElement(mc);
        this.morphs.flex().relative(this).wh(1F, 1F).column(0).vertical().stretch().scroll();
        this.reload();

        /* Initiate bottom bar */
        this.bar = new GuiElement(mc);
        this.search = new GuiTextElement(mc, this::setFilter);
        this.edit = new GuiButtonElement(mc, IKey.lang("metamorph.gui.edit"),  (b) -> this.enterEditMorph());
        this.edit.flex().w(60);

        this.bar.flex().relative(this.morphs).set(10, 0, 0, 20).y(1, -30).w(1, -20).row(5).preferred(0).height(20);
        this.bar.add(this.search, this.edit);

        this.screen.add(this.morphs, this.bar, this.quickEditor);
        this.add(this.screen, new GuiDrawable(this::drawOverlay), this.editor);

        /* Morph editor keybinds */
        this.exitKey = this.keys().register(IKey.lang("metamorph.gui.creative.exit"), Keyboard.KEY_ESCAPE, this::exit);

        this.updateExitKey();

        this.morphs.keys().register(IKey.lang("metamorph.gui.creative.edit"), Keyboard.KEY_E, this::enterEditMorph);
        this.morphs.keys().register(IKey.lang("metamorph.gui.creative.quick"), Keyboard.KEY_Q, this::toggleQuickEdit);
    }

    public void reload()
    {
        MorphList list = MorphManager.INSTANCE.list;

        list.update(this.mc.world);
        MinecraftForge.EVENT_BUS.post(new ReloadMorphs());

        this.sections.clear();
        this.morphs.removeAll();

        for (MorphSection section : list.sections)
        {
            GuiMorphSection element = section.getGUI(this.mc, this, this::pickMorph);

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

    public void exit()
    {
        if (this.isEditMode())
        {
            this.exitEditMorph(this.nestedEdits.isEmpty(), false);
        }
        else
        {
            this.restoreEdit();
        }

        GuiBase.getCurrent().setContextMenu(null);
    }

    protected void updateExitKey()
    {
        this.exitKey.active = this.editor.delegate != null || !this.nestedEdits.isEmpty();
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

    /* Quick mode */

    public void toggleQuickEdit()
    {
        if (this.isEditMode() || !this.quickEditor.isVisible() && this.getSelected() == null)
        {
            return;
        }

        this.quickEditor.toggleVisible();

        if (this.quickEditor.isVisible())
        {
            AbstractMorph morph = this.getSelected();

            if (!this.isSelectedMorphIsEditable())
            {
                morph = this.copyToRecent(morph);
            }

            this.quickEditor.setMorph(morph, this.getMorphEditor(morph));
            this.morphs.flex().wTo(this.quickEditor.flex());
        }
        else
        {
            this.morphs.flex().w(1F);
        }

        this.resize();
    }

    /* Nested editing */

    public boolean isNested()
    {
        return !this.nestedEdits.isEmpty();
    }

    public void nestEdit(AbstractMorph selected, boolean editing, Consumer<AbstractMorph> callback)
    {
        NestedEdit edit = new NestedEdit(this.filter, this.editor.delegate.morph, this.callback, this.selected, editing);
        this.callback = callback;

        if (editing)
        {
            this.enterEditMorph(selected);
        }
        else
        {
            this.exitEditMorph(false, true);
            this.setFilter("");
            this.setSelected(selected);
        }

        this.nestedEdits.add(edit);
        this.updateExitKey();
    }

    public void restoreEdit()
    {
        if (this.nestedEdits.isEmpty())
        {
            return;
        }

        NestedEdit edit = this.nestedEdits.pop();

        if (!edit.editing)
        {
            this.pickMorph(this.getSelected());
        }

        if (this.selected != null)
        {
            this.selected.reset();
        }

        this.setFilter(this.filter);
        this.callback = edit.callback;
        this.selected = edit.selected;
        this.selected.morph = edit.selectedMorph;
        this.selected.category = edit.selectedCategory;
        this.scrollTo = true;

        this.enterEditMorph(edit.editMorph);
    }

    /* Edit mode */

    public boolean isEditMode()
    {
        return this.editor.delegate != null;
    }

    public void enterEditMorph()
    {
        AbstractMorph morph = this.getSelected();

        if (morph == null)
        {
            return;
        }

        if (!this.isSelectedMorphIsEditable() || !this.nestedEdits.isEmpty())
        {
            morph = morph.copy();
            this.pickMorph(morph);
        }

        this.enterEditMorph(morph);
    }

    public void enterEditMorph(AbstractMorph morph)
    {
        if (morph == null)
        {
            return;
        }

        this.disableDirty();

        GuiAbstractMorph editor = this.getMorphEditor(morph);

        if (editor != null)
        {
            this.setEditor(editor);
        }
    }

    public void exitEditMorph(boolean add, boolean ignore)
    {
        if (!this.isEditMode())
        {
            return;
        }

        AbstractMorph edited = this.editor.delegate.morph;

        if (!this.nestedEdits.isEmpty() && !ignore)
        {
            this.pickMorph(edited);
            this.restoreEdit();

            return;
        }

        this.editor.delegate.finishEdit();
        this.syncSelected();

        if (add && edited != null && !this.isSelectedMorphIsEditable())
        {
            this.setSelected(edited);
        }

        this.setEditor(null);
    }

    protected void setEditor(GuiAbstractMorph editor)
    {
        this.editor.setDelegate(editor);
        this.screen.setVisible(editor == null);
        this.updateExitKey();
    }

    public void finish()
    {
        this.disableDirty();

        if (this.isEditMode())
        {
            this.editor.delegate.finishEdit();
        }

        this.syncSelected();
        this.pickMorph(MorphUtils.copy(this.getSelected()));
    }

    public boolean isSelectedMorphIsEditable()
    {
        return this.selected != null && this.selected.category != null && this.selected.category.isEditable(this.getSelected());
    }

    public void syncSelected()
    {
        AbstractMorph morph = this.getSelected();

        if (morph != null && this.selected != null && this.selected.category != null)
        {
            this.selected.category.edit(morph);
        }
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
                editor.setMorphs(this);
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

    public void pickMorph(GuiMorphSection selected)
    {
        this.disableDirty();

        if (this.selected != null && selected != this.selected)
        {
            this.selected.reset();
        }

        this.selected = selected;
        this.pickMorph(selected.morph);
        this.syncQuickEditor();
    }

    public void pickMorph(AbstractMorph morph)
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
        if (filter.equals(this.filter))
        {
            return;
        }

        String lcfilter = filter.toLowerCase().trim();

        for (GuiMorphSection section : this.sections)
        {
            section.setFilter(lcfilter);
        }

        this.filter = lcfilter;
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
            AbstractMorph found = null;
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
                this.copyToRecent(morph);
            }
            else
            {
                this.selected = selectedSection;
                this.scrollTo = true;

                selectedSection.morph = found;
                selectedSection.category = selectedCategory;
                this.pickMorph(found);
            }
        }
        else
        {
            this.selected = null;
        }

        this.syncQuickEditor();

        return this.getSelected();
    }

    protected AbstractMorph copyToRecent(AbstractMorph morph)
    {
        if (this.selected != null)
        {
            this.selected.reset();
        }

        morph = morph.copy();

        this.user.recent.add(morph);
        this.selected = this.userSection;
        this.selected.morph = morph;
        this.selected.category = this.user.recent;
        this.pickMorph(morph);

        this.scrollTo = true;

        return morph;
    }

    protected void syncQuickEditor()
    {
        if (this.quickEditor.isVisible())
        {
            AbstractMorph morph = this.getSelected();

            if (morph != null && this.isSelectedMorphIsEditable())
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

        if (this.scrollTo && !this.isEditMode())
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
                Gui.drawRect(area.x, area.y - 26, area.x + w + 8, area.y, 0xdd000000);
                this.font.drawStringWithShadow(selected, area.x + 4, area.y - 22, 0xffffffff);
                this.font.drawStringWithShadow(morph.name, area.x + 4, area.y - 11, 0x888888);
            }
            else
            {
                Gui.drawRect(area.x, area.y - 16, area.x + w + 8, area.y, 0xdd000000);
                this.font.drawStringWithShadow(selected, area.x + 4, area.y - 11, 0xffffffff);
            }
        }

        if (!this.isEditMode() && !this.search.field.isFocused() && this.search.field.getText().isEmpty())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.search"), this.search.area.x + 5, this.search.area.y + 6, 0x888888);
        }
    }

    /**
     * Data stored about currently nested editing
     */
    public static class NestedEdit
    {
        public String filter;
        public Consumer<AbstractMorph> callback;

        public GuiMorphSection selected;
        public MorphCategory selectedCategory;
        public AbstractMorph selectedMorph;
        public AbstractMorph editMorph;
        public boolean editing;

        public NestedEdit(String filter, AbstractMorph editMorph, Consumer<AbstractMorph> callback, GuiMorphSection selected, boolean editing)
        {
            this.filter = filter;
            this.editMorph = editMorph;
            this.callback = callback;
            this.editing = editing;

            this.selected = selected;
            this.selectedCategory = selected.category;
            this.selectedMorph = selected.morph;
        }
    }
}
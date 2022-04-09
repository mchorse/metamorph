package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Keybind;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.Timer;
import mchorse.mclib.utils.shaders.Shader;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL20;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Scroll list of available morphs
 * 
 * More morphs than presented in this menu are available, but the problem that 
 * it's impossible to list all variation of those morphs. iChun probably knows 
 * it, that's why he doesn't bother with a GUI of all available morphs.
 */
public class GuiCreativeMorphsList extends GuiElement
{
    public static Shader shader;
    public static int skinColor = -1;

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
    public GuiCreativeMorphs morphs;

    public List<OnionSkin> onionSkins = new ArrayList<OnionSkin>();
    public List<OnionSkin> lastOnionSkins;

    private Timer timer = new Timer(100);
    private Stack<NestedEdit> nestedEdits = new Stack<NestedEdit>();

    protected Keybind exitKey;

    protected boolean keepViewport;

    protected Vector3f lastPos = new Vector3f();
    protected float lastYaw;
    protected float lastPitch;
    protected float lastScale;

    protected boolean doRenderOnionSkin;

    private DummyEntity entity;

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiCreativeMorphsList(Minecraft mc, Consumer<AbstractMorph> callback)
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
        this.morphs = new GuiCreativeMorphs(mc, this);
        this.morphs.flex().relative(this).wh(1F, 1F).column(0).vertical().stretch().scroll();

        /* Initiate bottom bar */
        this.bar = new GuiElement(mc);
        this.search = new GuiTextElement(mc, this.morphs::setFilter);
        this.edit = new GuiButtonElement(mc, IKey.lang("metamorph.gui.edit"),  (b) -> this.enterEditMorph());
        this.edit.setEnabled(false);
        this.edit.flex().w(60);

        this.bar.flex().relative(this.morphs).set(10, 0, 0, 20).y(1, -30).w(1, -20).row(5).preferred(0).height(20);
        this.bar.add(this.search, this.edit);

        this.screen.add(this.morphs, this.bar, this.quickEditor);
        this.add(this.screen, new GuiDrawable(this::drawOverlay), this.editor);

        /* Onion skin */
        this.doRenderOnionSkin = true;
        this.entity = new DummyEntity(mc.world);

        if (shader == null)
        {
            try
            {
                String vert = IOUtils.toString(this.getClass().getResourceAsStream("/assets/metamorph/shaders/onionskin.vert"), StandardCharsets.UTF_8);
                String frag = IOUtils.toString(this.getClass().getResourceAsStream("/assets/metamorph/shaders/onionskin.frag"), StandardCharsets.UTF_8);

                shader = new Shader();
                shader.compile(vert, frag, true);

                GL20.glUniform1i(GL20.glGetUniformLocation(shader.programId, "texture"), 0);

                skinColor = GL20.glGetUniformLocation(shader.programId, "onionskin");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /* Morph editor keybinds */
        IKey category = IKey.lang("metamorph.gui.creative.keys.category");

        this.exitKey = this.keys().register(IKey.lang("metamorph.gui.creative.keys.exit"), Keyboard.KEY_ESCAPE, this::exit).category(category).active(this::updateExitKey);

        this.reload();

        this.morphs.keys().register(IKey.lang("metamorph.gui.creative.keys.edit"), Keyboard.KEY_E, this::enterEditMorph).category(category);
        this.morphs.keys().register(IKey.lang("metamorph.gui.creative.keys.quick"), Keyboard.KEY_Q, this::toggleQuickEdit).category(category);
        this.morphs.keys().register(IKey.lang("metamorph.gui.creative.keys.focus"), Keyboard.KEY_F, () -> GuiBase.getCurrent().focus(this.search, true)).held(Keyboard.KEY_LCONTROL).category(category);

        this.keys().register(IKey.lang("metamorph.gui.creative.keys.onionskin"), Keyboard.KEY_Q, () -> this.doRenderOnionSkin = !this.doRenderOnionSkin).active(() -> this.isEditMode() && this.haveOnionSkin()).category(category);
    }

    public void reload()
    {
        this.morphs.setupSections(this, this::pickMorph);
        this.search.setText("");
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

    protected boolean updateExitKey()
    {
        return this.editor.delegate != null || !this.nestedEdits.isEmpty();
    }

    public Runnable showGlobalMorphs(AbstractMorph morph)
    {
        if (this.morphs.user.global.isEmpty() || morph == null)
        {
            return null;
        }
        
        return () -> 
        {
            GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(this.mc);

            for (UserCategory category : this.morphs.user.global) 
            {
                contextMenu.action(IKey.str(category.getTitle()), () ->
                {
                    AbstractMorph added = morph.copy();

                    category.add(added);
                    this.setSelected(added);
                });
            }

            GuiBase.getCurrent().replaceContextMenu(contextMenu);
        };
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
            this.morphs.syncSelected();
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
                morph = this.morphs.copyToRecent(morph);
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
        this.nestEdit(selected, editing, false, callback);
    }

    public void nestEdit(AbstractMorph selected, boolean editing, boolean keepViewport, Consumer<AbstractMorph> callback)
    {
        NestedEdit edit = new NestedEdit(this.morphs.filter, this.editor.delegate.morph, this.editor.delegate.toNBT(), this.callback, this.morphs.selected, editing, this.keepViewport, this.lastOnionSkins);
        this.callback = callback;
        this.keepViewport = keepViewport;

        if (keepViewport)
        {
            this.saveViewport();

            this.lastOnionSkins = this.lastOnionSkins == null ? new ArrayList<OnionSkin>() : new ArrayList<OnionSkin>(this.lastOnionSkins);
            this.lastOnionSkins.addAll(this.onionSkins);
        }
        else
        {
            this.lastOnionSkins = null;
        }

        this.nestedEdits.add(edit);
        this.updateExitKey();

        if (editing)
        {
            this.enterEditMorph(selected);
        }
        else
        {
            this.exitEditMorph(false, true);
            this.morphs.setFilter("");
            this.setSelected(selected);
        }
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
        this.morphs.setFilter("");
        this.morphs.setSelectedDirect(edit.selected, edit.selectedMorph, edit.selectedCategory);
        this.callback = edit.callback;
        this.morphs.scrollTo();

        this.enterEditMorph(edit.editMorph);
        this.editor.delegate.fromNBT(edit.data);

        if (this.keepViewport)
        {
            this.loadViewport();
        }

        this.keepViewport = edit.keepViewport;
        this.lastOnionSkins = edit.lastOnionSkins;
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

        this.onionSkins.clear();

        GuiAbstractMorph editor = this.getMorphEditor(morph);

        if (editor != null)
        {
            this.setEditor(editor);

            if (this.keepViewport)
            {
                this.loadViewport();
            }

            editor.renderer.afterRender = this::renderOnionSkin;
        }
    }

    public void exitEditMorph(boolean add, boolean ignore)
    {
        if (!this.isEditMode())
        {
            return;
        }

        this.editor.delegate.renderer.afterRender = null;

        if (this.keepViewport)
        {
            this.saveViewport();
        }

        AbstractMorph edited = this.editor.delegate.morph;

        if (!this.nestedEdits.isEmpty() && !ignore)
        {
            this.pickMorph(edited);
            this.restoreEdit();

            return;
        }

        this.editor.delegate.finishEdit();
        this.morphs.syncSelected();

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
        int i = 0;

        while (this.isNested() || this.isEditMode())
        {
            this.exit();

            i ++;
        }

        if (i > 0)
        {
            this.pickMorph(MorphUtils.copy(this.getSelected()));
        }

        this.keepViewport = false;
        this.lastOnionSkins = null;
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

    private void saveViewport()
    {
        GuiModelRenderer renderer = this.editor.delegate.renderer;

        this.lastPos.set(renderer.pos);
        this.lastPitch = renderer.pitch;
        this.lastYaw = renderer.yaw;
        this.lastScale = renderer.scale;
    }

    private void loadViewport()
    {
        GuiModelRenderer renderer = this.editor.delegate.renderer;

        renderer.setPosition(this.lastPos.x, this.lastPos.y, this.lastPos.z);
        renderer.setRotation(this.lastYaw, this.lastPitch);
        renderer.setScale(this.lastScale);
    }

    /* Onion skin */

    public boolean haveOnionSkin()
    {
        return !this.onionSkins.isEmpty() || this.lastOnionSkins != null && !this.lastOnionSkins.isEmpty();
    }

    private void renderOnionSkin(GuiContext context)
    {
        if (!this.doRenderOnionSkin)
        {
            return;
        }

        this.entity.ticksExisted = this.editor.delegate.renderer.getEntity().ticksExisted;

        shader.bind();
        GuiModelRenderer.disableRenderingFlag();

        if (this.lastOnionSkins != null)
        {
            for (OnionSkin skin : this.lastOnionSkins)
            {
                renderSingleOnionSkin(skin, context.partialTicks);
            }
        }

        for (OnionSkin skin : this.onionSkins)
        {
            renderSingleOnionSkin(skin, context.partialTicks);
        }

        shader.unbind();
    }

    private void renderSingleOnionSkin(OnionSkin skin, float partialTicks)
    {
        if (skin.morph == null)
        {
            return;
        }

        GL20.glUniform4f(skinColor, skin.color.r, skin.color.g, skin.color.b, skin.color.a);

        entity.prevRotationPitch = entity.rotationPitch = skin.pitch;
        entity.prevRenderYawOffset = entity.renderYawOffset = skin.yawBody;
        entity.prevRotationYawHead = entity.rotationYawHead = skin.yawHead;

        GlStateManager.pushMatrix();
        MorphUtils.render(skin.morph, entity, skin.offset.x, skin.offset.y, skin.offset.z, 0, partialTicks);
        GlStateManager.popMatrix();
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

        return this.morphs.getSelected();
    }

    public void pickMorph(GuiMorphSection selected)
    {
        this.disableDirty();

        this.morphs.setSelectedDirect(selected);

        this.pickMorph(selected.morph);
        this.syncQuickEditor();
    }

    public void pickMorph(AbstractMorph morph)
    {
        this.edit.setEnabled(morph != null);

        if (this.callback != null)
        {
            this.callback.accept(morph);
        }
    }

    /**
     * Set selected morph 
     */
    public AbstractMorph setSelected(AbstractMorph morph)
    {
        this.disableDirty();
        this.morphs.setSelected(morph);
        this.syncQuickEditor();

        morph = this.getSelected();

        this.edit.setEnabled(morph != null);

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
    
    protected boolean isSelectedMorphIsEditable()
    {
        return this.morphs.isSelectedMorphIsEditable();
    }

    /* Element overrides */

    @Override
    public void draw(GuiContext context)
    {
        if (this.timer.checkReset())
        {
            this.morphs.syncSelected();
        }

        super.draw(context);
    }

    private void drawOverlay(GuiContext context)
    {
        /* Draw the name of the morph */
        if (!this.isEditMode())
        {
            AbstractMorph morph = this.getSelected();
            String selected = morph != null ? morph.getDisplayName() : I18n.format("metamorph.gui.no_morph");
            boolean error = morph != null && morph.errorRendering;

            if (error)
            {
                selected = I18n.format("metamorph.gui.morph_render_error");
            }

            Area area = this.search.area;
            int w = Math.max(this.font.getStringWidth(selected), morph != null ? this.font.getStringWidth(morph.name) : 0);

            if (morph != null && !morph.errorRendering)
            {
                Gui.drawRect(area.x, area.y - 27, area.x + w + 8, area.y, 0xdd000000);
                this.font.drawStringWithShadow(selected, area.x + 4, area.y - 23, 0xffffffff);
                this.font.drawStringWithShadow(morph.name, area.x + 4, area.y - 12, 0x888888);
            }
            else
            {
                Gui.drawRect(area.x, area.y - 16, area.x + w + 8, area.y, 0xdd000000);
                this.font.drawStringWithShadow(selected, area.x + 4, area.y - 12, error ? 0xff1833 : 0xffffff);
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
        public NBTTagCompound data;
        public Consumer<AbstractMorph> callback;

        public GuiMorphSection selected;
        public MorphCategory selectedCategory;
        public AbstractMorph selectedMorph;
        public AbstractMorph editMorph;
        public boolean editing;
        public boolean keepViewport;
        public List<OnionSkin> lastOnionSkins;

        public NestedEdit(String filter, AbstractMorph editMorph, NBTTagCompound data, Consumer<AbstractMorph> callback, GuiMorphSection selected, boolean editing, boolean keepViewport, List<OnionSkin> lastOnionSkins)
        {
            this.filter = filter;
            this.data = data;
            this.editMorph = editMorph;
            this.callback = callback;
            this.editing = editing;
            this.keepViewport = keepViewport;
            this.lastOnionSkins = lastOnionSkins;

            this.selected = selected;
            this.selectedCategory = selected == null ? null : selected.category;
            this.selectedMorph = selected == null ? null : selected.morph;
        }
    }

    /**
     * Onion skin data
     */
    public static class OnionSkin
    {
        public Color color = new Color();

        public AbstractMorph morph;

        public Vector3d offset = new Vector3d(0, 0, 0);

        public float pitch = 0f;

        public float yawHead = 0f;

        public float yawBody = 0f;

        public OnionSkin color(float r, float g, float b, float a)
        {
            this.color.set(r, g, b, a);
            return this;
        }

        public OnionSkin morph(AbstractMorph morph)
        {
            this.morph = morph;
            return this;
        }

        public OnionSkin offset(double x, double y, double z, float pitch, float yawHead, float yawBody)
        {
            this.offset.set(x, y, z);
            this.pitch = pitch;
            this.yawHead = yawHead;
            this.yawBody = yawBody;
            return this;
        }
    }
}
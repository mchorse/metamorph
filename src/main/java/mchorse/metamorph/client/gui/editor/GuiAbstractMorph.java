package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsList;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
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
    public static final IKey KEY_CATEGORY = IKey.lang("metamorph.gui.editor.keys.category");

    public GuiCreativeMorphsList morphs;

    public GuiIconElement finish;
    public GuiModelRenderer renderer;
    public GuiSettingsPanel settings;

    protected GuiMorphPanel defaultPanel;

    public T morph;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.finish = new GuiIconElement(mc, Icons.CLOSE, (b) -> this.morphs.exit());
        this.finish.flex().relative(this).set(0, 0, 20, 20).y(1F, -20);
        this.renderer = this.createMorphRenderer(mc);
        this.renderer.flex().relative(this).wh(1F, 1F);
        this.defaultPanel = this.settings = new GuiSettingsPanel(mc, this);

        this.registerPanel(this.settings, IKey.lang("metamorph.gui.editor.settings"), MMIcons.PROPERTIES);
        this.prepend(this.renderer);

        this.add(this.finish);

        this.keys().register(IKey.lang("metamorph.gui.editor.keys.cycle"), Keyboard.KEY_TAB, this::cycle).category(KEY_CATEGORY);
    }

    protected void cycle()
    {
        int index = -1;

        for (int i = 0; i < this.panels.size(); i ++)
        {
            if (this.view.delegate == this.panels.get(i))
            {
                index = i;

                break;
            }
        }

        index += GuiScreen.isShiftKeyDown() ? 1 : -1;
        index = MathUtils.cycler(index, 0, this.panels.size() - 1);

        this.buttons.elements.get(index).clickItself(GuiBase.getCurrent());
    }

    protected GuiModelRenderer createMorphRenderer(Minecraft mc)
    {
        return new GuiMorphRenderer(mc);
    }

    public void setMorphs(GuiCreativeMorphsList morphs)
    {
        this.morphs = morphs;
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
        this.setupRenderer(morph);

        for (GuiMorphPanel panel : this.panels)
        {
            panel.fillData(morph);
        }

        this.setPanel(this.defaultPanel);
    }

    protected void setupRenderer(T morph)
    {
        this.renderer.reset();

        if (this.renderer instanceof GuiMorphRenderer)
        {
            ((GuiMorphRenderer) this.renderer).morph = morph;
        }
    }

    public void finishEdit()
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.finishEditing();
        }
    }

    /**
     * Get presets
     */
    public List<Label<NBTTagCompound>> getPresets(T morph)
    {
        return Collections.emptyList();
    }

    protected void addPreset(AbstractMorph morph, List<Label<NBTTagCompound>> list, String label, String json)
    {
        try
        {
            this.addPreset(morph, list, label, JsonToNBT.getTagFromJson(json));
        }
        catch (Exception e)
        {}
    }

    protected void addPreset(AbstractMorph morph, List<Label<NBTTagCompound>> list, String label, NBTTagCompound tag)
    {
        NBTTagCompound morphTag = morph.toNBT();

        morphTag.merge(tag);
        list.add(new Label<NBTTagCompound>(IKey.str(label), morphTag));
    }

    /**
     * Get quick access editing fields
     */
    public List<GuiElement> getFields(Minecraft mc, GuiCreativeMorphsList morphs, T morph)
    {
        List<GuiElement> elements = new ArrayList<GuiElement>();
        GuiTextElement displayName = new GuiTextElement(mc, (name) ->
        {
            morphs.getSelected().displayName = name;
            morphs.markDirty();
        });

        displayName.setText(morph.displayName);
        elements.add(Elements.label(IKey.lang("metamorph.gui.editor.display_name"), this.font.FONT_HEIGHT));
        elements.add(displayName);

        return elements;
    }

    @Override
    public GuiIconElement registerPanel(GuiMorphPanel panel, IKey tooltip, Icon icon)
    {
        GuiIconElement button = super.registerPanel(panel, tooltip, icon);

        if (panel instanceof GuiBodyPartEditor)
        {
            this.registerKeybind(button, IKey.lang("metamorph.gui.body_parts.open"), Keyboard.KEY_B).category(KEY_CATEGORY);
        }

        return button;
    }

    @Override
    protected void drawBackground(GuiContext context, int x, int y, int w, int h)
    {
        Gui.drawRect(x, y, x + w, y + h, 0xee000000);
    }

    /* Saving and restoring the store */

    public void fromNBT(NBTTagCompound tag)
    {
        /* Restore model renderer's position */
        this.renderer.setPosition(tag.getFloat("MX"), tag.getFloat("MY"), tag.getFloat("MZ"));
        this.renderer.setScale(tag.getFloat("MS"));
        this.renderer.setRotation(tag.getFloat("MRY"), tag.getFloat("MRX"));

        /* Restore panel */
        int hash = tag.getInteger("PanelHash");

        for (GuiMorphPanel panel : this.panels)
        {
            if (panel.hashCode() == hash)
            {
                this.setPanel(panel);
                panel.fromNBT(tag.getCompoundTag("Panel"));

                return;
            }
        }
    }

	public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        /* Save model renderer's position */
        tag.setFloat("MX", this.renderer.pos.x);
        tag.setFloat("MY", this.renderer.pos.y);
        tag.setFloat("MZ", this.renderer.pos.z);
        tag.setFloat("MS", this.renderer.scale);
        tag.setFloat("MRX", this.renderer.pitch);
        tag.setFloat("MRY", this.renderer.yaw);

        /* Save panel */
        tag.setTag("Panel", this.view.delegate.toNBT());
        tag.setInteger("PanelHash", this.view.delegate.hashCode());

        return tag;
	}
}
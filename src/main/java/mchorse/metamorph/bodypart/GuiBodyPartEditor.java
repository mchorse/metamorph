package mchorse.metamorph.bodypart;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
    public static List<BodyPart> buffer = new ArrayList<BodyPart>();

    protected GuiBodyPartListElement bodyParts;
    protected GuiNestedEdit pickMorph;
    protected GuiToggleElement useTarget;
    protected GuiToggleElement enabled;
    protected GuiToggleElement animate;

    protected GuiIconElement add;
    protected GuiIconElement dupe;
    protected GuiIconElement remove;
    protected GuiIconElement copy;
    protected GuiIconElement paste;

    protected GuiBodyPartTransformations transformations;

    protected GuiStringListElement limbs;
    protected GuiElement elements;
    protected GuiElement bottomEditor;

    protected BodyPartManager parts;
    protected BodyPart part;

    protected GuiElement stacks;
    protected GuiSlotElement[] slots = new GuiSlotElement[6];

    public GuiBodyPartEditor(Minecraft mc, GuiAbstractMorph editor)
    {
        super(mc, editor);

        this.limbs = new GuiStringListElement(mc, (str) -> this.pickLimb(str.get(0)));
        this.limbs.background();

        this.bodyParts = new GuiBodyPartListElement(mc, (part) -> this.setPart(part.isEmpty() ? null : part.get(0)));
        this.bodyParts.background().sorting();
        this.bodyParts.context(this::bodyPartContextMenu);

        this.pickMorph = new GuiNestedEdit(mc, (editing) ->
        {
            BodyPart part = this.part;

            this.editor.morphs.nestEdit(part.morph.get(), editing, (morph) ->
            {
                if (part != null)
                {
                    AbstractMorph copy = MorphUtils.copy(morph);

                    part.morph.setDirect(copy);
                    this.applyUseTarget(part, copy);
                }
            });
        });

        this.add = new GuiIconElement(mc, Icons.ADD, this::addPart);
        this.add.tooltip(IKey.lang("metamorph.gui.body_parts.add_tooltip"));
        this.add.flex().w(20);
        this.dupe = new GuiIconElement(mc, Icons.DUPE, this::dupePart);
        this.dupe.tooltip(IKey.lang("metamorph.gui.body_parts.dupe_tooltip"));
        this.remove = new GuiIconElement(mc, Icons.REMOVE, this::removePart);
        this.remove.tooltip(IKey.lang("metamorph.gui.body_parts.remove_tooltip"));
        this.copy = new GuiIconElement(mc, Icons.COPY, this::copyParts);
        this.copy.tooltip(IKey.lang("metamorph.gui.body_parts.copy_tooltip"));
        this.paste = new GuiIconElement(mc, Icons.PASTE, this::pasteParts);
        this.paste.tooltip(IKey.lang("metamorph.gui.body_parts.paste_tooltip"));
        this.paste.flex().w(20);

        this.useTarget = new GuiToggleElement(mc, IKey.lang("metamorph.gui.body_parts.use_target"), false, this::toggleTarget);
        this.enabled = new GuiToggleElement(mc, IKey.lang("metamorph.gui.body_parts.enabled"), false, this::toggleEnabled);
        this.animate = new GuiToggleElement(mc, IKey.lang("metamorph.gui.body_parts.animate"), false, this::toggleAnimate);
        this.animate.tooltip(IKey.lang("metamorph.gui.body_parts.animate_tooltip"), Direction.LEFT);
        this.transformations = new GuiBodyPartTransformations(mc);

        int width = 110;

        GuiElement sidebar = new GuiElement(mc);

        sidebar.flex().relative(this).x(10).y(1, -30).wh(width, 20).row(0).height(20);
        sidebar.add(this.add, this.dupe, this.remove, this.copy, this.paste);

        this.bottomEditor = new GuiElement(mc);
        this.bottomEditor.flex().relative(this).x(1, -115).y(1, -10).w(width).anchorY(1F);
        this.bottomEditor.flex().column(5).vertical().stretch();
        this.bottomEditor.add(this.enabled, this.animate, this.useTarget);

        this.transformations.flex().relative(this.area).x(0.5F, -128).y(1, -10).wh(256, 70).anchorY(1F);
        this.limbs.flex().relative(this).set(0, 50, width, 90).x(1, -115).hTo(this.bottomEditor.area, -5);
        this.pickMorph.flex().relative(this).set(0, 10, width, 20).x(1, -115);
        this.bodyParts.flex().relative(this).set(10, 22, width, 0).hTo(this.transformations.flex(), 1F, -20);

        this.elements = new GuiElement(mc).noCulling();
        this.elements.add(this.bottomEditor, this.limbs, this.pickMorph, this.transformations);
        this.add(sidebar, this.bodyParts, this.elements);

        /* Inventory */
        this.stacks = new GuiElement(mc);
        this.stacks.flex().relative(this).x(0.5F).y(10).anchor(0.5F, 0).row(5).resize();

        for (int i = 0; i < this.slots.length; i++)
        {
            int slot = i;

            this.slots[i] = new GuiSlotElement(mc, i, (stack) -> this.pickItem(stack, slot));
            this.stacks.add(this.slots[i]);
        }

        this.elements.add(this.stacks);

        this.bodyParts.keys().register(IKey.lang("metamorph.gui.body_parts.keys.select_prev"), Keyboard.KEY_W, () -> this.moveIndex(-1)).category(GuiAbstractMorph.KEY_CATEGORY);
        this.bodyParts.keys().register(IKey.lang("metamorph.gui.body_parts.keys.select_next"), Keyboard.KEY_S, () -> this.moveIndex(1)).category(GuiAbstractMorph.KEY_CATEGORY);
    }

    private void applyUseTarget(BodyPart part, AbstractMorph copy)
    {
        if (copy == null)
        {
            return;
        }

        if (copy.name.equals("snowstorm") || copy.name.equals("particle") || copy.name.equals("tracker"))
        {
            part.useTarget = true;
            this.useTarget.toggled(part.useTarget);
        }
    }

    private GuiContextMenu bodyPartContextMenu()
    {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
        String text = GuiScreen.getClipboardString();
        BodyPart part = null;

        try
        {
            NBTTagCompound tag = JsonToNBT.getTagFromJson(text);

            part = new BodyPart();
            part.fromNBT(tag);
        }
        catch (Exception e)
        {}

        if (!this.bodyParts.isDeselected())
        {
            menu.action(Icons.COPY, IKey.lang("metamorph.gui.body_parts.context.copy"), () ->
            {
                NBTTagCompound tag = new NBTTagCompound();

                this.bodyParts.getCurrentFirst().toNBT(tag);
                GuiScreen.setClipboardString(tag.toString());
            });
        }

        if (part != null)
        {
            final BodyPart destination = part;

            menu.action(Icons.PASTE, IKey.lang("metamorph.gui.body_parts.context.paste"), () -> this.addPart(destination.copy()));
        }

        return menu.actions.getList().isEmpty() ? null : menu;
    }

    protected void addPart(GuiIconElement b)
    {
        BodyPart part = new BodyPart();

        this.setupNewBodyPart(part);
        this.addPart(part);
    }

    protected void addPart(BodyPart part)
    {
        part.init();

        this.parts.parts.add(part);
        this.setPart(part);

        this.bodyParts.setCurrentDirect(part);
        this.bodyParts.update();
    }

    protected void setupNewBodyPart(BodyPart part)
    {}

    protected void dupePart(GuiIconElement b)
    {
        if (this.bodyParts.isDeselected())
        {
            return;
        }

        BodyPart part = this.bodyParts.getCurrentFirst().copy();

        part.init();

        this.parts.parts.add(part);
        this.setPart(part);

        this.bodyParts.setCurrentDirect(part);
        this.bodyParts.update();
    }

    protected void removePart(GuiIconElement b)
    {
        if (this.bodyParts.isDeselected())
        {
            return;
        }

        List<BodyPart> parts = this.parts.parts;
        int index = -1;

        for (int i = 0; i < parts.size(); i ++)
        {
            if (parts.get(i) == this.part)
            {
                index = i;

                break;
            }
        }

        if (index != -1)
        {
            parts.remove(this.part);
            this.bodyParts.update();
            index--;

            if (parts.size() >= 1)
            {
                this.setPart(parts.get(MathUtils.clamp(index, 0, parts.size() - 1)));
            }
            else
            {
                this.setPart(null);
            }
        }

        this.bodyParts.update();
    }

    protected void copyParts(GuiIconElement b)
    {
        buffer.clear();

        for (BodyPart part : this.parts.parts)
        {
            buffer.add(part.copy());
        }
    }

    protected void pasteParts(GuiIconElement b)
    {
        for (BodyPart part : buffer)
        {
            BodyPart clone = part.copy();

            this.parts.parts.add(clone);
            clone.init();
        }

        if (!this.parts.parts.isEmpty())
        {
            this.setPart(this.parts.parts.get(this.parts.parts.size() - 1));
        }

        this.bodyParts.update();
    }

    protected void toggleTarget(GuiToggleElement b)
    {
        if (this.part != null)
        {
            this.part.useTarget = b.isToggled();
        }
    }

    protected void toggleEnabled(GuiToggleElement b)
    {
        if (this.part != null)
        {
            this.part.enabled = b.isToggled();
        }
    }

    protected void toggleAnimate(GuiToggleElement b)
    {
        if (this.part != null)
        {
            this.part.animate = b.isToggled();
        }
    }

    protected void pickItem(ItemStack stack, int slot)
    {
        if (this.part == null)
        {
            return;
        }

        this.part.slots[slot] = stack;
        this.part.updateEntity();
    }

    @Override
    public void fillData(AbstractMorph morph)
    {
        super.fillData(morph);

        if (morph instanceof IBodyPartProvider)
        {
            BodyPartManager manager = ((IBodyPartProvider) morph).getBodyPart();

            this.parts = manager;

            this.bodyParts.setList(manager.parts);
            this.bodyParts.update();
        }
    }

    @Override
    public void startEditing()
    {
        super.startEditing();

        if (this.parts != null)
        {
            this.setPart(this.parts.parts.isEmpty() ? null : this.parts.parts.get(0));
        }
    }

    public void setLimbs(Collection<String> limbs)
    {
        this.limbs.clear();
        this.limbs.add(limbs);
        this.limbs.sort();
    }

    protected void setPart(BodyPart part)
    {
        this.part = part;
        this.elements.setVisible(part != null);

        if (this.part != null)
        {
            this.fillBodyPart(part);
            this.limbs.setCurrent(part.limb);
            this.bodyParts.setCurrentDirect(part);
            this.pickMorph.setMorph(part.morph.get());
        }
    }

    protected void pickLimb(String str)
    {
    	this.part.setLimb(this.morph, str, GuiScreen.isAltKeyDown(), this.editor.renderer.getEntity(), GuiBase.getCurrent().partialTicks);
    	this.fillBodyPart(this.part);
    }

    public void fillBodyPart(BodyPart part)
    {
        if (part != null)
        {
            this.bottomEditor.removeAll();

            if (this.morph instanceof IAnimationProvider)
            {
                this.bottomEditor.add(this.enabled, this.animate, this.useTarget);
            }
            else
            {
                this.bottomEditor.add(this.enabled, this.useTarget);
            }

            this.elements.resize();
            this.transformations.setBodyPart(part);

            this.enabled.toggled(part.enabled);
            this.useTarget.toggled(part.useTarget);
            this.animate.toggled(part.animate);

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].setStack(part.slots[i]);
            }
        }
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        this.moveIndex(this.getMoveIndex(context.keyCode));

        return false;
    }

    private void moveIndex(int index)
    {
        if (index != 0)
        {
            index = MathUtils.cycler(this.bodyParts.getIndex() + index, 0, this.bodyParts.getList().size() - 1);

            this.bodyParts.setIndex(index);
            this.fillBodyPart(this.bodyParts.getCurrentFirst());
        }
    }

    private int getMoveIndex(int keyCode)
    {
        if (keyCode == Keyboard.KEY_UP)
        {
            return -1;
        }
        else if (keyCode == Keyboard.KEY_DOWN)
        {
            return 1;
        }

        return 0;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.font.drawStringWithShadow(I18n.format("metamorph.gui.body_parts.parts"), this.bodyParts.area.x, this.bodyParts.area.y - 12, 0xffffff);

        if (this.elements.isVisible())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.body_parts.limbs"), this.limbs.area.x, this.limbs.area.y - 12, 0xffffff);
        }

        super.draw(context);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.bodyParts.setIndex(tag.getInteger("Index"));

        BodyPart part = this.bodyParts.getCurrentFirst();

        if (part != null)
        {
            this.setPart(part);
        }
    }

    @Override
    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = super.toNBT();

        tag.setInteger("Index", this.bodyParts.getIndex());

        return tag;
    }

    public static class GuiBodyPartTransformations extends GuiTransformations
    {
        public BodyPart part;

        public GuiBodyPartTransformations(Minecraft mc)
        {
            super(mc);
        }

        public void setBodyPart(BodyPart part)
        {
            this.part = part;

            if (part != null)
            {
                this.fillT(part.translate.x, part.translate.y, part.translate.z);
                this.fillS(part.scale.x, part.scale.y, part.scale.z);
                this.fillR(part.rotate.x, part.rotate.y, part.rotate.z);
            }
        }

        @Override
        public void setT(double x, double y, double z)
        {
            this.part.translate.x = (float) x;
            this.part.translate.y = (float) y;
            this.part.translate.z = (float) z;
        }

        @Override
        public void setS(double x, double y, double z)
        {
            this.part.scale.x = (float) x;
            this.part.scale.y = (float) y;
            this.part.scale.z = (float) z;
        }

        @Override
        public void setR(double x, double y, double z)
        {
            this.part.rotate.x = (float) x;
            this.part.rotate.y = (float) y;
            this.part.rotate.z = (float) z;
        }
    }
}
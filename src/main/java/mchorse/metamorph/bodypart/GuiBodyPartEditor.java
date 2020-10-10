package mchorse.metamorph.bodypart;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    protected GuiIconElement add;
    protected GuiIconElement dupe;
    protected GuiIconElement remove;
    protected GuiIconElement copy;
    protected GuiIconElement paste;

    protected GuiBodyPartTransformations transformations;

    protected GuiStringListElement limbs;
    protected GuiElement elements;

    protected BodyPartManager parts;
    protected BodyPart part;

    protected GuiInventoryElement inventory;
    protected GuiElement stacks;
    protected GuiSlotElement[] slots = new GuiSlotElement[6];

    public GuiBodyPartEditor(Minecraft mc, GuiAbstractMorph editor)
    {
        super(mc, editor);

        this.limbs = new GuiStringListElement(mc, (str) -> this.pickLimb(str.get(0)));

        this.bodyParts = new GuiBodyPartListElement(mc, (part) -> this.setPart(part.isEmpty() ? null : part.get(0)));
        this.bodyParts.background().sorting();

        this.pickMorph = new GuiNestedEdit(mc, (editing) ->
        {
            BodyPart part = this.part;

            this.editor.morphs.nestEdit(part.morph.get(), editing, (morph) ->
            {
                if (part != null)
                {
                    part.morph.setDirect(MorphUtils.copy(morph));
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
        this.transformations = new GuiBodyPartTransformations(mc);

        int width = 110;

        GuiElement sidebar = new GuiElement(mc);

        sidebar.flex().relative(this).x(10).y(1, -30).wh(width, 20).row(0).height(20);
        sidebar.add(this.add, this.dupe, this.remove, this.copy, this.paste);

        GuiElement bottomEditor = new GuiElement(mc);

        bottomEditor.flex().relative(this).x(1, -115).y(1, -10).w(width).anchorY(1F);
        bottomEditor.flex().column(5).vertical().stretch();
        bottomEditor.add(this.enabled, this.useTarget);

        this.transformations.flex().relative(this.area).x(0.5F, -95).y(1, -10).wh(190, 70).anchorY(1F);
        this.limbs.flex().relative(this).set(0, 50, width, 90).x(1, -115).hTo(bottomEditor.area, -5);
        this.pickMorph.flex().relative(this).set(0, 10, width, 20).x(1, -115);
        this.bodyParts.flex().relative(this).set(10, 22, width, 0).hTo(this.transformations.flex(), 1F, -20);

        this.elements = new GuiElement(mc);
        this.elements.add(bottomEditor, this.limbs, this.pickMorph, this.transformations);
        this.add(sidebar, this.bodyParts, this.elements);

        /* Inventory */
        this.stacks = new GuiElement(mc);
        this.stacks.flex().relative(this).x(0.5F).y(10).anchor(0.5F, 0).row(5).resize();

        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.inventory.flex().relative(this.stacks).x(0.5F, 0).y(1, 10).anchor(0.5F, 0).row(6);
        this.inventory.setVisible(false);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlotElement(mc, i, this.inventory);
            this.stacks.add(this.slots[i]);
        }

        this.elements.add(this.stacks, this.inventory);
    }

    protected void addPart(GuiIconElement b)
    {
        BodyPart part = new BodyPart();

        part.init();

        this.parts.parts.add(part);
        this.setPart(part);

        this.bodyParts.setCurrentDirect(part);
        this.bodyParts.update();
    }

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

    protected void pickItem(ItemStack stack)
    {
        if (this.part == null)
        {
            return;
        }

        GuiSlotElement element = this.inventory.linked;

        element.stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.part.slots[element.slot] = element.stack;
        this.inventory.unlink();
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
        this.part.limb = str;
    }

    public void fillBodyPart(BodyPart part)
    {
        if (part != null)
        {
            this.transformations.setBodyPart(part);

            this.enabled.toggled(part.enabled);
            this.useTarget.toggled(part.useTarget);

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].stack = part.slots[i];
            }
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        this.font.drawStringWithShadow(I18n.format("metamorph.gui.body_parts.parts"), this.bodyParts.area.x, this.bodyParts.area.y - 12, 0xffffff);

        if (this.elements.isVisible())
        {
            Gui.drawRect(this.limbs.area.x, this.limbs.area.y, this.limbs.area.ex(), this.limbs.area.ey(), 0x88000000);
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
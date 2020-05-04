package mchorse.metamorph.bodypart;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
    protected GuiBodyPartListElement bodyParts;
    protected GuiNestedEdit pickMorph;
    protected GuiToggleElement useTarget;

    protected GuiButtonElement addPart;
    protected GuiButtonElement removePart;

    protected GuiBodyPartTransformations transformations;

    protected GuiStringListElement limbs;
    protected GuiElements<GuiElement> elements = new GuiElements<GuiElement>(this);

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
        this.bodyParts.background();

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

        this.addPart = new GuiButtonElement(mc, IKey.lang("metamorph.gui.add"), this::addPart);
        this.removePart = new GuiButtonElement(mc, IKey.lang("metamorph.gui.remove"), this::removePart);
        this.useTarget = new GuiToggleElement(mc, IKey.lang("metamorph.gui.body_parts.use_target"), false, this::toggleTarget);
        this.transformations = new GuiBodyPartTransformations(mc);

        this.transformations.flex().relative(this.area).x(0.5F, -95).y(1, -10).wh(190, 70).anchorY(1F);
        this.limbs.flex().relative(this).set(0, 50, 105, 90).x(1, -115).h(1, -80);
        this.pickMorph.flex().relative(this).set(0, 10, 105, 20).x(1, -115);
        this.addPart.flex().relative(this).set(10, 10, 50, 20);
        this.removePart.flex().relative(this.addPart).set(55, 0, 50, 20);
        this.bodyParts.flex().relative(this).set(10, 50, 105, 0).hTo(this.transformations.flex(), 1F);
        this.useTarget.flex().relative(this).set(0, 0, 105, 11).x(1, -115).y(1, -21);

        this.elements.add(this.limbs, this.pickMorph, this.useTarget, this.transformations);
        this.add(this.addPart, this.removePart, this.bodyParts, this.elements);

        /* Inventory */
        this.stacks = new GuiElement(mc);
        this.stacks.flex().relative(this).x(0.5F).y(10).anchor(0.5F, 0).row(5).resize();

        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.inventory.flex().relative(this.stacks).x(0.5F, 0).y(1, 10).anchor(0.5F, 0).wh(200, 100).row(6);
        this.inventory.setVisible(false);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlotElement(mc, i, this.inventory);
            this.slots[i].flex().wh(24, 24);
            this.stacks.add(this.slots[i]);
        }

        this.add(this.stacks, this.inventory);
    }

    protected void addPart(GuiButtonElement b)
    {
        List<BodyPart> currentPart = this.bodyParts.getCurrent();
        List<String> currentLimb = this.limbs.getCurrent();

        BodyPart part = currentPart.isEmpty() ? null : currentPart.get(0);
        String limb = currentLimb.isEmpty() ? null : currentLimb.get(0);

        if (part == null)
        {
            part = new BodyPart();

            if (limb != null)
            {
                part.limb = limb;
            }
        }
        else
        {
            part = part.copy();
        }

        part.init();

        this.parts.parts.add(part);
        this.part = part;
        this.setPart(part);

        this.bodyParts.setCurrentDirect(part);
        this.bodyParts.update();
    }

    protected void removePart(GuiButtonElement b)
    {
        if (this.part == null)
        {
            return;
        }

        List<BodyPart> parts = this.parts.parts;
        int index = parts.indexOf(this.part);

        if (index != -1)
        {
            parts.remove(this.part);
            this.bodyParts.update();
            index--;

            if (parts.size() >= 1)
            {
                this.setPart(parts.get(index >= 0 ? index : 0));
            }
            else
            {
                this.setPart(null);
            }
        }
    }

    protected void toggleTarget(GuiToggleElement b)
    {
        if (this.part != null)
        {
            this.part.useTarget = b.isToggled();
        }
    }

    protected void pickItem(ItemStack stack)
    {
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
        public void setT(float x, float y, float z)
        {
            this.part.translate.x = x;
            this.part.translate.y = y;
            this.part.translate.z = z;
        }

        @Override
        public void setS(float x, float y, float z)
        {
            this.part.scale.x = x;
            this.part.scale.y = y;
            this.part.scale.z = z;
        }

        @Override
        public void setR(float x, float y, float z)
        {
            this.part.rotate.x = x;
            this.part.rotate.y = y;
            this.part.rotate.z = z;
        }
    }
}
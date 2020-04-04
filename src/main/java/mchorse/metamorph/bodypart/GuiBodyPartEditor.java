package mchorse.metamorph.bodypart;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.utils.resizers.layout.RowResizer;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
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
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
    protected GuiBodyPartListElement bodyParts;
    protected GuiButtonElement pickMorph;
    protected GuiToggleElement useTarget;
    protected GuiCreativeMorphs morphPicker;

    protected GuiButtonElement addPart;
    protected GuiButtonElement removePart;

    protected GuiTrackpadElement tx;
    protected GuiTrackpadElement ty;
    protected GuiTrackpadElement tz;
    protected GuiTrackpadElement sx;
    protected GuiTrackpadElement sy;
    protected GuiTrackpadElement sz;
    protected GuiTrackpadElement rx;
    protected GuiTrackpadElement ry;
    protected GuiTrackpadElement rz;

    protected GuiStringListElement limbs;
    protected GuiElements<GuiElement> elements = new GuiElements<GuiElement>(this);

    protected BodyPartManager parts;
    protected BodyPart part;

    protected GuiInventoryElement inventory;
    protected GuiElement stacks;
    protected GuiSlotElement[] slots = new GuiSlotElement[6];
    protected GuiSlotElement active;

    public GuiBodyPartEditor(Minecraft mc, GuiAbstractMorph editor)
    {
        super(mc, editor);

        this.tx = new GuiTrackpadElement(mc, (value) -> this.part.translate.x = value);
        this.ty = new GuiTrackpadElement(mc, (value) -> this.part.translate.y = value);
        this.tz = new GuiTrackpadElement(mc, (value) -> this.part.translate.z = value);
        this.sx = new GuiTrackpadElement(mc, (value) -> this.part.scale.x = value);
        this.sy = new GuiTrackpadElement(mc, (value) -> this.part.scale.y = value);
        this.sz = new GuiTrackpadElement(mc, (value) -> this.part.scale.z = value);
        this.rx = new GuiTrackpadElement(mc, (value) -> this.part.rotate.x = value);
        this.ry = new GuiTrackpadElement(mc, (value) -> this.part.rotate.y = value);
        this.rz = new GuiTrackpadElement(mc, (value) -> this.part.rotate.z = value);

        this.tx.tooltip(I18n.format("metamorph.gui.x"), Direction.TOP);
        this.ty.tooltip(I18n.format("metamorph.gui.y"), Direction.TOP);
        this.tz.tooltip(I18n.format("metamorph.gui.z"), Direction.TOP);
        this.sx.tooltip(I18n.format("metamorph.gui.x"), Direction.TOP);
        this.sy.tooltip(I18n.format("metamorph.gui.y"), Direction.TOP);
        this.sz.tooltip(I18n.format("metamorph.gui.z"), Direction.TOP);
        this.rx.tooltip(I18n.format("metamorph.gui.x"), Direction.TOP);
        this.ry.tooltip(I18n.format("metamorph.gui.y"), Direction.TOP);
        this.rz.tooltip(I18n.format("metamorph.gui.z"), Direction.TOP);

        this.tx.flex().set(0, 35, 60, 20).parent(this.area).x(0.5F, -95).y(1, -80);
        this.ty.flex().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.flex().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.flex().set(65, 0, 60, 20).relative(this.tx.resizer());
        this.sy.flex().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.flex().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.flex().set(65, 0, 60, 20).relative(this.sx.resizer());
        this.ry.flex().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.flex().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) -> this.pickLimb(str.get(0)));

        this.bodyParts = new GuiBodyPartListElement(mc, (part) -> this.setPart(part.isEmpty() ? null : part.get(0)));
        this.bodyParts.background();

        this.pickMorph = new GuiButtonElement(mc, I18n.format("metamorph.gui.pick"), (b) ->
        {
            if (this.morphPicker == null)
            {
                this.morphPicker = new GuiCreativeMorphsMenu(mc,  (morph) ->
                {
                    if (this.part != null) this.part.morph.setDirect(morph);
                });
                this.morphPicker.flex().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);

                this.morphPicker.resize();
                this.add(this.morphPicker);
            }

            this.morphPicker.setSelected(this.part.morph.get());
            this.morphPicker.setVisible(true);
        });

        this.addPart = new GuiButtonElement(mc, I18n.format("metamorph.gui.add"), (b) ->
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
                part = part.clone(true);
            }

            part.init();

            this.parts.parts.add(part);
            this.bodyParts.update();
            this.bodyParts.setCurrent(part);
            this.part = part;
            this.setPart(part);
        });

        this.removePart = new GuiButtonElement(mc, I18n.format("metamorph.gui.remove"), (b) ->
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
        });

        this.useTarget = new GuiToggleElement(mc, I18n.format("metamorph.gui.use_target"), false, (b) ->
        {
            if (this.part != null) this.part.useTarget = b.isToggled();
        });

        this.limbs.flex().parent(this.area).set(0, 80, 105, 90).x(1, -115).h(1, -106);
        this.pickMorph.flex().parent(this.area).set(0, 40, 105, 20).x(1, -115);
        this.addPart.flex().parent(this.area).set(10, 10, 50, 20);
        this.removePart.flex().relative(this.addPart.resizer()).set(55, 0, 50, 20);
        this.bodyParts.flex().parent(this.area).set(10, 50, 105, 0).h(1, -55);
        this.useTarget.flex().parent(this.area).set(0, 0, 60, 11).x(1, -115).y(1, -21);

        this.elements.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.pickMorph, this.useTarget);
        this.add(this.addPart, this.removePart, this.bodyParts, this.elements);

        /* Inventory */
        this.stacks = new GuiElement(mc);
        this.stacks.flex().parent(this.area).x(0.5F, 0).y(10).wh(174, 24);

        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.inventory.flex().relative(this.stacks.resizer()).x(0.5F, 0).y(1, 10).anchor(0.5F, 0).wh(200, 100);
        this.inventory.setVisible(false);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlotElement(mc, i, (slot) ->
            {
                this.active = slot;
                this.active.selected = true;
                this.inventory.setVisible(true);
            });

            this.slots[i].flex().wh(24, 24);

            this.stacks.add(this.slots[i]);
        }

        RowResizer.apply(this.stacks, 6);
        this.add(this.stacks, this.inventory);
    }

    public void pickItem(ItemStack stack)
    {
        if (this.active != null)
        {
            this.active.stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            this.part.slots[this.active.slot] = this.active.stack;
            this.active = null;
            this.inventory.setVisible(false);
            this.part.updateEntity();
        }
    }

    @Override
    public void fillData(AbstractMorph morph)
    {
        super.fillData(morph);

        if (morph instanceof IBodyPartProvider)
        {
            BodyPartManager manager = ((IBodyPartProvider) morph).getBodyPart();

            this.startEditing(manager);
        }
    }

    public void setLimbs(Collection<String> limbs)
    {
        this.limbs.clear();
        this.limbs.add(limbs);
        this.limbs.sort();
    }

    public void startEditing(BodyPartManager manager)
    {
        this.parts = manager;

        this.bodyParts.setList(manager.parts);
        this.bodyParts.update();
        this.setPart(manager.parts.isEmpty() ? null : manager.parts.get(0));
    }

    public void setupBodyEditor()
    {
        this.bodyParts.update();
        this.setPart(this.parts.parts.isEmpty() ? null : this.parts.parts.get(0));
    }

    protected void setPart(BodyPart part)
    {
        this.part = part;
        this.elements.setVisible(part != null);

        if (this.part != null)
        {
            this.fillBodyPart(part);
            this.limbs.setCurrent(part.limb);
            this.bodyParts.setCurrent(part);
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
            this.tx.setValue(part.translate.x);
            this.ty.setValue(part.translate.y);
            this.tz.setValue(part.translate.z);

            this.sx.setValue(part.scale.x);
            this.sy.setValue(part.scale.y);
            this.sz.setValue(part.scale.z);

            this.rx.setValue(part.rotate.x);
            this.ry.setValue(part.rotate.y);
            this.rz.setValue(part.rotate.z);

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
        this.font.drawStringWithShadow(I18n.format("metamorph.gui.body_parts"), this.bodyParts.area.x, this.bodyParts.area.y - 12, 0xffffff);

        if (this.elements.isVisible())
        {
            Gui.drawRect(this.limbs.area.x, this.limbs.area.y, this.limbs.area.ex(), this.limbs.area.ey(), 0x88000000);
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.limbs"), this.limbs.area.x, this.limbs.area.y - 12, 0xffffff);

            this.font.drawStringWithShadow(I18n.format("metamorph.gui.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);
        }

        super.draw(context);
    }

    /**
     * Body part list which displays body parts 
     */
    public static class GuiBodyPartListElement extends GuiListElement<BodyPart>
    {
        public GuiBodyPartListElement(Minecraft mc, Consumer<List<BodyPart>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        protected String elementToString(BodyPart element, int i, int x, int y, boolean hover, boolean selected)
        {
            return i + (!element.limb.isEmpty() ? " - " + element.limb : "");
        }
    }
}
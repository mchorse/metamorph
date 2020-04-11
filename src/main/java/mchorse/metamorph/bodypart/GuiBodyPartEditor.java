package mchorse.metamorph.bodypart;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.widgets.GuiInventory;
import mchorse.mclib.client.gui.widgets.GuiInventory.IInventoryPicker;
import mchorse.mclib.client.gui.widgets.GuiSlot;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBodyPartEditor extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph> implements IInventoryPicker
{
    protected GuiBodyPartListElement bodyParts;
    protected GuiButtonElement<GuiButton> pickMorph;
    protected GuiButtonElement<GuiCheckBox> useTarget;
    protected GuiCreativeMorphs morphPicker;

    protected GuiButtonElement<GuiButton> addPart;
    protected GuiButtonElement<GuiButton> removePart;

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
    protected GuiElements<IGuiElement> elements = new GuiElements<IGuiElement>();

    protected BodyPartManager parts;
    protected BodyPart part;

    protected GuiInventory inventory;
    protected GuiSlot[] slots = new GuiSlot[6];
    protected GuiSlot active;

    public GuiBodyPartEditor(Minecraft mc, GuiAbstractMorph editor)
    {
        super(mc, editor);

        this.createChildren();

        this.tx = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.x"), (value) -> this.part.part.translate[0] = value);
        this.ty = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.y"), (value) -> this.part.part.translate[1] = value);
        this.tz = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.z"), (value) -> this.part.part.translate[2] = value);
        this.sx = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.x"), (value) -> this.part.part.scale[0] = value);
        this.sy = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.y"), (value) -> this.part.part.scale[1] = value);
        this.sz = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.z"), (value) -> this.part.part.scale[2] = value);
        this.rx = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.x"), (value) -> this.part.part.rotate[0] = value);
        this.ry = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.y"), (value) -> this.part.part.rotate[1] = value);
        this.rz = new GuiTrackpadElement(mc, I18n.format("metamorph.gui.z"), (value) -> this.part.part.rotate[2] = value);

        this.tx.resizer().set(0, 35, 60, 20).parent(this.area).x(0.5F, -95).y(1, -80);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(65, 0, 60, 20).relative(this.tx.resizer());
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(65, 0, 60, 20).relative(this.sx.resizer());
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) -> this.pickLimb(str));

        this.bodyParts = new GuiBodyPartListElement(mc, (part) -> this.setPart(part));

        this.pickMorph = GuiButtonElement.button(mc, I18n.format("metamorph.gui.pick"), (b) ->
        {
            if (this.morphPicker == null)
            {
                IMorphing morphing = Morphing.get(this.mc.player);

                this.morphPicker = new GuiCreativeMorphsMenu(mc, 6, null, morphing);
                this.morphPicker.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
                this.morphPicker.callback = (morph) ->
                {
                    if (this.part != null) this.part.part.morph.setDirect(morph);
                };

                GuiScreen screen = Minecraft.getMinecraft().currentScreen;

                this.morphPicker.resize(screen.width, screen.height);
                this.children.add(this.morphPicker);
            }

            this.children.unfocus();
            this.morphPicker.setSelected(this.part.part.morph.get());
            this.morphPicker.setVisible(true);
        });

        this.addPart = GuiButtonElement.button(mc, I18n.format("metamorph.gui.add"), (b) ->
        {
            BodyPart part = this.bodyParts.getCurrent();
            String limb = this.limbs.getCurrent();

            if (part == null)
            {
                part = new BodyPart();
                part.part = new MorphBodyPart();

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

        this.removePart = GuiButtonElement.button(mc, I18n.format("metamorph.gui.remove"), (b) ->
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

        this.useTarget = GuiButtonElement.checkbox(mc, I18n.format("metamorph.gui.use_target"), false, (b) ->
        {
            if (this.part != null) this.part.part.useTarget = b.button.isChecked();
        });

        this.limbs.resizer().parent(this.area).set(0, 80, 105, 90).x(1, -115).h(1, -106);
        this.pickMorph.resizer().parent(this.area).set(0, 40, 105, 20).x(1, -115);
        this.addPart.resizer().parent(this.area).set(10, 10, 50, 20);
        this.removePart.resizer().relative(this.addPart.resizer()).set(55, 0, 50, 20);
        this.bodyParts.resizer().parent(this.area).set(10, 50, 105, 0).h(1, -55);
        this.useTarget.resizer().parent(this.area).set(0, 0, 60, 11).x(1, -115).y(1, -21);

        this.elements.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.pickMorph, this.useTarget);
        this.children.add(this.addPart, this.removePart, this.bodyParts, this.elements);

        /* Inventory */
        this.inventory = new GuiInventory(this, mc.player);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlot(i);
        }
    }

    @Override
    public void pickItem(GuiInventory inventory, ItemStack stack)
    {
        if (this.active != null)
        {
            this.active.stack = stack == null ? null : stack.copy();
            this.part.part.slots[this.active.slot] = this.active.stack;
            this.inventory.visible = false;
            this.part.part.updateEntity();
        }
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i].update(this.area.getX(0.5F) + 30 * i - 85, this.area.y + 10);
        }

        this.inventory.update(this.area.getX(0.5F), this.area.getY(0.5F) - 40);

        if (this.morphPicker != null)
        {
            this.morphPicker.setPerRow((int) Math.ceil(this.morphPicker.area.w / 50.0F));
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
        this.inventory.player = this.mc.player;
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
            this.fillBodyPart(part.part);
            this.limbs.setCurrent(part.limb);
            this.bodyParts.setCurrent(part);
        }
    }

    protected void pickLimb(String str)
    {
        this.part.limb = str;
    }

    public void fillBodyPart(MorphBodyPart part)
    {
        if (part != null)
        {
            this.tx.trackpad.setValue(part.translate[0]);
            this.ty.trackpad.setValue(part.translate[1]);
            this.tz.trackpad.setValue(part.translate[2]);

            this.sx.trackpad.setValue(part.scale[0]);
            this.sy.trackpad.setValue(part.scale[1]);
            this.sz.trackpad.setValue(part.scale[2]);

            this.rx.trackpad.setValue(part.rotate[0]);
            this.ry.trackpad.setValue(part.rotate[1]);
            this.rz.trackpad.setValue(part.rotate[2]);

            this.useTarget.button.setIsChecked(part.useTarget);

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].stack = part.slots[i];
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.elements.isVisible())
        {
            this.inventory.mouseClicked(mouseX, mouseY, mouseButton);
            this.active = null;

            for (GuiSlot slot : this.slots)
            {
                if (slot.area.isInside(mouseX, mouseY))
                {
                    this.active = slot;
                    this.inventory.visible = true;
                }
            }

            if (this.active != null || (this.inventory.visible && this.inventory.area.isInside(mouseX, mouseY)))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.elements.isVisible())
        {
            for (GuiSlot slot : this.slots)
            {
                slot.draw(mouseX, mouseY, partialTicks);
            }

            if (this.active != null)
            {
                Area a = this.active.area;

                Gui.drawRect(a.x, a.y, a.x + a.w, a.y + a.h, 0x880088ff);
            }

            this.inventory.draw(mouseX, mouseY, partialTicks);
        }

        Gui.drawRect(this.bodyParts.area.x, this.bodyParts.area.y, this.bodyParts.area.getX(1), this.bodyParts.area.getY(1), 0x88000000);
        this.font.drawStringWithShadow(I18n.format("metamorph.gui.body_parts"), this.bodyParts.area.x, this.bodyParts.area.y - 12, 0xffffff);

        if (this.elements.isVisible())
        {
            Gui.drawRect(this.limbs.area.x, this.limbs.area.y, this.limbs.area.getX(1), this.limbs.area.getY(1), 0x88000000);
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.limbs"), this.limbs.area.x, this.limbs.area.y - 12, 0xffffff);

            this.font.drawStringWithShadow(I18n.format("metamorph.gui.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    /**
     * Body part list which displays body parts 
     */
    public static class GuiBodyPartListElement extends GuiListElement<BodyPart>
    {
        public GuiBodyPartListElement(Minecraft mc, Consumer<BodyPart> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        public void sort()
        {}

        @Override
        public void drawElement(BodyPart element, int i, int x, int y, boolean hover)
        {
            if (this.current == i)
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
            }

            String label = i + (!element.limb.isEmpty() ? " - " + element.limb : "");

            this.font.drawStringWithShadow(label, x + 4, y + 4, hover ? 16777120 : 0xffffff);
        }
    }
}
package mchorse.vanilla_pack.editors.panels;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.vanilla_pack.morphs.ItemStackMorph;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemStackPanel extends GuiMorphPanel<ItemStackMorph, GuiAbstractMorph<? extends ItemStackMorph>>
{
    public GuiSlotElement slot;
    public GuiToggleElement lighting;
    private boolean isItemPanel;
    public GuiToggleElement animated;
    public GuiToggleElement realSize;

    public GuiItemStackPanel(Minecraft mc, GuiAbstractMorph<? extends ItemStackMorph> editor, boolean isItemPanel)
    {
        super(mc, editor);

        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.morph.setStack(stack));
        this.lighting = new GuiToggleElement(mc, IKey.lang("metamorph.gui.label.lighting"), (b) -> this.morph.lighting = b.isToggled());

        this.slot.flex().relative(this).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);
        this.lighting.flex().relative(this).xy(10, 10).w(110);

        if (isItemPanel){
            this.isItemPanel = true;
            this.animated = new GuiToggleElement(mc, IKey.lang("metamorph.gui.editor.item_morph.animated"), (b) -> this.morph.animated = b.isToggled());
            this.realSize = new GuiToggleElement(mc, IKey.lang("metamorph.gui.editor.item_morph.real_size"), (b) -> this.morph.realSize = b.isToggled());
            this.animated.flex().relative(this.lighting.resizer()).y(1F, 5).w(110);
            this.realSize.flex().relative(this.animated.resizer()).y(1F, 5).w(110);
            this.add(this.slot, this.lighting, this.animated, this.realSize);
            return;
        }

        this.add(this.slot, this.lighting);
    }

    @Override
    public void fillData(ItemStackMorph morph)
    {
        super.fillData(morph);

        this.slot.setStack(morph.getStack());
        this.lighting.toggled(morph.lighting);
        if (this.isItemPanel)
        {
            this.animated.toggled(morph.animated);
            this.realSize.toggled(morph.realSize);
        }
    }
}
package mchorse.vanilla_pack.editors.panels;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.vanilla_pack.morphs.ItemMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemPanel extends GuiMorphPanel<ItemMorph, GuiAbstractMorph<? extends ItemMorph>>
{
    public GuiSlotElement slot;
    public GuiToggleElement lighting;
    public GuiCirculateElement transform;

    public GuiItemPanel(Minecraft mc, GuiAbstractMorph<? extends ItemMorph> editor)
    {
        super(mc, editor);

        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.morph.setStack(stack));
        this.lighting = new GuiToggleElement(mc, IKey.lang("metamorph.gui.label.lighting"), (b) -> this.morph.lighting = b.isToggled());

        this.slot.flex().relative(this).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);
        this.lighting.flex().relative(this).xy(10, 10).w(110);

        this.transform = new GuiCirculateElement(mc, (b) ->
        {
            ItemCameraTransforms.TransformType type = ItemCameraTransforms.TransformType.values()[b.getValue()];

            this.morph.transform = ItemMorph.getTransformTypes().inverse().get(type);
        });

        this.transform.flex().relative(this.lighting).y(1F, 5).w(1F);

        for (ItemCameraTransforms.TransformType transform : ItemCameraTransforms.TransformType.values())
        {
            String key = ItemMorph.getTransformTypes().inverse().get(transform);

            this.transform.addLabel(IKey.lang("metamorph.gui.item.transform." + key));
        }

        this.add(this.slot, this.lighting, this.transform);
    }

    @Override
    public void fillData(ItemMorph morph)
    {
        super.fillData(morph);

        ItemCameraTransforms.TransformType type = morph.getTransformType();

        this.slot.setStack(morph.getStack());
        this.lighting.toggled(morph.lighting);
        this.transform.setValue(type.ordinal());
    }
}
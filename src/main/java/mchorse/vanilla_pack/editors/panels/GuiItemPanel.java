package mchorse.vanilla_pack.editors.panels;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.resources.RLUtils;
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
    public GuiButtonElement texture;
    public GuiToggleElement animation;

    public GuiTexturePicker picker;

    public GuiItemPanel(Minecraft mc, GuiAbstractMorph<? extends ItemMorph> editor)
    {
        super(mc, editor);

        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.morph.setStack(stack));
        this.lighting = new GuiToggleElement(mc, IKey.lang("metamorph.gui.label.lighting"), (b) -> this.morph.lighting = b.isToggled());
        this.animation = new GuiToggleElement(mc, IKey.lang("metamorph.gui.item.animation"), (b) -> this.morph.animation = b.isToggled());

        this.slot.flex().relative(this).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);

        this.transform = new GuiCirculateElement(mc, (b) ->
        {
            ItemCameraTransforms.TransformType type = ItemCameraTransforms.TransformType.values()[b.getValue()];

            this.morph.transform = ItemMorph.getTransformTypes().inverse().get(type);
        });

        for (ItemCameraTransforms.TransformType transform : ItemCameraTransforms.TransformType.values())
        {
            String key = ItemMorph.getTransformTypes().inverse().get(transform);

            this.transform.addLabel(IKey.lang("metamorph.gui.item.transform." + key));
        }

        this.texture = new GuiButtonElement(mc, IKey.lang("metamorph.gui.editor.texture"), (b) ->
        {
            this.picker.refresh();
            this.picker.fill(this.morph.texture);
            this.add(this.picker);
            this.picker.resize();
        });
        this.picker = new GuiTexturePicker(mc, (rl) -> this.morph.texture = RLUtils.clone(rl));
        this.picker.flex().relative(this).wh(1F, 1F);

        GuiElement column = Elements.column(mc, 5, this.lighting, this.transform, this.texture, this.animation);

        column.flex().relative(this).xy(10, 10).w(110);

        this.add(this.slot, column);
    }

    @Override
    public void fillData(ItemMorph morph)
    {
        super.fillData(morph);

        ItemCameraTransforms.TransformType type = morph.getTransformType();

        this.slot.setStack(morph.getStack());
        this.lighting.toggled(morph.lighting);
        this.transform.setValue(type.ordinal());
        this.animation.toggled(morph.animation);
    }
}
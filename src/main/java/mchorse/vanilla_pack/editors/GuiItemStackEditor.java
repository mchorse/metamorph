package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.vanilla_pack.morphs.ItemStackMorph;
import net.minecraft.client.Minecraft;

public class GuiItemStackEditor extends GuiMorphPanel<ItemStackMorph, GuiAbstractMorph<? extends ItemStackMorph>>
{
	public GuiSlotElement slot;
	public GuiInventoryElement inventory;

	public GuiItemStackEditor(Minecraft mc, GuiAbstractMorph<? extends ItemStackMorph> editor)
	{
		super(mc, editor);

		this.slot = new GuiSlotElement(mc, 0, (slot) ->
		{
			slot.selected = true;
			this.inventory.setVisible(true);
		});

		this.inventory = new GuiInventoryElement(mc, (stack) ->
		{
			this.slot.selected = false;
			this.slot.stack = stack;

			this.morph.setStack(stack);
			this.inventory.setVisible(false);
		});
		this.inventory.setVisible(false);

		this.slot.flex().parent(this.area).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);
		this.inventory.flex().parent(this.slot.area).x(0.5F, 0).y(-5).wh(200, 100).anchor(0.5F, 1);

		this.add(this.slot, this.inventory);
	}

	@Override
	public void fillData(ItemStackMorph morph)
	{
		super.fillData(morph);

		this.slot.stack = morph.getStack();
	}
}
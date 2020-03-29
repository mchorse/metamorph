package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.vanilla_pack.morphs.BlockMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GuiBlockEditor extends GuiMorphPanel<BlockMorph, GuiBlockMorph>
{
	public GuiSlotElement slot;
	public GuiInventoryElement inventory;

	public GuiBlockEditor(Minecraft mc, GuiBlockMorph editor)
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

			if (stack.getItem() instanceof ItemBlock)
			{
				this.morph.block = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
				this.inventory.setVisible(false);
			}
		});
		this.inventory.setVisible(false);

		this.slot.flex().parent(this.area).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);
		this.inventory.flex().parent(this.slot.area).x(0.5F, 0).y(-5).wh(200, 100).anchor(0.5F, 1);

		this.add(this.slot, this.inventory);
	}

	@Override
	public void fillData(BlockMorph morph)
	{
		super.fillData(morph);

		this.slot.stack = new ItemStack(morph.block.getBlock(), 1, morph.block.getBlock().getMetaFromState(morph.block));
	}
}
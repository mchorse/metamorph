package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.util.MMIcons;
import mchorse.vanilla_pack.editors.panels.GuiItemStackPanel;
import mchorse.vanilla_pack.morphs.ItemMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemMorph extends GuiAbstractMorph<ItemMorph>
{
	public GuiItemStackPanel block;

	public GuiItemMorph(Minecraft mc)
	{
		super(mc);

		this.defaultPanel = this.block = new GuiItemStackPanel(mc, this);
		this.registerPanel(this.block, IKey.lang("metamorph.gui.editor.item_morph"), MMIcons.ITEM);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof ItemMorph;
	}
}
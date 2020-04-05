package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.util.MMIcons;
import mchorse.vanilla_pack.editors.panels.GuiItemStackPanel;
import mchorse.vanilla_pack.morphs.ItemMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiItemMorph extends GuiAbstractMorph<ItemMorph>
{
	public GuiItemStackPanel block;

	public GuiItemMorph(Minecraft mc)
	{
		super(mc);

		this.defaultPanel = this.block = new GuiItemStackPanel(mc, this);
		this.registerPanel(this.block, I18n.format("metamorph.gui.panels.item"), MMIcons.ITEM);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof ItemMorph;
	}

	@Override
	protected void drawMorph(GuiContext context)
	{
		try
		{
			this.morph.renderOnScreen(this.mc.player, this.area.mx(), this.area.my(), this.area.h / 3, 1);
		}
		catch (Exception e)
		{}

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
}
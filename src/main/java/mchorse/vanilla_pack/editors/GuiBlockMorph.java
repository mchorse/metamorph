package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.util.MMIcons;
import mchorse.vanilla_pack.morphs.BlockMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiBlockMorph extends GuiAbstractMorph<BlockMorph>
{
	public GuiBlockEditor block;

	public GuiBlockMorph(Minecraft mc)
	{
		super(mc);

		this.defaultPanel = this.block = new GuiBlockEditor(mc, this);
		this.registerPanel(this.block, I18n.format("metamorph.gui.panels.block"), MMIcons.BLOCK);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof BlockMorph;
	}

	@Override
	protected void drawMorph(GuiContext context)
	{
		super.drawMorph(context);

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
}
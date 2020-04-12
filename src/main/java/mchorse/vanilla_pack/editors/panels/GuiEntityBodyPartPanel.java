package mchorse.vanilla_pack.editors.panels;

import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;

public class GuiEntityBodyPartPanel extends GuiBodyPartEditor
{
	public GuiEntityBodyPartPanel(Minecraft mc, GuiAbstractMorph editor)
	{
		super(mc, editor);
	}

	@Override
	protected void setPart(BodyPart part)
	{
		super.setPart(part);
	}

	public void setLimb(String limb)
	{
		try
		{
			this.pickLimb(limb);
			this.limbs.setCurrent(limb);
		}
		catch (Exception e) {}
	}
}
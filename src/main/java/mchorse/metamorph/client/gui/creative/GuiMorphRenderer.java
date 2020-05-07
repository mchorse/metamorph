package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class GuiMorphRenderer extends GuiModelRenderer
{
	public AbstractMorph morph;

	public GuiMorphRenderer(Minecraft mc)
	{
		super(mc);
	}

	@Override
	protected void drawUserModel(GuiContext context)
	{
		if (this.morph == null)
		{
			return;
		}

		this.entity.rotationYaw = this.entity.prevRotationYaw = 0;
		this.entity.rotationPitch = this.entity.prevRotationPitch = 0;
		this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0;
		this.entity.renderYawOffset = this.entity.prevRenderYawOffset = 0;
		this.morph.render(this.entity, 0, 0, 0, this.yaw, context.partialTicks);
	}
}
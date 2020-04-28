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

		EntityPlayer player = this.mc.player;

		float yaw = player.rotationYaw;
		float head = player.rotationYawHead;
		float pitch = player.rotationPitch;
		float yawOffset = player.renderYawOffset;

		player.rotationYaw = player.prevRotationYaw = 0;
		player.rotationYawHead = player.prevRotationYawHead = 0;
		player.rotationPitch = player.prevRotationPitch = 0;
		player.renderYawOffset = player.prevRenderYawOffset = 0;

		this.morph.render(player, 0, 0, 0, this.yaw, context.partialTicks);

		player.rotationYaw = player.prevRotationYaw = yaw;
		player.rotationYawHead = player.prevRotationYawHead = head;
		player.rotationPitch = player.prevRotationPitch = pitch;
		player.renderYawOffset = player.prevRenderYawOffset = yawOffset;
	}
}
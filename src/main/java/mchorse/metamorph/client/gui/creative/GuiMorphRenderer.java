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
		EntityPlayer player = this.mc.player;

		float yaw = player.rotationYaw;
		float pitch = player.rotationPitch;

		player.rotationYaw = player.prevRotationYaw = this.yaw;
		player.rotationPitch = player.prevRotationPitch = this.pitch;

		this.morph.render(player, 0, 0, 0, this.yaw, this.pitch);

		player.rotationYaw = player.prevRotationYaw = yaw;
		player.rotationPitch = player.prevRotationPitch = pitch;
	}
}
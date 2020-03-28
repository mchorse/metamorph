package mchorse.metamorph.client.gui.creative;

import mchorse.metamorph.api.creative.MorphSection;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiUserSection extends GuiMorphSection
{
	public GuiUserSection(Minecraft mc, MorphSection section, Consumer<GuiMorphSection> callback)
	{
		super(mc, section, callback);
	}
}
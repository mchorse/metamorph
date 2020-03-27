package mchorse.metamorph.api.creative;

import mchorse.metamorph.client.gui.creative.GuiMorphSection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MorphSection
{
	public String title;
	public List<MorphCategory> categories = new ArrayList<MorphCategory>();

	public MorphSection(String title)
	{
		this.title = title;
	}

	/**
	 * This method gets called when a new morph picker appears
	 */
	public void update(World world)
	{}

	/**
	 * This method gets called when player exits to the main menu
	 */
	public void reset()
	{}

	@SideOnly(Side.CLIENT)
	public GuiMorphSection getGUI(Minecraft mc, Consumer<GuiMorphSection> callback)
	{
		return new GuiMorphSection(mc, this, callback);
	}
}
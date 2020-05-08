package mchorse.metamorph.api.creative.sections;

import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsList;
import mchorse.metamorph.client.gui.creative.GuiMorphSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
	public boolean hidden;

	public MorphSection(String title)
	{
		this.title = title;
	}

	@SideOnly(Side.CLIENT)
	public String getTitle()
	{
		return I18n.format("morph.section." + this.title);
	}

	public void add(MorphCategory category)
	{
		this.categories.add(category);
	}

	public void remove(MorphCategory category)
	{
		this.categories.remove(category);
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
	public GuiMorphSection getGUI(Minecraft mc, GuiCreativeMorphsList parent, Consumer<GuiMorphSection> callback)
	{
		return new GuiMorphSection(mc, parent, this, callback);
	}

	public boolean keyTyped(EntityPlayer player, int keycode)
	{
		for (MorphCategory category : this.categories)
		{
			if (category.keyTyped(player, keycode))
			{
				return true;
			}
		}

		return false;
	}
}
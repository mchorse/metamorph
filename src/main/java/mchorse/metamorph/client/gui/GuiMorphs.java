package mchorse.metamorph.client.gui;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiMorphSection;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class GuiMorphs extends GuiScrollElement
{
	/**
	 * Cached previous filter. Used for avoiding double recalculations
	 */
	public String filter = "";

	public List<GuiMorphSection> sections = new ArrayList<GuiMorphSection>();
	public GuiMorphSection selected;

	public GuiMorphs(Minecraft mc)
	{
		super(mc);
	}

	/* Morph selection */

	public void setSelected(AbstractMorph morph)
	{
		this.resetSelected();
	}

	public void setSelectedDirect(GuiMorphSection selected)
	{
		this.setSelectedDirect(selected, selected.morph, selected.category);
	}

	public void setSelectedDirect(GuiMorphSection section, AbstractMorph morph, MorphCategory category)
	{
		this.resetSelected();

		this.selected = section;
		this.selected.set(morph, category);
	}

	public void resetSelected()
	{
		if (this.selected != null)
		{
			this.selected.reset();
		}
	}

	public AbstractMorph getSelected()
	{
		return this.selected == null ? null : this.selected.morph;
	}

	/* Section management */

	/**
	 * Set filter for search
	 *
	 * This method is responsible for recalculating the hidden flag of the
	 * individual cells and changing heights and y position of each category.
	 */
	public void setFilter(String filter)
	{
		if (filter.equals(this.filter))
		{
			return;
		}

		String lcfilter = filter.toLowerCase().trim();

		for (GuiMorphSection section : this.sections)
		{
			section.setFilter(lcfilter);
		}

		this.filter = lcfilter;
	}

	public void setFavorite(boolean favorite)
	{
		for (GuiMorphSection section : this.sections)
		{
			section.favorite = favorite;
		}
	}

	/**
	 * Scroll to the selected morph
	 *
	 * This method heavily relies on the elements drawn before hand
	 */
	public void scrollTo()
	{
		AbstractMorph morph = this.getSelected();

		if (morph == null)
		{
			return;
		}

		int y = 0;

		for (GuiMorphSection section : this.sections)
		{
			if (section.morph == morph)
			{
				this.scroll.scrollIntoView(y + section.selectedY, section.cellHeight + 30);

				break;
			}

			y += section.height;
		}
	}
}
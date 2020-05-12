package mchorse.metamorph.client.gui;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiMorphSection;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

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

	public int x;
	public int y;
	public boolean scrollTo;

	public GuiMorphs(Minecraft mc)
	{
		super(mc);

		this.scroll.scrollSpeed = 35;

		IKey category = IKey.lang("metamorph.gui.morphs.keys.category");

		this.keys().register(IKey.lang("metamorph.gui.morphs.keys.down"), Keyboard.KEY_DOWN, () -> this.pickMorph(0, 1)).category(category);
		this.keys().register(IKey.lang("metamorph.gui.morphs.keys.up"), Keyboard.KEY_UP, () -> this.pickMorph(0, -1)).category(category);
		this.keys().register(IKey.lang("metamorph.gui.morphs.keys.right"), Keyboard.KEY_RIGHT, () -> this.pickMorph(1, 0)).category(category);
		this.keys().register(IKey.lang("metamorph.gui.morphs.keys.left"), Keyboard.KEY_LEFT, () -> this.pickMorph(-1, 0)).category(category);
	}

	private void pickMorph(int x, int y)
	{
		if (this.selected == null || this.selected.morph == null)
		{
			x = 0;
			y = 0;

			this.selected = this.sections.get(0);
			this.selected.category = this.selected.section.categories.get(0);
			this.x = this.y = 0;
		}
		else
		{
			this.selected.calculateXY(this);
		}

		int ox = this.x;

		this.x += x;
		this.y += y;

		AbstractMorph morph = this.selected.getMorphAt(this);

		if (morph != null)
		{
			this.selected.morph = morph;
			this.selected.pick(this.selected.morph, this.selected.category);
			this.scrollTo();

			return;
		}

		if (y < 0 && this.isFirstCategory(this.selected))
		{
			return;
		}
		else if (y > 0 && this.isLastCategory(this.selected))
		{
			return;
		}

		if (y == 0)
		{
			y = 1;
		}

		GuiMorphSection original = this.selected;
		GuiMorphSection section = this.selected;
		MorphCategory category = this.selected.category;

		do
		{
			int index = section.section.categories.indexOf(category) + y;

			if (index < 0)
			{
				int sectionIndex = this.sections.indexOf(section) - 1;

				if (sectionIndex < 0)
				{
					return;
				}

				section = this.sections.get(sectionIndex);
				category = section.section.categories.get(section.section.categories.size() - 1);
			}
			else if (index >= section.section.categories.size())
			{
				int sectionIndex = this.sections.indexOf(section) + 1;

				if (sectionIndex >= this.sections.size())
				{
					return;
				}

				section = this.sections.get(sectionIndex);
				category = section.section.categories.get(0);
			}
			else
			{
				category = section.section.categories.get(index);
			}
		}
		while (this.isCategoryEmpty(section, category));

		if (category.getMorphs().isEmpty())
		{
			return;
		}

		original.reset();
		this.selected = section;
		this.selected.category = category;
		this.selected.morph = this.getFirstMorph(ox, y);
		this.selected.pick(this.selected.morph, this.selected.category);
		this.scrollTo();
	}

	private boolean isFirstCategory(GuiMorphSection section)
	{
		return section == this.sections.get(0) && section.category == section.section.categories.get(0);
	}

	private boolean isLastCategory(GuiMorphSection section)
	{
		return section == this.sections.get(this.sections.size() - 1) && section.category == section.section.categories.get(section.section.categories.size() - 1);
	}

	private boolean isCategoryEmpty(GuiMorphSection section, MorphCategory category)
	{
		if (category.getMorphs().isEmpty())
		{
			return true;
		}

		return section.getMorphsSize(category) == 0;
	}

	private AbstractMorph getFirstMorph(int ox, int y)
	{
		List<AbstractMorph> list = this.selected.category.getMorphs();
		AbstractMorph first = null;

		int row = this.selected.getPerRow();
		int j = 0;
		int c = this.selected.getMorphsSize(this.selected.category);
		int firstIndex = y < 0 ? c - 1 : 0;
		int lastIndex = y < 0 ? (c - 1) / row * row + ox : ox;

		for (AbstractMorph morph : list)
		{
			if (this.selected.isMatching(morph))
			{
				if (j == firstIndex)
				{
					first = morph;
				}

				if (j == lastIndex)
				{
					return morph;
				}

				j ++;
			}
		}

		return first;
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
				this.scroll.scrollIntoView(y + section.getY(section.morph), section.cellHeight + 30);

				break;
			}

			y += section.height;
		}
	}

	@Override
	public void draw(GuiContext context)
	{
		super.draw(context);

		if (this.scrollTo)
		{
			this.scrollTo();
			this.scrollTo = false;
		}
	}
}
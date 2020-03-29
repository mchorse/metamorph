package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;

public class RecentCategory extends MorphCategory
{
	public RecentCategory(MorphSection parent, String title)
	{
		super(parent, title);
	}

	@Override
	protected void addMorph(AbstractMorph morph)
	{
		while (this.morphs.size() >= 20)
		{
			this.morphs.remove(this.morphs.size() - 1);
		}

		this.morphs.add(0, morph);
	}
}
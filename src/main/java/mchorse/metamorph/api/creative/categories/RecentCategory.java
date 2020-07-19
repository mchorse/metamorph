package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.Metamorph;
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
		while (this.morphs.size() >= Metamorph.maxRecentMorphs.get())
		{
			this.morphs.remove(this.morphs.size() - 1);
		}

		this.morphs.add(0, morph);
	}

	@Override
	public boolean isEditable(AbstractMorph morph)
	{
		return this.morphs.indexOf(morph) != -1;
	}
}
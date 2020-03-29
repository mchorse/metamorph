package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;

public class RecentCategory extends MorphCategory
{
	public RecentCategory(MorphSection parent, String title)
	{
		super(parent, title);
	}

	@Override
	public void add(AbstractMorph morph)
	{
		if (MorphManager.isBlacklisted(morph.name))
		{
			return;
		}

		while (this.morphs.size() >= 20)
		{
			this.morphs.remove(this.morphs.size() - 1);
		}

		MorphManager.INSTANCE.applySettings(morph);

		this.morphs.add(0, morph);
	}
}
package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;

import java.util.ArrayList;
import java.util.List;

public class MorphCategory
{
	public MorphSection parent;

	public String title;
	public List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

	public MorphCategory(MorphSection parent, String title)
	{
		this.parent = parent;
		this.title = title;
	}

	public boolean isHidden()
	{
		return this.morphs.isEmpty();
	}

	public AbstractMorph getEqual(AbstractMorph morph)
	{
		for (AbstractMorph child : this.morphs)
		{
			if (child.equals(morph))
			{
				return child;
			}
		}

		return null;
	}

	public void clear()
	{
		this.morphs.clear();
	}

	public void add(AbstractMorph morph)
	{
		if (MorphManager.isBlacklisted(morph.name))
		{
			return;
		}

		MorphManager.INSTANCE.applySettings(morph);

		this.morphs.add(morph);
	}

	public boolean remove(AbstractMorph morph)
	{
		return this.morphs.remove(morph);
	}

	public void edit(AbstractMorph morph)
	{}
}

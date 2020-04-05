package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.creative.PacketSyncMorph;
import mchorse.metamorph.network.common.survival.PacketSelectMorph;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class AcquiredCategory extends MorphCategory
{
	public AcquiredCategory(MorphSection parent, String title)
	{
		super(parent, title);
	}

	public void setMorph(List<AbstractMorph> morphs)
	{
		this.morphs = morphs;
	}

	@Override
	public void edit(AbstractMorph morph)
	{
		int index = this.morphs.indexOf(morph);

		if (index >= 0)
		{
			Dispatcher.sendToServer(new PacketSyncMorph(morph, index));
		}
	}

	@Override
	protected boolean morph(EntityPlayer player, AbstractMorph morph)
	{
		int index = this.morphs.indexOf(morph);

		if (index >= 0)
		{
			Dispatcher.sendToServer(new PacketSelectMorph(index));
		}

		return true;
	}
}
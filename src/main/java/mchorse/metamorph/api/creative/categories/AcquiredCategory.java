package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.creative.PacketAcquireMorph;
import mchorse.metamorph.network.common.creative.PacketClearAcquired;
import mchorse.metamorph.network.common.creative.PacketSyncMorph;
import mchorse.metamorph.network.common.survival.PacketRemoveMorph;
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
	public void clear()
	{
		super.clear();

		Dispatcher.sendToServer(new PacketClearAcquired());
	}

	@Override
	protected void addMorph(AbstractMorph morph)
	{
		super.addMorph(morph);

		Dispatcher.sendToServer(new PacketAcquireMorph(morph, false));
	}

	@Override
	public boolean isEditable(AbstractMorph morph)
	{
		return this.morphs.indexOf(morph) != -1;
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
	public boolean remove(AbstractMorph morph)
	{
		int index = this.morphs.indexOf(morph);
		boolean has = index != -1;

		if (has)
		{
			Dispatcher.sendToServer(new PacketRemoveMorph(index));
		}

		return has;
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
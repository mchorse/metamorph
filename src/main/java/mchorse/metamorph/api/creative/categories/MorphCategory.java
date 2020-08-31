package mchorse.metamorph.api.creative.categories;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.creative.PacketMorph;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MorphCategory
{
	public MorphSection parent;

	public String title;
	protected List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

	public MorphCategory(MorphSection parent, String title)
	{
		this.parent = parent;
		this.title = title;
	}

	@SideOnly(Side.CLIENT)
	public String getTitle()
	{
		return I18n.format("morph.category." + this.title);
	}

	public List<AbstractMorph> getMorphs()
	{
		return this.morphs;
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

	public void sort()
	{
		Collections.sort(this.morphs, (a, b) -> a.name.compareToIgnoreCase(b.name));
	}

	public final void add(AbstractMorph morph)
	{
		if (MorphManager.isBlacklisted(morph.name))
		{
			return;
		}

		MorphManager.INSTANCE.applySettings(morph);

		this.addMorph(morph);
	}

	protected void addMorph(AbstractMorph morph)
	{
		this.morphs.add(morph);
	}

	public boolean isEditable(AbstractMorph morph)
	{
		return false;
	}

	public void edit(AbstractMorph morph)
	{}

	public boolean remove(AbstractMorph morph)
	{
		return this.morphs.remove(morph);
	}

	public boolean keyTyped(EntityPlayer player, int keycode)
	{
		for (AbstractMorph morph : this.morphs)
		{
			if (morph.keybind == keycode && this.morph(player, morph))
			{
				return true;
			}
		}

		return false;
	}

	protected boolean morph(EntityPlayer player, AbstractMorph morph)
	{
		if (Metamorph.proxy.canUse(player))
		{
			Dispatcher.sendToServer(new PacketMorph(morph));

			return true;
		}

		return false;
	}
}

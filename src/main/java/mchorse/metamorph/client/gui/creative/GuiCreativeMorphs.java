package mchorse.metamorph.client.gui.creative;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.MorphList;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.sections.UserSection;
import mchorse.metamorph.api.events.ReloadMorphs;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.GuiMorphs;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Consumer;

public class GuiCreativeMorphs extends GuiMorphs
{
	public UserSection user;
	public GuiMorphSection userSection;

	private GuiCreativeMorphsList parent;

	public GuiCreativeMorphs(Minecraft mc, GuiCreativeMorphsList parent)
	{
		super(mc);

		this.parent = parent;
	}

	public void setupSections(GuiCreativeMorphsList menu, Consumer<GuiMorphSection> callback)
	{
		MorphList list = MorphManager.INSTANCE.list;

		list.update(this.mc.world);
		MinecraftForge.EVENT_BUS.post(new ReloadMorphs());

		this.sections.clear();
		this.removeAll();

		for (MorphSection section : list.sections)
		{
			GuiMorphSection element = section.getGUI(this.mc, menu, callback);

			if (section instanceof UserSection)
			{
				this.user = (UserSection) section;
				this.userSection = element;
			}

			element.flex();
			this.sections.add(element);
			this.add(element);
		}

		this.sections.get(this.sections.size() - 1).last = true;
	}

	@Override
	public void setSelected(AbstractMorph morph)
	{
		super.setSelected(morph);

		if (this.selected != null)
		{
			this.selected.reset();
		}

		if (morph != null)
		{
			AbstractMorph found = null;
			MorphCategory selectedCategory = null;
			GuiMorphSection selectedSection = null;

			searchForMorph:
			for (GuiMorphSection section : this.sections)
			{
				for (MorphCategory category : section.section.categories)
				{
					found = category.getEqual(morph);

					if (found != null)
					{
						selectedCategory = category;
						selectedSection = section;

						break searchForMorph;
					}
				}
			}

			if (found == null)
			{
				this.copyToRecent(morph);
			}
			else
			{
				this.selected = selectedSection;
				this.scrollTo();

				selectedSection.morph = found;
				selectedSection.category = selectedCategory;
				this.parent.pickMorph(found);
			}
		}
		else
		{
			this.selected = null;
		}
	}

	public void syncSelected()
	{
		AbstractMorph morph = this.getSelected();

		if (morph != null && this.selected != null && this.selected.category != null)
		{
			this.selected.category.edit(morph);
		}
	}

	public AbstractMorph copyToRecent(AbstractMorph morph)
	{
		if (this.selected != null)
		{
			this.selected.reset();
		}

		morph = morph.copy();

		this.user.recent.add(morph);
		this.selected = this.userSection;
		this.selected.morph = morph;
		this.selected.category = this.user.recent;
		this.parent.pickMorph(morph);

		this.scrollTo();

		return morph;
	}

	public boolean isSelectedMorphIsEditable()
	{
		return this.selected != null && this.selected.category != null && this.selected.category.isEditable(this.getSelected());
	}
}
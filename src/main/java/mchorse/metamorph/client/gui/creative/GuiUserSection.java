package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.sections.UserSection;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiUserSection extends GuiMorphSection
{
	public GuiUserSection(Minecraft mc, GuiCreativeMorphs parent, MorphSection section, Consumer<GuiMorphSection> callback)
	{
		super(mc, parent, section, callback);
	}

	@Override
	public GuiContextMenu createContextMenu(GuiContext context)
	{
		GuiSimpleContextMenu contextMenu = null;

		if (!(this.hoverCategory instanceof UserCategory))
		{
			GuiContextMenu superMenu = super.createContextMenu(context);

			if (superMenu instanceof GuiSimpleContextMenu)
			{
				contextMenu = (GuiSimpleContextMenu) superMenu;
			}
		}

		if (contextMenu == null)
		{
			contextMenu = new GuiSimpleContextMenu(this.mc);
		}

		contextMenu.action(Icons.ADD, "Add a new category", this::addCategory);

		if (this.hoverCategory instanceof UserCategory)
		{
			MorphCategory category = this.hoverCategory;

			contextMenu.action(Icons.EDIT, "Rename category", () -> this.renameCategory(category));
			contextMenu.action(Icons.REMOVE, "Remove category", () -> this.removeCategory(category));

			if (this.hoverMorph != null)
			{
				AbstractMorph morph = this.hoverMorph;

				contextMenu.action(Icons.CLOSE, "Remove morph", () -> category.remove(morph));
			}
		}

		return contextMenu;
	}

	private void addCategory()
	{
		UserSection section = (UserSection) this.section;

		section.add(new UserCategory(this.section, "User category"));
	}

	private void renameCategory(MorphCategory category)
	{
		for (IGuiElement element : this.parent.getChildren())
		{
			if (element instanceof GuiPromptModal)
			{
				return;
			}
		}

		GuiPromptModal modal = new GuiPromptModal(this.mc, "Give a new name to given category...", (string) ->
		{
			category.title = string;
			((UserSection) this.section).save();
		});

		modal.setValue(category.title);
		modal.flex().parent(this.parent.area).xy(0.5F, 0.5F).wh(160, 180).anchor(0.5F, 0.5F);
		modal.resize();

		this.parent.add(modal);
	}

	private void removeCategory(MorphCategory category)
	{
		UserSection section = (UserSection) this.section;

		section.remove(category);
	}
}
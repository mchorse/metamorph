package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.categories.AcquiredCategory;
import mchorse.metamorph.api.creative.categories.RecentCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.sections.UserSection;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.JsonToNBT;

import java.util.function.Consumer;

public class GuiUserSection extends GuiMorphSection
{
	public GuiUserSection(Minecraft mc, GuiCreativeMorphsList parent, MorphSection section, Consumer<GuiMorphSection> callback)
	{
		super(mc, parent, section, callback);
	}

	@Override
	public GuiContextMenu createContextMenu(GuiContext context)
	{
		GuiSimpleContextMenu contextMenu = (GuiSimpleContextMenu) super.createContextMenu(context);

		if (this.parent == null)
		{
			return contextMenu;
		}

		if (this.hoverCategory != null)
		{
			contextMenu.action(Icons.PASTE, IKey.lang("metamorph.gui.creative.context.paste"), () -> this.pasteMorph(this.hoverCategory));
		}

		contextMenu.action(Icons.ADD, IKey.lang("metamorph.gui.creative.context.add_category"), () -> section.add(new UserCategory(this.section, "User category")));

		if (this.hoverCategory instanceof UserCategory)
		{
			MorphCategory category = this.hoverCategory;

			contextMenu.action(Icons.EDIT, IKey.lang("metamorph.gui.creative.context.rename_category"), () -> this.renameCategory(category));
			contextMenu.action(Icons.REMOVE, IKey.lang("metamorph.gui.creative.context.remove_category"), () -> this.section.remove(category));
		}
		else if (this.hoverCategory instanceof RecentCategory)
		{
			MorphCategory category = this.hoverCategory;

			contextMenu.action(Icons.REMOVE, IKey.lang("metamorph.gui.creative.context.clear_category"), () ->
			{
				category.clear();
				this.parent.setSelected(null);
			});
		}
		else if (this.hoverCategory instanceof AcquiredCategory || this.hoverCategory instanceof UserCategory)
		{
			AbstractMorph morph = this.hoverMorph;

			if (morph != null)
			{
				contextMenu.action(Icons.REFRESH, IKey.lang("metamorph.gui.creative.context.to_recent"), () -> this.copyToRecent(morph));
			}
		}

		if (this.hoverMorph != null && this.hoverCategory != null)
		{
			MorphCategory category = this.hoverCategory;
			AbstractMorph morph = this.hoverMorph;

			contextMenu.action(Icons.CLOSE, IKey.lang("metamorph.gui.creative.context.remove_morph"), () -> category.remove(morph));
		}

		return contextMenu;
	}

	private void pasteMorph(MorphCategory category)
	{
		String clipboard = GuiScreen.getClipboardString();

		if (!GuiScreen.isCtrlKeyDown())
		{
			try
			{
				category.add(MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(clipboard)));

				return;
			}
			catch (Exception e)
			{}
		}

		GuiModal.addFullModal(this.parent, () ->
		{
			GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("metamorph.gui.creative.context.paste_modal"), (string) ->
			{
				try
				{
					category.add(MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(string)));
				}
				catch (Exception e)
				{}
			});

			modal.text.field.setMaxStringLength(100000);
			modal.setValue(clipboard);

			return modal;
		});
	}

	private void renameCategory(MorphCategory category)
	{
		GuiModal.addModal(this.parent, () ->
		{
			GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("metamorph.gui.creative.context.rename_category_modal"), (string) ->
			{
				category.title = string;
				((UserSection) this.section).save();
			});

			modal.setValue(category.getTitle());
			modal.flex().relative(this.parent).xy(0.5F, 0.5F).wh(160, 180).anchor(0.5F, 0.5F);

			return modal;
		});
	}

	private void copyToRecent(AbstractMorph morph)
	{
		((UserSection) this.section).recent.add(morph.copy());
	}
}
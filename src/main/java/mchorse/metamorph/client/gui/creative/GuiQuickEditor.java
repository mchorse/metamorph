package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Quick morph editor
 *
 * This GUI is responsible for providing quick access editing of the most used
 * fields in the morph editor
 */
public class GuiQuickEditor extends GuiElement
{
	public GuiCreativeMorphs parent;

	public GuiButtonElement presetsButton;
	public GuiButtonElement quickAccessButton;

	public GuiLabelListElement<NBTTagCompound> presets;
	public GuiScrollElement quickAccess;
	public GuiQuickEditor(Minecraft mc, GuiCreativeMorphs parent)
	{
		super(mc);

		this.parent = parent;

		this.presetsButton = new GuiButtonElement(mc, "Presets", this::toggleVisibility);
		this.quickAccessButton = new GuiButtonElement(mc, "Quick edit", this::toggleVisibility);

		this.presets = new GuiLabelListElement<NBTTagCompound>(mc, this::setPreset);
		this.presets.flex().relative(this.area).set(10, 30, 0, 0).w(1F, -20).h(1F, -40);

		this.quickAccess = new GuiScrollElement(mc);
		this.quickAccess.setVisible(false);
		this.quickAccess.flex().relative(this.area).y(20).w(1F).h(1F, -20).column(5).vertical().stretch().scroll().padding(10).height(20);

		GuiElement row = Elements.row(mc, 0, this.presetsButton, this.quickAccessButton);
		row.flex().relative(this.area).w(1F).h(20);

		this.add(row, this.presets, this.quickAccess);
	}

	private void toggleVisibility(GuiButtonElement button)
	{
		if (button == this.presetsButton)
		{
			this.presetsButton.setEnabled(false);
			this.quickAccessButton.setEnabled(true);

			this.presets.setVisible(true);
			this.quickAccess.setVisible(false);
		}
		else
		{
			this.presetsButton.setEnabled(true);
			this.quickAccessButton.setEnabled(false);

			this.presets.setVisible(false);
			this.quickAccess.setVisible(true);
		}
	}

	public void setMorph(AbstractMorph morph, GuiAbstractMorph<AbstractMorph> editor)
	{
		/* Fill quick access */
		this.quickAccess.clear();

		for (GuiElement element : editor.getFields(this.mc, this.parent, morph))
		{
			this.quickAccess.add(element);
		}

		/* Fill presets */
		List<Label<NBTTagCompound>> presets = editor.getPresets(morph);

		if (!presets.equals(this.presets.getList()))
		{
			this.presets.clear();
			this.presets.add(presets);
		}

		this.toggleVisibility(this.presets.isVisible() ? this.presetsButton : this.quickAccessButton);
		this.resize();
	}

	protected void setPreset(List<Label<NBTTagCompound>> label)
	{
		AbstractMorph morph = this.parent.getSelected().clone(true);

		morph.fromNBT(label.get(0).value);
		this.parent.setSelected(morph);
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0xaa000000);

		if (this.presets.isVisible() && this.presets.getList().isEmpty())
		{
			this.drawCenteredString(this.font, "No factory presets found...", this.presets.area.mx(), this.presets.area.my() - this.font.FONT_HEIGHT / 2, 0xffffff);
		}

		super.draw(context);
	}
}
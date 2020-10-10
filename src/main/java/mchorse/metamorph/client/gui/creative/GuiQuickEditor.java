package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelSearchListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Objects;

/**
 * Quick morph editor
 *
 * This GUI is responsible for providing quick access editing of the most used
 * fields in the morph editor
 */
public class GuiQuickEditor extends GuiElement
{
	public GuiCreativeMorphsList parent;

	public GuiButtonElement presetsButton;
	public GuiButtonElement randomPreset;
	public GuiButtonElement quickAccessButton;

	public GuiLabelSearchListElement<NBTTagCompound> presets;
	public GuiScrollElement quickAccess;

	private AbstractMorph last;

	public GuiQuickEditor(Minecraft mc, GuiCreativeMorphsList parent)
	{
		super(mc);

		this.parent = parent;

		this.presetsButton = new GuiButtonElement(mc, IKey.lang("metamorph.gui.creative.presets"), this::toggleVisibility);
		this.quickAccessButton = new GuiButtonElement(mc, IKey.lang("metamorph.gui.creative.quick"), this::toggleVisibility);

		this.presets = new GuiLabelSearchListElement<NBTTagCompound>(mc, this::setPreset);
		this.presets.flex().relative(this).set(10, 30, 0, 0).w(1F, -20).h(1F, -60);
		this.randomPreset = new GuiButtonElement(mc, IKey.lang("metamorph.gui.creative.random"), this::pickRandomPreset);
		this.randomPreset.flex().relative(this.presets).y(1F).w(1F);

		this.quickAccess = new GuiScrollElement(mc);
		this.quickAccess.setVisible(false);
		this.quickAccess.flex().relative(this).y(20).w(1F).h(1F, -20).column(5).vertical().stretch().scroll().padding(10).height(20);

		GuiElement row = Elements.row(mc, 0, this.presetsButton, this.quickAccessButton);
		row.flex().relative(this).w(1F).h(20);

		this.presets.add(this.randomPreset);
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

	private void pickRandomPreset(GuiButtonElement button)
	{
		int i = (int) (Math.random() * this.presets.list.getList().size());

		this.presets.list.setIndex(i);
		this.setPreset(this.presets.list.getCurrent());
	}

	public void setMorph(AbstractMorph morph, GuiAbstractMorph<AbstractMorph> editor)
	{
		if (Objects.equals(this.last, morph))
		{
			return;
		}

		/* Fill quick access */
		this.quickAccess.removeAll();

		for (GuiElement element : editor.getFields(this.mc, this.parent, morph))
		{
			this.quickAccess.add(element);
		}

		/* Fill presets */
		List<Label<NBTTagCompound>> presets = editor.getPresets(morph);

		this.presets.list.clear();
		this.presets.list.add(presets);
		this.presets.list.sort();
		this.presets.filter("", true);

		this.toggleVisibility(this.presets.isVisible() ? this.presetsButton : this.quickAccessButton);
		this.resize();

		this.last = morph;
	}

	protected void setPreset(List<Label<NBTTagCompound>> label)
	{
		if (!label.isEmpty())
		{
			this.parent.getSelected().fromNBT(label.get(0).value);
		}
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0xaa000000);

		if (this.presets.isVisible() && this.presets.list.getList().isEmpty())
		{
			this.drawCenteredString(this.font, "No factory presets found...", this.presets.area.mx(), this.presets.area.my() - this.font.FONT_HEIGHT / 2, 0xffffff);
		}

		super.draw(context);
	}
}
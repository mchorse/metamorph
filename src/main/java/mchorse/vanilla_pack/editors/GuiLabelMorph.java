package mchorse.vanilla_pack.editors;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.util.MMIcons;
import mchorse.vanilla_pack.editors.panels.GuiLabelPanel;
import mchorse.vanilla_pack.morphs.LabelMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiLabelMorph extends GuiAbstractMorph<LabelMorph>
{
	public GuiLabelPanel label;

	public GuiLabelMorph(Minecraft mc)
	{
		super(mc);

		this.defaultPanel = this.label = new GuiLabelPanel(mc, this);
		this.registerPanel(this.label, I18n.format("metamorph.gui.panels.label"), MMIcons.LABEL);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof LabelMorph;
	}
}
package mchorse.vanilla_pack.editors.panels;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.vanilla_pack.editors.GuiLabelMorph;
import mchorse.vanilla_pack.morphs.LabelMorph;
import net.minecraft.client.Minecraft;

public class GuiLabelPanel extends GuiMorphPanel<LabelMorph, GuiLabelMorph>
{
	public GuiScrollElement element;

	public GuiTextElement label;
	public GuiTrackpadElement max;
	public GuiTrackpadElement anchorX;
	public GuiTrackpadElement anchorY;
	public GuiColorElement color;

	public GuiToggleElement shadow;
	public GuiTrackpadElement shadowX;
	public GuiTrackpadElement shadowY;
	public GuiColorElement shadowColor;

	public GuiLabelPanel(Minecraft mc, GuiLabelMorph editor)
	{
		super(mc, editor);

		this.label = new GuiTextElement(mc, 10000, (label) -> this.morph.label = label);
		this.max = new GuiTrackpadElement(mc, (value) -> this.morph.max = value.intValue());
		this.max.limit(-1, Integer.MAX_VALUE, true).increment(10);
		this.anchorX = new GuiTrackpadElement(mc, (value) -> this.morph.anchorX = value);
		this.anchorX.values(0.01F);
		this.anchorY = new GuiTrackpadElement(mc, (value) -> this.morph.anchorY = value);
		this.anchorY.values(0.01F);
		this.color = new GuiColorElement(mc, (value) -> this.morph.color = value);

		this.shadow = new GuiToggleElement(mc, "Shadow", (button) -> this.morph.shadow = button.isToggled());
		this.shadowX = new GuiTrackpadElement(mc, (value) -> this.morph.shadowX = value);
		this.shadowX.limit(-100, 100).values(0.1F, 0.01F, 0.5F).increment(0.1F);
		this.shadowY = new GuiTrackpadElement(mc, (value) -> this.morph.shadowY = value);
		this.shadowY.limit(-100, 100).values(0.1F, 0.01F, 0.5F).increment(0.1F);
		this.shadowColor = new GuiColorElement(mc, (value) -> this.morph.shadowColor = value);

		this.element = new GuiScrollElement(mc);
		this.element.scroll.opposite = true;
		this.element.flex().relative(this).w(120).h(1F).column(5).vertical().stretch().scroll().height(20).padding(10);

		this.element.add(GuiLabel.create("Label", 16).anchor(0, 1F), this.label);
		this.element.add(GuiLabel.create("Max width", 16).anchor(0, 1F), this.max);
		this.element.add(GuiLabel.create("Anchor", 16).anchor(0, 1F), this.anchorX, this.anchorY);
		this.element.add(GuiLabel.create("Text color", 16).anchor(0, 1F), this.color);

		this.element.add(this.shadow);
		this.element.add(GuiLabel.create("Shadow offset", 16).anchor(0, 1F), this.shadowX, this.shadowY);
		this.element.add(GuiLabel.create("Shadow color", 16).anchor(0, 1F), this.shadowColor);

		this.add(this.element);
	}

	@Override
	public void fillData(LabelMorph morph)
	{
		super.fillData(morph);

		this.label.setText(morph.label);
		this.max.setValue(morph.max);
		this.anchorX.setValue(morph.anchorX);
		this.anchorY.setValue(morph.anchorY);
		this.color.picker.setColor(morph.color);

		this.shadow.toggled(morph.shadow);
		this.shadowX.setValue(morph.shadowX);
		this.shadowY.setValue(morph.shadowY);
		this.shadowColor.picker.setColor(morph.shadowColor);
	}
}
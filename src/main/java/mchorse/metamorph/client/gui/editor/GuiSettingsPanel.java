package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphSettings;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class GuiSettingsPanel extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
	public GuiButtonElement reset;
	public GuiTextElement displayName;
	public GuiStringListElement abilities;
	public GuiStringListElement attack;
	public GuiStringListElement action;
	public GuiTrackpadElement health;
	public GuiTrackpadElement speed;

	public GuiSettingsPanel(Minecraft mc, GuiAbstractMorph editor)
	{
		super(mc, editor);

		this.reset = new GuiButtonElement(mc, "Reset", (button) ->
		{
			this.morph.settings = MorphSettings.DEFAULT;

			MorphManager.INSTANCE.applySettings(this.morph);
			this.editor.setPanel(this.editor.defaultPanel);
		});
		this.displayName = new GuiTextElement(mc, (string) -> this.morph.displayName = string);
		this.abilities = new GuiStringListElement(mc, (values) ->
		{
			this.morph.settings.abilities.clear();

			for (String value : values)
			{
				IAbility ability = MorphManager.INSTANCE.abilities.get(value);

				if (ability != null)
				{
					this.morph.settings.abilities.add(ability);
				}
			}
		});
		this.abilities.multi().setBackground();
		this.attack = new GuiStringListElement(mc, (values) -> this.morph.settings.attack = MorphManager.INSTANCE.attacks.get(values.get(0)));
		this.attack.setBackground();
		this.action = new GuiStringListElement(mc, (values) -> this.morph.settings.action = MorphManager.INSTANCE.actions.get(values.get(0)));
		this.action.setBackground();
		this.health = new GuiTrackpadElement(mc, (value) -> this.morph.settings.health = value.intValue()).limit(0, Float.POSITIVE_INFINITY, true);
		this.speed = new GuiTrackpadElement(mc, (value) -> this.morph.settings.speed = value).limit(0, Float.POSITIVE_INFINITY);

		this.reset.resizer().parent(this.area).set(10, 0, 100, 20).y(1, -30);
		this.displayName.resizer().parent(this.area).set(10, 10, 100, 20);
		this.health.resizer().relative(this.displayName.getResizer()).y(1, 5).w(1, 0).h(20);
		this.speed.resizer().relative(this.health.getResizer()).y(1, 5).w(1, 0).h(20);
		this.speed.values(0.05F, 0.01F, 0.1F).increment(0.25F);
		this.abilities.resizer().relative(this.displayName.getResizer()).x(1, 5).w(1, 0).h(80);
		this.attack.resizer().relative(this.speed.getResizer()).y(1, 5).w(1, 0).h(80);
		this.action.resizer().relative(this.attack.getResizer()).y(1, 5).w(1, 0).h(80);

		this.add(this.displayName, this.abilities, this.attack, this.action, this.health, this.speed, this.reset);
	}

	@Override
	public void fillData(AbstractMorph morph)
	{
		super.fillData(morph);

		this.abilities.add(MorphManager.INSTANCE.abilities.keySet());
		this.attack.add(MorphManager.INSTANCE.attacks.keySet());
		this.action.add(MorphManager.INSTANCE.actions.keySet());
	}

	@Override
	public void startEditing()
	{
		super.startEditing();

		if (!morph.hasCustomSettings())
		{
			MorphSettings settings = morph.settings;

			morph.settings = new MorphSettings();
			morph.settings.merge(settings);
		}

		this.displayName.setText(morph.displayName);
		this.health.setValue(morph.settings.health);
		this.speed.setValue(morph.settings.speed);

		List<String> abilities = new ArrayList<String>();

		for (IAbility ability : morph.settings.abilities)
		{
			String key = MorphSettings.getKey(MorphManager.INSTANCE.abilities, ability);

			if (key != null)
			{
				abilities.add(key);
			}
		}

		this.abilities.sort();
		this.attack.sort();
		this.action.sort();

		this.abilities.setCurrent(abilities);
		this.attack.setCurrent(MorphSettings.getKey(MorphManager.INSTANCE.attacks, morph.settings.attack));
		this.action.setCurrent(MorphSettings.getKey(MorphManager.INSTANCE.actions, morph.settings.action));
	}
}
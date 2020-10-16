package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphSettings;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class GuiSettingsPanel extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
	public GuiScrollElement element;

	public GuiButtonElement reset;
	public GuiTextElement displayName;
	public GuiStringListElement abilities;
	public GuiStringListElement attack;
	public GuiStringListElement action;
	public GuiTrackpadElement health;
	public GuiTrackpadElement speed;

	public GuiTextElement data;
	public boolean error;

	public GuiSettingsPanel(Minecraft mc, GuiAbstractMorph editor)
	{
		super(mc, editor);

		this.element = new GuiScrollElement(mc);
		this.element.scroll.opposite = true;
		this.element.flex().relative(this).w(120).h(1F).column(5).vertical().stretch().scroll().height(20).padding(10);

		this.reset = new GuiButtonElement(mc, IKey.lang("metamorph.gui.editor.reset"), (button) ->
		{
			this.morph.settings = MorphSettings.DEFAULT;

			MorphManager.INSTANCE.applySettings(this.morph);
			this.editor.setPanel(this.editor.defaultPanel);
		});
		this.displayName = new GuiTextElement(mc, (string) -> this.morph.displayName = string);
		this.abilities = new GuiStringListElement(mc, (values) ->
		{
			this.ensureCustomSettings();
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
		this.abilities.multi().background().tooltip(IKey.lang("metamorph.gui.editor.abilities_tooltip"));
		this.attack = new GuiStringListElement(mc, (values) ->
		{
			this.ensureCustomSettings();
			this.morph.settings.attack = MorphManager.INSTANCE.attacks.get(values.get(0));
		});
		this.attack.background();
		this.action = new GuiStringListElement(mc, (values) ->
		{
			this.ensureCustomSettings();
			this.morph.settings.action = MorphManager.INSTANCE.actions.get(values.get(0));
		});
		this.action.background();
		this.health = new GuiTrackpadElement(mc, (value) ->
		{
			this.ensureCustomSettings();
			this.morph.settings.health = value.intValue();
		})
			.limit(0, Float.POSITIVE_INFINITY, true);
		this.speed = new GuiTrackpadElement(mc, (value) ->
		{
			this.ensureCustomSettings();
			this.morph.settings.speed = value.floatValue();
		})
			.limit(0, Float.POSITIVE_INFINITY)
			.values(0.05F, 0.01F, 0.1F)
			.increment(0.25F);
		this.data = new GuiTextElement(mc, 1000000, this::editNBT);

		this.abilities.flex().h(80);
		this.attack.flex().h(80);
		this.action.flex().h(80);

		this.data.flex().relative(this).relative(this.element).x(1F, 10).y(1, -30).wTo(this.flex(), 1F, -10);

		this.element.add(this.reset);
		this.element.add(Elements.label(IKey.lang("metamorph.gui.editor.display_name"), 16).anchor(0, 1F), this.displayName);
		this.element.add(Elements.label(IKey.lang("metamorph.gui.editor.health"), 16).anchor(0, 1F), this.health);
		this.element.add(Elements.label(IKey.lang("metamorph.gui.editor.speed"), 16).anchor(0, 1F), this.speed);
		this.element.add(Elements.label(IKey.lang("metamorph.gui.editor.abilities"), 16).anchor(0, 1F), this.abilities);
		this.element.add(Elements.label(IKey.lang("metamorph.gui.editor.attack"), 16).anchor(0, 1F), this.attack);
		this.element.add(Elements.label(IKey.lang("metamorph.gui.editor.action"), 16).anchor(0, 1F), this.action);

		this.add(this.element, this.data);
	}

	private void ensureCustomSettings()
	{
		if (!this.morph.hasCustomSettings())
		{
			MorphSettings old = this.morph.settings;

			this.morph.settings = new MorphSettings();
			this.morph.settings.copy(old);
		}
	}

	@Override
	public void fillData(AbstractMorph morph)
	{
		super.fillData(morph);

		this.abilities.clear();
		this.abilities.add(MorphManager.INSTANCE.abilities.keySet());
		this.attack.clear();
		this.attack.add(MorphManager.INSTANCE.attacks.keySet());
		this.action.clear();
		this.action.add(MorphManager.INSTANCE.actions.keySet());
	}

	public void updateNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();

		this.morph.toNBT(tag);
		this.data.setText(tag.toString());
	}

	public void editNBT(String str)
	{
		try
		{
			this.morph.fromNBT(JsonToNBT.getTagFromJson(str));
			this.error = false;
		}
		catch (Exception e)
		{
			this.error = true;
		}
	}

	@Override
	public void startEditing()
	{
		super.startEditing();

		this.error = false;

		this.updateNBT();

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

	@Override
	public void draw(GuiContext context)
	{
		super.draw(context);

		if (this.data.isVisible())
		{
			this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.nbt_data"), this.data.area.x, this.data.area.y - 12, this.error ? 0xffff3355 : 0xffffff);
		}
	}
}
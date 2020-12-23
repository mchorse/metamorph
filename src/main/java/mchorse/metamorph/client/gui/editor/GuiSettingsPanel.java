package mchorse.metamorph.client.gui.editor;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
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
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class GuiSettingsPanel extends GuiMorphPanel<AbstractMorph, GuiAbstractMorph>
{
	public GuiScrollElement left;

	public GuiKeybindElement keybind;
	public GuiButtonElement reset;
	public GuiTextElement displayName;
	public GuiStringListElement abilities;
	public GuiStringListElement attack;
	public GuiStringListElement action;
	public GuiTrackpadElement health;
	public GuiTrackpadElement speed;

	public GuiScrollElement right;

	public GuiToggleElement hitboxEnabled;
	public GuiTrackpadElement hitboxWidth;
	public GuiTrackpadElement hitboxHeight;
	public GuiTrackpadElement hitboxSneakingHeight;
	public GuiTrackpadElement hitboxEyePosition;

	public GuiTextElement data;
	public boolean error;

	public GuiSettingsPanel(Minecraft mc, GuiAbstractMorph editor)
	{
		super(mc, editor);

		this.left = new GuiScrollElement(mc);
		this.left.scroll.opposite = true;
		this.left.cancelScrollEdge();
		this.left.flex().relative(this).w(130).h(1F).column(5).vertical().stretch().scroll().height(20).padding(10);

		this.keybind = new GuiKeybindElement(mc, (key) ->
		{
			if (key == Keyboard.KEY_ESCAPE)
			{
				this.morph.keybind = -1;
				this.keybind.setKeybind(-1);
			}
			else
			{
				this.morph.keybind = key;
			}
		});
		this.keybind.tooltip(IKey.lang("metamorph.gui.editor.keybind_tooltip"));
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

		this.data.flex().relative(this).relative(this.left).x(1F, 10).y(1, -30).wTo(this.flex(), 1F, -10);

		this.left.add(this.reset);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.keybind"), 16).anchor(0, 1F), this.keybind);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.display_name"), 16).anchor(0, 1F), this.displayName);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.health"), 16).anchor(0, 1F), this.health);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.speed"), 16).anchor(0, 1F), this.speed);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.abilities"), 16).anchor(0, 1F), this.abilities);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.attack"), 16).anchor(0, 1F), this.attack);
		this.left.add(Elements.label(IKey.lang("metamorph.gui.editor.action"), 16).anchor(0, 1F), this.action);

		this.right = new GuiScrollElement(mc);
		this.right.flex().relative(this).x(1F).w(130).h(1F).anchorX(1F).column(5).vertical().stretch().scroll().height(20).padding(10);

		this.hitboxEnabled = new GuiToggleElement(mc, IKey.lang("metamorph.gui.editor.hitbox.enabled"), (b) -> this.morph.hitbox.enabled = b.isToggled());
		this.hitboxWidth = new GuiTrackpadElement(mc, (value) -> this.morph.hitbox.width = value.floatValue());
		this.hitboxWidth.limit(0.01, Integer.MAX_VALUE).tooltip(IKey.lang("metamorph.gui.editor.hitbox.width"));
		this.hitboxHeight = new GuiTrackpadElement(mc, (value) -> this.morph.hitbox.height = value.floatValue());
		this.hitboxHeight.limit(0.01, Integer.MAX_VALUE).tooltip(IKey.lang("metamorph.gui.editor.hitbox.height"));
		this.hitboxSneakingHeight = new GuiTrackpadElement(mc, (value) -> this.morph.hitbox.sneakingHeight = value.floatValue());
		this.hitboxSneakingHeight.limit(0.01, Integer.MAX_VALUE).tooltip(IKey.lang("metamorph.gui.editor.hitbox.sneaking_height"));
		this.hitboxEyePosition = new GuiTrackpadElement(mc, (value) -> this.morph.hitbox.eye = value.floatValue());
		this.hitboxEyePosition.limit(0.01, Integer.MAX_VALUE).tooltip(IKey.lang("metamorph.gui.editor.hitbox.eye_tooltip"));

		this.right.add(this.hitboxEnabled);
		this.right.add(Elements.label(IKey.lang("metamorph.gui.editor.hitbox.size")), this.hitboxWidth, this.hitboxHeight, this.hitboxSneakingHeight);
		this.right.add(Elements.label(IKey.lang("metamorph.gui.editor.hitbox.eye")), this.hitboxEyePosition);

		this.add(this.left, this.right, this.data);
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

		this.hitboxEnabled.toggled(morph.hitbox.enabled);
		this.hitboxWidth.setValue(morph.hitbox.width);
		this.hitboxHeight.setValue(morph.hitbox.height);
		this.hitboxSneakingHeight.setValue(morph.hitbox.sneakingHeight);
		this.hitboxEyePosition.setValue(morph.hitbox.eye);
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

		this.keybind.setKeybind(morph.keybind);
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
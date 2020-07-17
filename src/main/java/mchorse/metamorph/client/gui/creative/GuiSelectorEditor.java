package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Timer;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.render.EntitySelector;
import mchorse.metamorph.client.EntityModelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;

import java.util.List;
import java.util.function.Consumer;

public class GuiSelectorEditor extends GuiElement
{
	public GuiListElement<EntitySelector> selectors;

	public GuiElement form;
	public GuiTextElement name;
	public GuiTextElement type;
	public GuiTextElement match;
	public GuiToggleElement active;
	public GuiButtonElement pick;

	private EntitySelector selector;
	private Timer timer = new Timer(200);
	private boolean selecting;
	private boolean menu;

	public GuiSelectorEditor(Minecraft mc)
	{
		this(mc, false);
	}

	public GuiSelectorEditor(Minecraft mc, boolean menu)
	{
		super(mc);

		this.menu = menu;

		this.selectors = new GuiSelectorListElement(mc, this::fillData);
		this.selectors.sorting().background(0xff000000).setList(EntityModelHandler.selectors);
		this.selectors.context(() ->
		{
			GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(mc).action(Icons.ADD, IKey.lang("metamorph.gui.selectors.add"), this::addSelector);

			if (!this.selectors.getCurrent().isEmpty())
			{
				contextMenu.action(Icons.REMOVE, IKey.lang("metamorph.gui.selectors.remove"), this::removeSelector);
			}

			return contextMenu;
		});

		this.form = new GuiElement(mc);
		this.name = new GuiTextElement(mc, 1000, (name) ->
		{
			this.selector.name = name;
			this.selector.updateTime();
			this.timer.mark();
		});
		this.type = new GuiTextElement(mc, 1000, (name) ->
		{
			this.selector.type = name;
			this.selector.updateTime();
			this.timer.mark();
		});
		this.match = new GuiTextElement(mc, 10000, (value) ->
		{
			try
			{
				this.selector.match = JsonToNBT.getTagFromJson(value);
				this.selector.updateTime();
				this.timer.mark();
			}
			catch (Exception e)
			{}
		});
		this.match.tooltip(IKey.lang("metamorph.gui.selectors.match_tooltip"));
		this.active = new GuiToggleElement(mc, IKey.lang("metamorph.gui.selectors.enabled"), (toggle) ->
		{
			this.selector.enabled = toggle.isToggled();
			this.selector.updateTime();
			this.timer.mark();
		});
		this.pick = new GuiButtonElement(mc, IKey.lang("metamorph.gui.body_parts.pick"), (button) ->
		{
			this.selecting = true;
			button.setEnabled(false);
		});

		this.form.flex().relative(this).w(1F).column(5).vertical().stretch().height(20).padding(10);
		this.selectors.flex().relative(this.form).y(1F).w(1F).hTo(this.flex(), 1F);

		GuiLabel title = Elements.label(IKey.lang("metamorph.gui.selectors.title"), this.font.FONT_HEIGHT);
		GuiLabel name = Elements.label(IKey.lang("metamorph.gui.selectors.name"), 16).anchor(0, 1);
		GuiLabel type = Elements.label(IKey.lang("metamorph.gui.selectors.type"), 16).anchor(0, 1);
		GuiLabel match = Elements.label(IKey.lang("metamorph.gui.selectors.match"), 16).anchor(0, 1);

		this.form.add(title.tooltip(IKey.lang("metamorph.gui.selectors.tooltip")), name, this.name, type, this.type, match, this.match, this.active);

		if (!this.menu)
		{
			this.form.add(this.pick);
		}

		this.markContainer().add(this.form, this.selectors);

		this.selectors.setIndex(0);
		this.fillData(this.selectors.getCurrent());
	}

	private void addSelector()
	{
		EntityModelHandler.selectors.add(new EntitySelector());
		this.selectors.update();
		this.timer.mark();

		this.selectors.setIndex(this.selectors.getList().size() - 1);
		this.fillData(this.selectors.getCurrent());
	}

	private void removeSelector()
	{
		if (!this.selectors.current.isEmpty())
		{
			EntitySelector selector = this.selectors.getCurrent().get(0);

			selector.name = "";
			selector.type = "";
			selector.morph = null;
			selector.updateTime();

			int current = this.selectors.current.get(0);

			EntityModelHandler.selectors.remove(current);
			this.selectors.setIndex(current - 1);
			this.fillData(this.selectors.getCurrent());
			this.selectors.update();
			this.timer.mark();
		}
	}

	private void fillData(List<EntitySelector> selectors)
	{
		this.selector = null;
		this.selecting = false;
		this.form.setVisible(!selectors.isEmpty());
		this.pick.setEnabled(true);

		if (selectors.isEmpty())
		{
			return;
		}

		EntitySelector selector = selectors.get(0);

		this.selector = selector;
		this.name.setText(selector.name);
		this.type.setText(selector.type);
		this.match.setText(selector.match == null ? "" : selector.match.toString());
		this.active.toggled(selector.enabled);
	}

	public void setMorph(AbstractMorph morph)
	{
		if (!this.isVisible() || this.selector == null)
		{
			return;
		}

		if (this.selecting || this.menu)
		{
			this.selector.morph = morph == null ? null : morph.toNBT();
		}

		this.pick.setEnabled(true);
		this.selecting = false;
		this.selector.updateTime();
		this.timer.mark();
	}

	@Override
	public void draw(GuiContext context)
	{
		if (this.timer.checkReset())
		{
			ClientProxy.models.saveSelectors();
		}

		this.area.draw(0xaa000000);

		super.draw(context);

		if (this.selectors.getList().isEmpty())
		{
			this.drawCenteredString(this.font, "Right click here...", this.selectors.area.mx(), this.selectors.area.my(), 0x888888);
		}
	}

	public static class GuiSelectorListElement extends GuiListElement<EntitySelector>
	{
		public GuiSelectorListElement(Minecraft mc, Consumer<List<EntitySelector>> callback)
		{
			super(mc, callback);

			this.scroll.scrollItemSize = 16;
		}

		@Override
		protected String elementToString(EntitySelector element)
		{
			return element.name + " (" + element.type + ") - " + (element.morph == null ? "null" : element.morph.getString("Name"));
		}
	}
}
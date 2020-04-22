package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.Timer;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.render.EntitySelector;
import mchorse.metamorph.client.EntityModelHandler;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

public class GuiSelectorEditor extends GuiElement
{
	public GuiListElement<EntitySelector> selectors;

	public GuiElement form;
	public GuiTextElement name;
	public GuiTextElement type;
	public GuiToggleElement active;
	public GuiButtonElement pick;

	private EntitySelector selector;
	private Timer timer = new Timer(200);
	private boolean selecting;

	public GuiSelectorEditor(Minecraft mc)
	{
		super(mc);

		this.selectors = new GuiSelectorListElement(mc, this::fillData);
		this.selectors.sorting().background(0xff000000).setList(EntityModelHandler.selectors);
		this.selectors.context(() ->
		{
			GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc).action(Icons.ADD, "Add a selector", this::addSelector);

			if (!this.selectors.getCurrent().isEmpty())
			{
				menu.action(Icons.REMOVE, "Remove a selector", this::removeSelector);
			}

			return menu;
		});

		this.form = new GuiElement(mc);
		this.name = new GuiTextElement(mc, (name) ->
		{
			this.selector.name = name;
			this.selector.updateTime();
			this.timer.mark();
		});
		this.type = new GuiTextElement(mc, (name) ->
		{
			this.selector.type = name;
			this.selector.updateTime();
			this.timer.mark();
		});
		this.active = new GuiToggleElement(mc, "Enabled", (toggle) ->
		{
			this.selector.enabled = toggle.isToggled();
			this.selector.updateTime();
			this.timer.mark();
		});
		this.pick = new GuiButtonElement(mc, "Pick morph", (button) ->
		{
			this.selecting = true;
			button.setEnabled(false);
		});

		this.form.flex().relative(this).w(1F).column(5).vertical().stretch().height(20).padding(10);
		this.selectors.flex().relative(this.form).y(1F).w(1F).hTo(this.flex(), 1F);

		GuiLabel title = GuiLabel.create("Entity Selectors", this.font.FONT_HEIGHT);
		GuiLabel name = GuiLabel.create("Name", 16).anchor(0, 1);
		GuiLabel type = GuiLabel.create("Type", 16).anchor(0, 1);

		this.form.add(title.tooltip("With this feature, you can add morphs to entities by specific name or their type...", Direction.BOTTOM), name, this.name, type, this.type, this.active,this.pick);
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
		this.active.toggled(selector.enabled);
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

	public void setMorph(AbstractMorph morph)
	{
		if (!this.isVisible() || this.selector == null)
		{
			return;
		}

		if (this.selecting && this.selector != null)
		{
			this.selector.morph = MorphUtils.copy(morph);
		}

		this.pick.setEnabled(true);
		this.selecting = false;
		this.selector.updateTime();
		this.timer.mark();
	}

	public static class GuiSelectorListElement extends GuiListElement<EntitySelector>
	{
		public GuiSelectorListElement(Minecraft mc, Consumer<List<EntitySelector>> callback)
		{
			super(mc, callback);

			this.scroll.scrollItemSize = 16;
		}

		@Override
		protected String elementToString(EntitySelector element, int i, int x, int y, boolean hover, boolean selected)
		{
			return element.name + " (" + element.type + ") - " + (element.morph == null ? "null" : element.morph.getDisplayName());
		}
	}
}
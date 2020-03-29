package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.function.Consumer;

public class GuiMorphSection extends GuiElement
{
	public static final int HEADER_HEIGHT = 20;
	public static final int CATEGORY_HEIGHT = 16;

	public GuiCreativeMorphs parent;
	public MorphSection section;
	public Consumer<GuiMorphSection> callback;

	public boolean toggled = true;
	public int cellWidth = 55;
	public int cellHeight = 70;

	public AbstractMorph morph;
	public MorphCategory category;

	protected AbstractMorph hoverMorph;
	protected MorphCategory hoverCategory;

	public GuiMorphSection(Minecraft mc, GuiCreativeMorphs parent, MorphSection section, Consumer<GuiMorphSection> callback)
	{
		super(mc);

		this.parent = parent;
		this.section = section;
		this.callback = callback;
	}

	public GuiMorphSection size(int w, int h)
	{
		this.cellWidth = w;
		this.cellHeight = h;

		return this;
	}

	public void reset()
	{
		this.morph = null;
		this.category = null;
	}

	public int getPerRow()
	{
		return Math.max(this.area.w / this.cellWidth, 1);
	}

	public int getCategoryHeight(MorphCategory category)
	{
		int size = Math.max(category.morphs.size(), 1);

		return (int) Math.ceil(size / (float) this.getPerRow()) * this.cellHeight;
	}

	public int calculateHeight()
	{
		int h = HEADER_HEIGHT;

		for (MorphCategory category : this.section.categories)
		{
			if (category.isHidden())
			{
				continue;
			}

			h += CATEGORY_HEIGHT;
			h += this.getCategoryHeight(category);
		}

		return h;
	}

	public int calculateY(AbstractMorph morph)
	{
		int row = this.getPerRow();
		int h = HEADER_HEIGHT;

		for (MorphCategory category : this.section.categories)
		{
			if (category.isHidden())
			{
				continue;
			}

			h += CATEGORY_HEIGHT;

			for (int i = 0; i < category.morphs.size(); i ++)
			{
				AbstractMorph child = category.morphs.get(i);

				if (child == morph)
				{
					return h + (i / row) * this.cellHeight;
				}
			}

			h += this.getCategoryHeight(category);
		}

		return -1;
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.area.isInside(context.mouseX, context.mouseY) && context.mouseButton == 0)
		{
			if (context.mouseY - this.area.y < HEADER_HEIGHT)
			{
				this.toggled = !this.toggled;

				return true;
			}

			int x = context.mouseX - this.area.x;
			int y = context.mouseY - this.area.y - HEADER_HEIGHT;
			int row = this.getPerRow();

			for (MorphCategory category : this.section.categories)
			{
				if (category.isHidden())
				{
					continue;
				}

				y -= CATEGORY_HEIGHT;

				int ix = (int) (x / (this.area.w / (float) row));
				int iy = y / this.cellHeight;
				int i = ix + (y < 0 ? -1 : iy) * row;

				if (i >= 0 && i < category.morphs.size())
				{
					this.set(category.morphs.get(i), category);

					return true;
				}

				y -= this.getCategoryHeight(category);
			}

			this.set(null, null);
		}

		return false;
	}

	private void set(AbstractMorph morph, MorphCategory category)
	{
		this.morph = morph;
		this.category = category;

		if (this.callback != null)
		{
			this.callback.accept(this);
		}
	}

	@Override
	public GuiContextMenu createContextMenu(GuiContext context)
	{
		if (this.hoverMorph != null && this.parent.user.global.size() > 0)
		{
			GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(this.mc);
			AbstractMorph morph = this.hoverMorph;

			contextMenu.action(Icons.UPLOAD, "Add to global morphs...", () -> this.showGlobalMorphs(morph));

			return contextMenu;
		}

		return super.createContextMenu(context);
	}

	private void showGlobalMorphs(AbstractMorph morph)
	{
		GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(this.mc);

		for (UserCategory category : this.parent.user.global)
		{
			contextMenu.action(category.title, () ->
			{
				AbstractMorph added = morph.clone(true);

				category.add(added);
				this.parent.setSelected(added);
			});
		}

		GuiBase.getCurrent().replaceContextMenu(contextMenu);
	}

	@Override
	public void draw(GuiContext context)
	{
		Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + HEADER_HEIGHT, 0x88000000);

		this.font.drawStringWithShadow(this.section.title, this.area.x + 7, this.area.y + 10 - this.font.FONT_HEIGHT / 2, 0xffffff);
		(this.toggled ? Icons.MOVE_UP : Icons.MOVE_DOWN).render(this.area.ex() - 18 - 3, this.area.y + 10 + (this.toggled ? -1 : 1), 0, 0.5F);

		int y = this.drawMorphs(context);

		if (this.area.h != y)
		{
			this.flex().h(y);
			this.getParent().getParent().resize();
		}

		super.draw(context);
	}

	/**
	 * Draw morphs and return the final height
	 */
	protected int drawMorphs(GuiContext context)
	{
		int y = HEADER_HEIGHT;

		this.hoverMorph = null;
		this.hoverCategory = null;

		if (this.toggled)
		{
			int row = this.getPerRow();

			for (MorphCategory category : this.section.categories)
			{
				if (category.isHidden())
				{
					continue;
				}

				this.font.drawStringWithShadow(category.title, this.area.x + 7, this.area.y + y + 8 - this.font.FONT_HEIGHT / 2, 0xcccccc);

				Area.SHARED.copy(this.area);
				Area.SHARED.y = this.area.y + y;
				Area.SHARED.h = CATEGORY_HEIGHT + this.getCategoryHeight(category);

				if (Area.SHARED.isInside(context.mouseX, context.mouseY))
				{
					this.hoverCategory = category;
				}

				float x = 0;
				y += CATEGORY_HEIGHT;

				for (int i = 0; i < category.morphs.size(); i ++)
				{
					AbstractMorph morph = category.morphs.get(i);

					if (i != 0 && i % row == 0)
					{
						x = 0;
						y += this.cellHeight;
					}

					int mx = this.area.x + Math.round(x);
					int my = this.area.y + y;
					x += this.area.w / (float) row;
					int w = Math.round(x - (mx - this.area.x));

					Area.SHARED.set(mx, my, w, this.cellHeight);

					if (Area.SHARED.isInside(context.mouseX, context.mouseY))
					{
						this.hoverMorph = morph;
					}

					GuiDraw.scissor(mx, my, w, this.cellHeight, context);
					this.drawMorph(context, morph, mx, my, w, this.cellHeight);
					GuiDraw.unscissor(context);
				}

				y += this.cellHeight;
			}
		}

		return y;
	}

	/**
	 * Draw individual morph
	 */
	protected void drawMorph(GuiContext context, AbstractMorph morph, int x, int y, int w, int h)
	{
		if (this.morph == morph)
		{
			Gui.drawRect(x, y, x + w, y + h, 0xaa000000 + McLib.primaryColor.get());
		}
		else if (this.hoverMorph == morph)
		{
			Gui.drawRect(x, y, x + w, y + h, 0x44000000);
		}

		int spot = (int) (w * 0.4F);
		int spotX = x + w / 2;
		int spotY = y + h / 2;

		GuiDraw.drawDropCircleShadow(spotX, spotY, spot, (int) (spot * 0.65F), 10, 0x44000000, 0x00);

		morph.renderOnScreen(this.mc.player, x + w / 2, y + (int) (h * 0.7F), w * 0.4F, 1);

		if (this.morph == morph)
		{
			GuiDraw.drawOutline(x, y, x + w, y + h, 0xff000000 + McLib.primaryColor.get(), 2);
		}
	}
}
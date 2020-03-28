package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.metamorph.api.creative.MorphCategory;
import mchorse.metamorph.api.creative.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.function.Consumer;

public class GuiMorphSection extends GuiElement
{
	public MorphSection section;
	public Consumer<GuiMorphSection> callback;

	public boolean toggled = true;
	public int cellWidth = 55;
	public int cellHeight = 70;

	public AbstractMorph morph;
	public MorphCategory category;

	public GuiMorphSection(Minecraft mc, MorphSection section, Consumer<GuiMorphSection> callback)
	{
		super(mc);

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

	public int calculateHeight()
	{
		int row = this.area.w / this.cellWidth;
		int h = 20;

		for (MorphCategory category : this.section.categories)
		{
			if (category.morphs.isEmpty())
			{
				continue;
			}

			h += 16;
			h += (category.morphs.size() / row + 1) * this.cellHeight;
		}

		return h;
	}

	public int calculateY(AbstractMorph morph)
	{
		int row = this.area.w / this.cellWidth;
		int h = 20;

		for (MorphCategory category : this.section.categories)
		{
			if (category.morphs.isEmpty())
			{
				continue;
			}

			h += 16;

			for (int i = 0; i < category.morphs.size(); i ++)
			{
				AbstractMorph child = category.morphs.get(i);

				if (child == morph)
				{
					return h + (i / row) * this.cellHeight;
				}
			}

			h += (category.morphs.size() / row + 1) * this.cellHeight;
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

		if (this.area.isInside(context.mouseX, context.mouseY))
		{
			if (context.mouseY - this.area.y < 20)
			{
				this.toggled = !this.toggled;

				return true;
			}

			int x = context.mouseX - this.area.x;
			int y = context.mouseY - this.area.y - 20;
			int row = this.area.w / this.cellWidth;

			if (row == 0)
			{
				row = 1;
			}

			for (MorphCategory category : this.section.categories)
			{
				if (category.morphs.isEmpty())
				{
					continue;
				}

				y -= 16;

				int ix = (int) (x / (this.area.w / (float) row));
				int iy = y / this.cellHeight;

				if (y < 0)
				{
					iy = -1;
				}

				int i = ix + iy * row;

				if (i >= 0 && i < category.morphs.size())
				{
					this.morph = category.morphs.get(i);
					this.category = category;

					if (this.callback != null)
					{
						this.callback.accept(this);
					}

					return true;
				}

				y -= (category.morphs.size() / row + 1) * this.cellHeight;
			}

			this.morph = null;

			if (this.callback != null)
			{
				this.callback.accept(this);
			}
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 20, 0x88000000);

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
		int y = 20;

		if (this.toggled)
		{
			int row = this.area.w / this.cellWidth;

			if (row == 0)
			{
				row = 1;
			}

			for (MorphCategory category : this.section.categories)
			{
				if (category.morphs.isEmpty())
				{
					continue;
				}

				float x = 0;

				this.font.drawStringWithShadow(category.title, this.area.x + 7, this.area.y + y + 8 - this.font.FONT_HEIGHT / 2, 0xcccccc);
				y += 16;

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

		morph.renderOnScreen(this.mc.player, x + w / 2, y + (int) (h * 0.7F), w * 0.4F, 1);

		if (this.morph == morph)
		{
			GuiDraw.drawOutline(x, y, x + w, y + h, 0xff000000 + McLib.primaryColor.get(), 2);
		}
	}
}
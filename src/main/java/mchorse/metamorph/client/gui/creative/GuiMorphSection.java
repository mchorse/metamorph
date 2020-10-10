package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Keys;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.GuiMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class GuiMorphSection extends GuiElement
{
	public static final int HEADER_HEIGHT = 20;
	public static final int CATEGORY_HEIGHT = 16;

	public GuiCreativeMorphsList parent;
	public MorphSection section;
	public Consumer<GuiMorphSection> callback;

	public int cellWidth = 55;
	public int cellHeight = 70;
	public boolean last;
	public boolean favorite;

	public AbstractMorph morph;
	public MorphCategory category;

	protected AbstractMorph hoverMorph;
	protected MorphCategory hoverCategory;

	public int height;

	private String filter = "";

	public GuiMorphSection(Minecraft mc, GuiCreativeMorphsList parent, MorphSection section, Consumer<GuiMorphSection> callback)
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

	public void set(AbstractMorph morph, MorphCategory category)
	{
		this.morph = morph;
		this.category = category;
	}

	public void pick(AbstractMorph morph, MorphCategory category)
	{
		this.set(morph, category);

		if (this.callback != null)
		{
			this.callback.accept(this);
		}
	}

	public void reset()
	{
		this.set(null, null);
	}

	/* Searching methods */

	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	public boolean noFilter()
	{
		return this.filter.isEmpty() && !this.favorite;
	}

	public boolean isMatching(AbstractMorph morph)
	{
		if (this.favorite)
		{
			return morph.favorite;
		}

		if (this.filter.isEmpty())
		{
			return true;
		}

		return morph.name.toLowerCase().contains(this.filter) || morph.getDisplayName().toLowerCase().contains(this.filter);
	}

	public void calculateXY(GuiMorphs morphs)
	{
		int j = 0;

		for (AbstractMorph morph : this.category.getMorphs())
		{
			if (morph == this.morph)
			{
				int row = this.getPerRow();

				morphs.x = j % row;
				morphs.y = j / row;

				return;
			}

			if (this.isMatching(morph))
			{
				j ++;
			}
		}
	}

	public AbstractMorph getMorphAt(GuiMorphs morphs)
	{
		int row = this.getPerRow();
		int size = this.category.getMorphs().size();

		/* Shortcuts */
		if (morphs.y < 0 || size == 0)
		{
			return null;
		}
		else if (morphs.y < size / row + 1)
		{
			int i = morphs.x + morphs.y * row;

			if (i >= size)
			{
				return this.category.getMorphs().get(size - 1);
			}
		}

		/* Find the actual morph */
		int j = 0;

		if (morphs.x < 0) morphs.x = row - 1;
		if (morphs.x > row - 1) morphs.x = 0;

		for (AbstractMorph morph : this.category.getMorphs())
		{
			if (this.isMatching(morph))
			{
				if (j % row == morphs.x && j / row == morphs.y)
				{
					return morph;
				}

				j ++;
			}
		}

		return null;
	}

	public int getY(AbstractMorph selected)
	{
		if (this.section.categories.isEmpty())
		{
			return 0;
		}

		int y = HEADER_HEIGHT;
		int row = this.getPerRow();

		for (MorphCategory category : this.section.categories)
		{
			int count = this.getMorphsSize(category);

			if (category.isHidden() || (count == 0 && !this.noFilter()))
			{
				continue;
			}

			y += CATEGORY_HEIGHT + 5;

			for (int i = 0, j = 0; i < category.getMorphs().size(); i ++)
			{
				AbstractMorph morph = category.getMorphs().get(i);

				if (!this.isMatching(morph))
				{
					continue;
				}

				if (j != 0 && j % row == 0)
				{
					y += this.cellHeight;
				}

				if (morph == selected)
				{
					return y;
				}

				j ++;
			}

			y += this.cellHeight + 5;
		}

		return -1;
	}

	/* Calculation methods */

	public int getMorphsSize(MorphCategory category)
	{
		if (this.noFilter())
		{
			return category.getMorphs().size();
		}

		int count = 0;

		for (AbstractMorph morph : category.getMorphs())
		{
			count += this.isMatching(morph) ? 1 : 0;
		}

		return count;
	}

	public int getPerRow()
	{
		return Math.max(this.area.w / this.cellWidth, 1);
	}

	public int getCategoryHeight(MorphCategory category)
	{
		return this.getCategoryHeight(this.getMorphsSize(category));
	}

	public int getCategoryHeight(int given)
	{
		int size = Math.max(given, 1);

		return (int) Math.ceil(size / (float) this.getPerRow()) * this.cellHeight;
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		boolean result = false;

		if (this.area.isInside(context) && !this.section.categories.isEmpty())
		{
			if (context.mouseY - this.area.y < HEADER_HEIGHT && context.mouseButton == 0)
			{
				this.section.hidden = !this.section.hidden;

				return true;
			}

			int x = context.mouseX - this.area.x;
			int y = context.mouseY - this.area.y - HEADER_HEIGHT;
			int row = this.getPerRow();

			category:
			for (MorphCategory category : this.section.categories)
			{
				int count = this.getMorphsSize(category);

				if (category.isHidden() || (count == 0 && !this.noFilter()))
				{
					continue;
				}

				y -= CATEGORY_HEIGHT + 5;

				int ix = (int) (x / (this.area.w / (float) row));
				int iy = y / this.cellHeight;
				int i = ix + (y < 0 ? -1 : iy) * row;

				if (i >= 0 && i < count)
				{
					int real = category.getMorphs().size();

					if (count == real)
					{
						this.pick(category.getMorphs().get(i), category);

						result = true;

						break;
					}
					else
					{
						for (int j = 0, k = -1; j < real; j ++)
						{
							AbstractMorph morph = category.getMorphs().get(j);

							if (this.isMatching(morph))
							{
								k ++;
							}

							if (i == k)
							{
								this.pick(morph, category);

								result = true;
								break category;
							}
						}
					}
				}

				y -= this.getCategoryHeight(count) + 5;
			}

			if (!result)
			{
				this.pick(null, null);
			}
		}

		return super.mouseClicked(context) || result;
	}

	@Override
	public GuiContextMenu createContextMenu(GuiContext context)
	{
		if (this.parent == null)
		{
			return super.createContextMenu(context);
		}

		GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(this.mc);
		AbstractMorph morph = this.hoverMorph;

		if (morph != null)
		{
			if (!(this.hoverCategory instanceof UserCategory))
			{
				Runnable runnable = this.parent.showGlobalMorphs(morph);

				if (runnable != null)
				{
					contextMenu.action(Icons.UPLOAD, IKey.lang("metamorph.gui.creative.context.add_global"), runnable);
				}
			}

			contextMenu.action(Icons.EDIT, IKey.lang("metamorph.gui.creative.context.edit"), () -> this.parent.enterEditMorph(morph));
			contextMenu.action(Icons.COPY, IKey.lang("metamorph.gui.creative.context.copy"), () -> GuiScreen.setClipboardString(morph.toNBT().toString()));
		}

		return contextMenu;
	}

	@Override
	public void draw(GuiContext context)
	{
		int y = this.drawMorphs(context) + (this.last ? 30 : 0);

		if (this.area.h != y)
		{
			this.height = y;
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
		if (this.section.categories.isEmpty())
		{
			return 0;
		}

		/* Draw header */
		Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + HEADER_HEIGHT, 0xbb000000);

		this.font.drawStringWithShadow(this.section.getTitle(), this.area.x + 7, this.area.y + 10 - this.font.FONT_HEIGHT / 2, 0xffffff);
		(this.section.hidden ? Icons.MOVE_DOWN : Icons.MOVE_UP).render(this.area.ex() - 18 - 3, this.area.y + 10 + (this.section.hidden ? 1 : -1), 0, 0.5F);

		/* Draw categories */
		int y = HEADER_HEIGHT;

		this.hoverMorph = null;
		this.hoverCategory = null;

		if (this.section.hidden)
		{
			return y;
		}

		int row = this.getPerRow();

		for (MorphCategory category : this.section.categories)
		{
			int count = this.getMorphsSize(category);

			if (category.isHidden() || (count == 0 && !this.noFilter()))
			{
				continue;
			}

			GuiDraw.drawTextBackground(this.font, category.getTitle(), this.area.x + 7, this.area.y + y + 8 - this.font.FONT_HEIGHT / 2, 0xeeeeee, 0x88000000, 2);

			Area.SHARED.copy(this.area);
			Area.SHARED.y = this.area.y + y;
			Area.SHARED.h = CATEGORY_HEIGHT + this.getCategoryHeight(category);

			if (Area.SHARED.isInside(context.mouseX, context.mouseY))
			{
				this.hoverCategory = category;
			}

			float x = 0;
			y += CATEGORY_HEIGHT + 5;

			for (int i = 0, j = 0; i < category.getMorphs().size(); i ++)
			{
				AbstractMorph morph = category.getMorphs().get(i);

				if (!this.isMatching(morph))
				{
					continue;
				}

				if (j != 0 && j % row == 0)
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
				this.drawMorph(context, morph, mx, my, w, this.cellHeight, this.hoverMorph == morph, this.morph == morph);
				GuiDraw.unscissor(context);

				j ++;
			}

			y += this.cellHeight + 5;
		}

		return y;
	}

	/**
	 * Draw individual morph
	 */
	protected void drawMorph(GuiContext context, AbstractMorph morph, int x, int y, int w, int h, boolean hover, boolean selected)
	{
		if (selected && !morph.errorRendering)
		{
			Gui.drawRect(x, y, x + w, y + h, 0xaa000000 + McLib.primaryColor.get());
		}
		else if (hover)
		{
			Gui.drawRect(x, y, x + w, y + h, 0x66000000);
		}

		int spot = (int) (w * 0.4F);
		int spotX = x + w / 2;
		int spotY = y + h / 2;

		GuiDraw.drawDropCircleShadow(spotX, spotY, spot, (int) (spot * 0.65F), 10, 0x44000000, 0x00);

		if (morph.errorRendering)
		{
			GuiDraw.drawOutline(x, y, x + w, y + h, 0x88ff0000, 4);
			GuiDraw.drawOutline(x, y, x + w, y + h, 0xffff0000, 2);

			return;
		}

		if (!MorphUtils.renderOnScreen(morph, this.mc.player, x + w / 2, y + (int) (h * 0.7F), w * 0.4F, 1))
		{
			return;
		}

		if (selected)
		{
			GuiDraw.drawOutline(x, y, x + w, y + h, 0xff000000 + McLib.primaryColor.get(), 2);
		}

		if (morph.keybind != -1)
		{
			String key = Keys.getKeyName(morph.keybind);
			int kw = this.font.getStringWidth(key);
			int kx = x + w - 6 - kw;
			int ky = y + h - 6 - this.font.FONT_HEIGHT;

			Gui.drawRect(kx - 3, ky - 2, kx + kw + 3, ky + this.font.FONT_HEIGHT + 2, 0xff000000);
			this.font.drawStringWithShadow(key, kx, ky, 0xffffff);
		}

		if (morph.favorite)
		{
			/* Stupid hack because the morph seems to change the blend function or something */
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GuiDraw.drawOutlinedIcon(Icons.FAVORITE, x + 2, y + 2, 0xffffffff);
		}
	}
}
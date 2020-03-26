package mchorse.metamorph.client.gui.elements;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollArea.ScrollDirection;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Creative morph variant picker GUI subview
 * 
 * This class is responsible for displaying, scrolling through and 
 * picking the collapsed variants of morphs in {@link GuiCreativeMorphs}. 
 */
public class GuiCreativeVariantPicker extends GuiElement
{
    /**
     * Parent view 
     */
    public GuiCreativeMorphs morphs;

    /**
     * Previous morph was assigned in {@link #isActive()} 
     */
    public MorphCell previous;

    /**
     * Horizontal scroll area 
     */
    public ScrollArea scroll = new ScrollArea(40);

    public GuiCreativeVariantPicker(Minecraft mc, GuiCreativeMorphs morphs)
    {
        super(mc);

        this.morphs = morphs;
        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.scroll.scrollSpeed = 5;
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
    }

    /**
     * Returns whether this picker is active, meaning, it can rendered 
     * or clicked. The main condition for this:
     * 
     * - User must selected a morph in creative morphs
     * - There must be at least two morph variants
     */
    public boolean isActive()
    {
        MorphCell selected = this.morphs.getSelected();

        if (this.previous != selected)
        {
            this.scroll.scroll = 0;

            if (selected != null)
            {
                int size = selected.variants.size();
                int index = 0;

                if (size > 1)
                {
                    for (int i = 0; i < size; i++)
                    {
                        if (i == selected.selected)
                        {
                            break;
                        }

                        index += selected.variants.get(i).hidden ? 0 : 1;
                    }

                    this.scroll.scroll = index * 40;
                }
            }
        }

        this.previous = selected;

        return selected != null && selected.variants.size() > 1;
    }

    /**
     * When mouse clicked, it's either marks as scrolling, or selects 
     * the current morph variant to a different index. 
     */
    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context) || this.scroll.mouseClicked(context.mouseX, context.mouseY))
        {
            return true;
        }

        if (!this.scroll.isInside(context.mouseX, context.mouseY) || !this.isActive())
        {
            return false;
        }

        MorphCell selected = this.morphs.getSelected();
        int index = (context.mouseX - this.scroll.x + this.scroll.scroll) / 40;
        int i = 0;
        int j = 0;

        for (MorphVariant variant : selected.variants)
        {
            if (!variant.hidden)
            {
                if (i == index)
                {
                    selected.selected = j;
                    this.morphs.setMorph(this.morphs.getSelected().current().morph);

                    return true;
                }

                i++;
            }

            j++;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (this.isActive())
        {
            return this.scroll.mouseScroll(context.mouseX, context.mouseY, context.mouseWheel);
        }

        return super.mouseScrolled(context);
    }

    /**
     * When mouse released, just reset the scrolling flag 
     */
    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);
        this.scroll.mouseReleased(context.mouseX, context.mouseY);
    }

    /**
     * This method is responsible for drawing morph variants, scroll bar, 
     * selected morph bar, and also responsible for scrolling this view.
     */
    @Override
    public void draw(GuiContext context)
    {
        if (!this.isActive())
        {
            return;
        }

        MorphCell selected = this.morphs.getSelected();
        Minecraft mc = Minecraft.getMinecraft();

        EntityPlayer player = mc.player;
        GuiScreen screen = mc.currentScreen;

        if (selected != null && selected.hasVisible)
        {
            int w = this.scroll.w;
            int h = this.scroll.h;
            int x = this.scroll.x;
            int y = this.scroll.y;
            int i = 0;
            int j = 0;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 200);
            Gui.drawRect(x, y, x + w, y + h, 0xaa000000);

            GuiDraw.scissor(x, y, w, h, screen.width, screen.height);

            /* Draw morph variants within viewing range */
            for (MorphVariant variant : selected.variants)
            {
                boolean variantSelected = j == selected.selected;

                x = this.scroll.x + i * 40 - this.scroll.scroll;
                j++;

                if (variant.hidden)
                {
                    continue;
                }

                i++;

                if (x < this.scroll.x - 40 || x >= this.scroll.x + this.scroll.w)
                {
                    continue;
                }

                variant.morph.renderOnScreen(player, x + 20, y + (variantSelected ? 42 : 40), variantSelected ? 24F : 18F, 1F);

                if (variantSelected)
                {
                    Gui.drawRect(x, y, x + 40, y + 2, 0xaaffffff);
                }
            }

            GuiDraw.unscissor();
            GlStateManager.popMatrix();

            /* Scroll bar */
            this.scroll.scrollSize = i * 40;
            this.scroll.clamp();
            this.scroll.drag(context.mouseX, context.mouseY);
            this.scroll.drawScrollbar();
        }
    }
}
package mchorse.metamorph.client.gui.elements;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphVariant;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Creative morph variant picker GUI subview
 * 
 * This class is responsible for displaying, scrolling through and 
 * picking the collapsed variants of morphs in {@link GuiCreativeMorphs}. 
 */
public class GuiCreativeVariantPicker
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
     * Whether the user currently scrolling 
     */
    public boolean scrolling;

    /**
     * Scrolling offset 
     */
    public int scroll;

    public GuiCreativeVariantPicker(GuiCreativeMorphs morphs)
    {
        this.morphs = morphs;
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
            this.scroll = 0;

            if (selected != null && selected.variants.size() > 1)
            {
                int maxWidth = selected.variants.size() * 40 - this.morphs.w;

                if (maxWidth > 0)
                {
                    this.scroll = selected.selected * 40;
                    this.scroll = MathHelper.clamp_int(this.scroll, 0, maxWidth);
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
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseY >= this.morphs.y + this.morphs.h - 10)
        {
            this.scrolling = true;

            return;
        }

        MorphCell selected = this.morphs.getSelected();
        int index = (mouseX - this.morphs.x + this.scroll) / 40;
        int i = 0;
        int j = 0;

        for (MorphVariant variant : selected.variants)
        {
            if (!variant.hidden)
            {
                if (i == index)
                {
                    selected.selected = j;

                    break;
                }

                i++;
            }

            j++;
        }
    }

    /**
     * When mouse released, just reset the scrolling flag 
     */
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.scrolling = false;
    }

    /**
     * This method is responsible for drawing morph variants, scroll bar, 
     * selected morph bar, and also responsible for scrolling this view.
     */
    public void drawPane(int mouseX, int mouseY, float partialTicks)
    {
        MorphCell selected = this.morphs.getSelected();
        EntityPlayer player = this.morphs.mc.thePlayer;

        if (selected != null)
        {
            int w = this.morphs.w;
            int h = this.morphs.h;
            int x = this.morphs.x;
            int y = this.morphs.y + h - GuiCreativeMorphs.CELL_HEIGHT;
            int i = 0;
            int j = 0;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 200);
            Gui.drawRect(x, y, x + w, y + GuiCreativeMorphs.CELL_HEIGHT, 0xaa000000);

            GuiUtils.scissor(x, y, w, h, this.morphs.width, this.morphs.height);

            /* Draw morph variants within viewing range */
            for (MorphVariant variant : selected.variants)
            {
                boolean variantSelected = j == selected.selected;

                x = this.morphs.x + i * 40 - this.scroll;
                j++;

                if (variant.hidden)
                {
                    continue;
                }

                i++;

                if (x < this.morphs.x - 40 || x >= this.morphs.x + this.morphs.w)
                {
                    continue;
                }

                variant.morph.renderOnScreen(player, x + 20, y + (variantSelected ? 42 : 40), variantSelected ? 24F : 18F, 1F);

                if (variantSelected)
                {
                    Gui.drawRect(x, y, x + 40, y + 2, 0xaaffffff);
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();

            int maxWidth = i * 40 - this.morphs.w;

            /* Scroll the view */
            if (this.scrolling)
            {
                float factor = (mouseX - (float) this.morphs.x) / this.morphs.w;

                this.scroll = (int) (factor * maxWidth);
            }

            this.scroll = MathHelper.clamp_int(this.scroll, 0, maxWidth < 0 ? 0 : maxWidth);

            /* Draw scrollbar */
            if (maxWidth > 0)
            {
                float factor = this.scroll / (float) maxWidth;

                int sx = this.morphs.x + (int) (factor * (this.morphs.w - 20));

                Gui.drawRect(this.morphs.x, y + 50, this.morphs.x + this.morphs.w, y + 60, 0xcc000000);
                Gui.drawRect(sx, y + 50, sx + 20, y + 60, 0xffffffff);
            }
        }
    }
}
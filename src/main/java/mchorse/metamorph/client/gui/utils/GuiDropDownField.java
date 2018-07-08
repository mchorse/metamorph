package mchorse.metamorph.client.gui.utils;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

/**
 * Drop down list widget
 */
public class GuiDropDownField
{
    /* Field's frame */
    public int x;
    public int y;
    public int w;
    public int h;

    /**
     * Maximum height of the drop down part itself 
     */
    public int maxHeight = 80;

    /**
     * Values that can be chosen from this drop down
     */
    public List<DropDownItem> values = new ArrayList<DropDownItem>();

    /* State control */
    public int selected = -1;
    public boolean visible;

    /* Scrolling stuff */
    public int scroll;
    public boolean scrolling;

    /* Private stuff */
    private FontRenderer font;
    private IDropDownListener listener;

    public GuiDropDownField(FontRenderer font, IDropDownListener listener)
    {
        this.font = font;
        this.listener = listener;
    }

    public boolean isInside(int x, int y)
    {
        return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h + (this.visible ? this.maxHeight : 0);
    }

    public void setSelected(String value)
    {
        int index = -1;
        int i = 0;

        for (DropDownItem item : this.values)
        {
            if (item.value.equals(value))
            {
                index = i;
            }

            i++;
        }

        this.selected = index;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (!this.isInside(mouseX, mouseY) || (mouseX > this.x + this.w - 20 && mouseY < this.y + 20 && this.visible))
        {
            this.visible = false;

            return;
        }

        if (!this.visible)
        {
            this.visible = true;
        }
        else if (mouseY - this.y > 20)
        {
            if (mouseX >= this.x + this.w - 10)
            {
                this.scrolling = true;

                return;
            }

            int index = (mouseY - this.y - 20 + this.scroll) / 20;

            if (index >= 0 && index < this.values.size())
            {
                this.selected = index;
                this.listener.clickedDropDown(this, this.values.get(index).value);
                this.visible = false;
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.scrolling = false;
    }

    /**
     * Draw the drop down 
     */
    public void draw(int mouseX, int mouseY, int width, int height, float partialTicks)
    {
        int h = this.h + (this.visible ? this.maxHeight : 0);

        /* Draw the background */
        Gui.drawRect(this.x, this.y, this.x + this.w, this.y + h, 0xff888888);
        Gui.drawRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + h - 1, 0xff000000);

        String selected = "";

        if (this.selected >= 0 && this.selected < this.values.size())
        {
            selected = this.values.get(this.selected).title;
        }

        this.font.drawStringWithShadow(selected, this.x + 6, this.y + 6, 0xffffff);

        int cx = this.x + this.w - 20 + 6;
        int cy = this.y + 8;

        if (this.visible)
        {
            cx += 6;
            cy -= 2;
        }

        for (int i = 0; i < 4; i++)
        {
            if (this.visible)
            {
                Gui.drawRect(cx - i, cy + i, cx - i - 1, cy + 8 - i, 0xffffffff);
            }
            else
            {
                Gui.drawRect(cx + i, cy + i, cx + 8 - i, cy + i + 1, 0xffffffff);
            }
        }

        Gui.drawRect(this.x + this.w - 20, this.y, this.x + this.w - 19, this.y + 20, 0xff888888);

        if (this.visible)
        {
            /* Scroll the view */
            int max = this.values.size() * 20 - this.maxHeight;

            if (this.scrolling)
            {
                this.scroll = (int) ((mouseY - (this.y + 20)) / (float) this.maxHeight * max);
                this.scroll = MathHelper.clamp_int(this.scroll, 0, max);
            }

            /* Draw the dropdown items */
            Gui.drawRect(this.x, this.y + 19, this.x + this.w, this.y + 20, 0xff888888);
            GuiUtils.scissor(this.x + 1, this.y + 19, this.w - 2, this.maxHeight, width, height);

            int y = this.y + 20;
            int i = 0;

            for (DropDownItem item : this.values)
            {
                int yy = y + i * 20 - this.scroll;
                boolean hover = this.isInside(mouseX, mouseY) && mouseY >= yy && mouseY < yy + 20;

                Gui.drawRect(this.x, yy + 19, this.x + this.w, yy + 20, 0x88888888);

                this.font.drawStringWithShadow(item.title, this.x + 6, yy + 6, hover ? 0xffffff : 0xcccccc);

                i++;
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            /* Draw scrollbar */
            if (max > 0)
            {
                float factor = this.scroll / (float) max;

                int sy = this.y + 20 + (int) (factor * (this.maxHeight - 21));

                Gui.drawRect(this.x + this.w - 10, sy, this.x + this.w - 1, sy + 20, 0xffffffff);
            }
        }
    }

    /**
     * Drop dpwn title 
     */
    public static class DropDownItem
    {
        public String title;
        public String value;

        public DropDownItem(String title, String value)
        {
            this.title = title;
            this.value = value;
        }
    }

    /**
     * Callback interface 
     */
    public static interface IDropDownListener
    {
        public void clickedDropDown(GuiDropDownField dropDown, String value);
    }
}
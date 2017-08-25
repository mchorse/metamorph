package mchorse.metamorph.client.gui.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

/**
 * Dropdown list
 */
public class GuiDropDownField
{
    /* Field's frame */
    public int x;
    public int y;
    public int w;
    public int h;

    public int maxHeight = 80;

    public List<String> values = new ArrayList<String>();
    public int selected = -1;
    public boolean visible;

    public int scroll;
    public boolean scrolling;

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

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (!this.isInside(mouseX, mouseY))
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
            int index = (mouseY - this.y - 20 + this.scroll) / 20;

            if (index >= 0 && index < this.values.size())
            {
                this.selected = index;
                this.listener.clickedDropDown(this, this.values.get(index));
                this.visible = false;
            }
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        int h = this.h + (this.visible ? this.maxHeight : 0);

        Gui.drawRect(this.x, this.y, this.x + this.w, this.y + h, 0xff888888);
        Gui.drawRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + h - 1, 0xff000000);

        String selected = this.selected >= 0 ? this.values.get(this.selected) : "";

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
            Gui.drawRect(this.x, this.y + 19, this.x + this.w, this.y + 20, 0xff888888);

            int y = this.y + 20;
            int i = 0;

            for (String value : this.values)
            {
                int yy = y + i * 20;
                boolean hover = this.isInside(mouseX, mouseY) && mouseY >= yy && mouseY < yy + 20;
                boolean current = this.selected == i;

                Gui.drawRect(this.x, yy + 19, this.x + this.w, yy + 20, 0x88888888);

                if (current)
                {
                    value = "> " + value;
                }

                this.font.drawStringWithShadow(value, this.x + 6, yy + 6, hover ? 0xffffff : 0xcccccc);

                i++;
            }
        }
    }

    public static interface IDropDownListener
    {
        public void clickedDropDown(GuiDropDownField dropDown, String value);
    }
}
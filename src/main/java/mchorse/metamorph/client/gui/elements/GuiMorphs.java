package mchorse.metamorph.client.gui.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Scroll list of available morphs
 * 
 * More morphs than presented in this menu are available, but the problem that 
 * it's impossible to list all variation of those morphs. iChun probably knows 
 * it, that's why he doesn't bother with a GUI of all available morphs.
 */
public class GuiMorphs extends GuiScrollPane
{
    private static final int cellH = 60;

    private int perRow;

    private int selected = -1;
    private int selectedMorph = -1;

    private String filter = "";

    /**
     * This field stores categories, which store available morphs 
     */
    private List<MorphCategory> categories = new ArrayList<MorphCategory>();

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiMorphs(int perRow)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        IMorphing morphing = Morphing.get(player);
        Map<String, MorphCategory> categories = new HashMap<String, MorphCategory>();

        for (List<AbstractMorph> morphs : MorphManager.INSTANCE.getMorphs().morphs.values())
        {
            for (AbstractMorph morph : morphs)
            {
                MorphCategory category = categories.get(morph.category);

                if (category == null)
                {
                    category = new MorphCategory(morph.category, morph.category);
                    categories.put(morph.category, category);
                }

                category.cells.add(new MorphCell(morph.name, morph, category.cells.size()));
            }
        }

        this.perRow = perRow;
        this.categories.addAll(categories.values());

        int i = 0;

        /* Calculate the scroll height and per category height */
        for (MorphCategory category : categories.values())
        {
            category.height = MathHelper.ceiling_float_int((float) category.cells.size() / (float) this.perRow);
            category.y = this.scrollHeight + 20;

            this.scrollHeight += category.height * cellH + 20;
            int j = 0;

            /* Select current morph */
            if (this.selected == -1)
            {
                for (MorphCell cell : category.cells)
                {
                    if (morphing.isMorphed() && cell.morph.equals(morphing.getCurrentMorph()))
                    {
                        this.selected = i;
                        this.selectedMorph = j;
                    }

                    j++;
                }
            }

            i++;
        }

        this.scrollHeight += 10;
    }

    /**
     * Set filtering 
     */
    public void setFilter(String text)
    {
        this.filter = text;
        this.scrollHeight = 0;

        for (MorphCategory cat : this.categories)
        {
            int i = 0;

            for (MorphCell cell : cat.cells)
            {
                if (text.isEmpty())
                {
                    cell.hidden = false;
                }
                else
                {
                    cell.hidden = cell.name.toLowerCase().indexOf(text.toLowerCase()) == -1;
                }

                if (!cell.hidden)
                {
                    i++;
                }
            }

            cat.height = MathHelper.ceiling_float_int((float) i / (float) this.perRow);
            cat.y = this.scrollHeight + 20;

            this.scrollHeight += i == 0 ? 0 : cat.height * cellH + 20;
        }

        this.scrollY = 0;
    }

    /**
     * Get currently selected morph 
     */
    public AbstractMorph getSelected()
    {
        if (this.selected >= 0 && this.selected < this.categories.size())
        {
            MorphCategory category = this.categories.get(this.selected);

            if (this.selectedMorph >= 0 && this.selectedMorph < category.cells.size())
            {
                return category.cells.get(this.selectedMorph).morph;
            }
        }

        return null;
    }

    /**
     * Mouse clicked event
     * 
     * This method is responsible for selecting a morph. Very much code for such 
     * simple feature.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.isInside(mouseX, mouseY) && !this.dragging)
        {
            int y = mouseY - this.y + this.scrollY - 10;
            int x = (mouseX - this.x) / (this.w / this.perRow);
            int i = 0;

            MorphCategory cat = null;

            for (MorphCategory category : this.categories)
            {
                if (y >= category.y && y < category.y + category.height * cellH)
                {
                    cat = category;
                    break;
                }

                i++;
            }

            if (cat != null)
            {
                y = (y - cat.y) / cellH;

                this.selected = i;
                this.selectedMorph = -1;

                int j = 0;
                int index = x + y * this.perRow;

                for (MorphCell cell : cat.cells)
                {
                    if (!cell.hidden)
                    {
                        if (j == index)
                        {
                            this.selectedMorph = cell.index;

                            break;
                        }

                        j++;
                    }
                }
            }
            else
            {
                this.selected = -1;
                this.selectedMorph = -1;
            }
        }
    }

    /**
     * Don't draw the background 
     */
    @Override
    protected void drawBackground()
    {}

    @Override
    protected void drawPane(int mouseX, int mouseY, float partialTicks)
    {
        int m = this.w / this.perRow;
        int j = 0;

        /* Render morphs */
        for (MorphCategory category : this.categories)
        {
            if (category.height == 0)
            {
                j++;

                continue;
            }

            int k = 0;
            this.drawString(fontRendererObj, category.title, this.x + 1, category.y + this.y, 0xFFFFFFFF);

            for (MorphCell cell : category.cells)
            {
                int x = k % this.perRow * m + this.x;
                int y = k / this.perRow * cellH + category.y + this.y;

                if (cell.hidden)
                {
                    continue;
                }

                float scale = 21.5F;

                this.renderMorph(cell, Minecraft.getMinecraft().thePlayer, x + m / 2, y + 50, scale);

                if (j == this.selected && cell.index == this.selectedMorph)
                {
                    this.renderSelected(x + 1, y + 10, m - 2, cellH);
                }

                k++;
            }

            j++;
        }
    }

    /**
     * Render a morph 
     */
    private void renderMorph(MorphCell cell, EntityPlayer player, int x, int y, float scale)
    {
        /* Render the model */
        cell.morph.renderOnScreen(player, x, y, scale, 1.0F);
    }

    /**
     * Render a grey outline around the given area.
     * 
     * Basically, this method renders selection.
     */
    private void renderSelected(int x, int y, int width, int height)
    {
        int color = 0xffcccccc;

        this.drawHorizontalLine(x, x + width - 1, y, color);
        this.drawHorizontalLine(x, x + width - 1, y + height - 1, color);

        this.drawVerticalLine(x, y, y + height - 1, color);
        this.drawVerticalLine(x + width - 1, y, y + height - 1, color);
    }

    /**
     * Morph category class
     * 
     * This class is responsible for holding morph cells located in this 
     * category
     */
    public static class MorphCategory
    {
        /**
         * Prefix for morph category titles 
         */
        public static final String KEY = "morph.category.";

        public List<MorphCell> cells = new ArrayList<MorphCell>();

        /* Meta information */
        public String title;
        public String key;

        /* Cached space information */
        public int height;
        public int y;

        public MorphCategory(String title, String key)
        {
            String result = I18n.format(KEY + title);

            if (title.isEmpty())
            {
                result = I18n.format(KEY + "unknown");
            }
            else if (result.equals(KEY + title))
            {
                result = I18n.format(KEY + "modded", title);
            }

            this.title = result;
            this.key = key;
        }
    }

    /**
     * Morph cell class
     * 
     * An instance of this class represents a morph which can be selected and 
     * morphed into upon pressing "Morph" button.
     */
    public static class MorphCell
    {
        public String name;
        public AbstractMorph morph;
        public int index;
        public boolean hidden = false;

        public MorphCell(String name, AbstractMorph morph, int index)
        {
            this.name = name;
            this.morph = morph;
            this.index = index;
        }
    }
}
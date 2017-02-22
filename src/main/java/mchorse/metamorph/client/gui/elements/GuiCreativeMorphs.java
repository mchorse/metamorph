package mchorse.metamorph.client.gui.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Scroll list of available morphs
 * 
 * More morphs than presented in this menu are available, but the problem that 
 * it's impossible to list all variation of those morphs. iChun probably knows 
 * it, that's why he doesn't bother with a GUI of all available morphs.
 */
public class GuiCreativeMorphs extends GuiScrollPane
{
    /**
     * Morph cell's height 
     */
    private static final int cellH = 60;

    /**
     * How many morphs visible per row 
     */
    private int perRow;

    /**
     * Index of selected category 
     */
    private int selected = -1;

    /**
     * Index of selected moprh 
     */
    private int selectedMorph = -1;

    /**
     * Cached previous filter. Used for avoiding double recalculations 
     */
    private String previousFilter = "";

    /**
     * This field stores categories, which store available morphs 
     */
    private List<MorphCategory> categories = new ArrayList<MorphCategory>();

    /**
     * Category label shift
     */
    public int shiftX = 0;

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiCreativeMorphs(int perRow, AbstractMorph selected, IMorphing morphing)
    {
        this.perRow = perRow;
        this.compileCategories(morphing);
        this.initiateCategories(selected);
    }

    public GuiCreativeMorphs(int perRow, AbstractMorph selected)
    {
        this(perRow, selected, null);
    }

    /**
     * Compile morph categories
     * 
     * This method is responsible for compiling all morph categories into 
     * {@link #categories} list and then sorting it by its titles.
     */
    protected void compileCategories(IMorphing morphing)
    {
        Map<String, MorphCategory> categories = new HashMap<String, MorphCategory>();
        World world = Minecraft.getMinecraft().theWorld;

        for (List<MorphList.MorphCell> morphs : MorphManager.INSTANCE.getMorphs(world).morphs.values())
        {
            for (MorphList.MorphCell morph : morphs)
            {
                MorphCategory category = categories.get(morph.category);

                if (category == null)
                {
                    category = new MorphCategory(morph.category, morph.category);
                    categories.put(morph.category, category);
                }

                String variant = morph.variant.isEmpty() ? morph.variant : " (" + morph.variant + ")";
                String title = MorphManager.INSTANCE.morphDisplayNameFromMorph(morph.morph) + variant;

                category.cells.add(new MorphCell(title, morph.morph, 0));
            }
        }

        this.categories.addAll(categories.values());

        Collections.sort(this.categories, new Comparator<MorphCategory>()
        {
            @Override
            public int compare(MorphCategory a, MorphCategory b)
            {
                return a.title.compareTo(b.title);
            }
        });

        if (morphing != null)
        {
            MorphCategory category = new MorphCategory("acquired", "acquired");

            this.categories.add(0, category);

            for (AbstractMorph morph : morphing.getAcquiredMorphs())
            {
                category.cells.add(new MorphCell(MorphManager.INSTANCE.morphDisplayNameFromMorph(morph), morph, 0));
            }
        }
    }

    /**
     * Initiate morph categories
     * 
     * This method is responsible for sorting for each category cells by 
     * alphabet, computing space attributes (height and y-coord) and selecting 
     * most similar morph that player might have.
     */
    protected void initiateCategories(AbstractMorph morph)
    {
        int i = 0;
        int y = 0;

        int selectedCat = -1;
        int selectedMorph = -1;

        this.scrollHeight = 0;

        for (MorphCategory category : this.categories)
        {
            int j = 0;

            Collections.sort(category.cells, new Comparator<MorphCell>()
            {
                @Override
                public int compare(MorphCell a, MorphCell b)
                {
                    return a.name.compareTo(b.name);
                }
            });

            /* Calculate the scroll height and per category height */
            category.height = MathHelper.ceiling_float_int((float) category.cells.size() / (float) this.perRow);
            category.y = this.scrollHeight + 20;

            this.scrollHeight += category.height * cellH + 20;

            /* Select current morph */
            for (MorphCell cell : category.cells)
            {
                if (selectedCat == -1 && morph != null && cell.morph.equals(morph))
                {
                    selectedCat = i;
                    selectedMorph = j;

                    y = category.y + j / this.perRow * cellH;
                }

                cell.index = j;

                j++;
            }

            i++;
        }

        this.scrollHeight += 10;
        this.scrollTo(y);

        this.selected = selectedCat;
        this.selectedMorph = selectedMorph;
    }

    /**
     * Set filter for search
     * 
     * This method is responsible for recalculating the hidden flag of the 
     * individual cells and changing heights and y position of each category.
     */
    public void setFilter(String filter)
    {
        if (filter.equals(this.previousFilter))
        {
            return;
        }

        this.scrollY = 0;
        this.scrollHeight = 0;
        this.previousFilter = filter;

        for (MorphCategory cat : this.categories)
        {
            int i = 0;

            for (MorphCell cell : cat.cells)
            {
                cell.hidden = filter.isEmpty() ? false : cell.name.toLowerCase().indexOf(filter.toLowerCase()) == -1;

                if (!cell.hidden)
                {
                    i++;
                }
            }

            cat.height = MathHelper.ceiling_float_int((float) i / (float) this.perRow);
            cat.y = this.scrollHeight + 20;

            this.scrollHeight += i == 0 ? 0 : cat.height * cellH + 20;
        }
    }

    /**
     * Set selected morph 
     */
    public void setSelected(AbstractMorph morph)
    {
        this.initiateCategories(morph);
    }

    /**
     * Get currently selected morph 
     */
    public MorphCell getSelected()
    {
        if (this.selected >= 0 && this.selected < this.categories.size())
        {
            MorphCategory category = this.categories.get(this.selected);

            if (this.selectedMorph >= 0 && this.selectedMorph < category.cells.size())
            {
                return category.cells.get(this.selectedMorph);
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

        if (!this.isInside(mouseX, mouseY) || this.dragging)
        {
            return;
        }

        /* Computing x and y. X is horizontal index, and y is simply shifted 
         * mouseY relative to the scroll pane's top edge*/
        int y = mouseY - this.y + this.scrollY - 10;
        int x = (mouseX - this.x) / (this.w / this.perRow);
        int i = 0;

        /* Searching for a category which is in shifted y's range */
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

        /* If we found a category, we'll need to select the category and also 
         * a morph in the category. This requires some logic and looping, 
         * because some of the cells might be hidden */
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
            this.drawString(fontRendererObj, category.title, this.x + this.shiftX + 1, category.y + this.y, 0xFFFFFFFF);

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
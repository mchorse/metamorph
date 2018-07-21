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
import net.minecraft.nbt.NBTTagCompound;
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
    public static final int CELL_HEIGHT = 60;

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
     * Variant picker 
     */
    private GuiCreativeVariantPicker picker;

    /**
     * Acquired morphs category
     */
    private MorphCategory acquired;

    /**
     * Morph that was selected 
     */
    private MorphCell selectedCell;

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
        this.setScrollSpeed(15);
        this.picker = new GuiCreativeVariantPicker(this);
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

        /* Compile categories */
        for (List<MorphList.MorphCell> morphs : MorphManager.INSTANCE.getMorphs(world).morphs.values())
        {
            if (morphs.size() == 0)
            {
                continue;
            }

            MorphCell cell = new MorphCell();
            String categoryName = morphs.get(0).category;

            for (MorphList.MorphCell morph : morphs)
            {
                String variant = morph.variant.isEmpty() ? morph.variant : " (" + morph.variant + ")";
                String title = MorphManager.INSTANCE.morphDisplayNameFromMorph(morph.morph) + variant;

                cell.variants.add(new MorphVariant(title, morph.morph));
            }

            MorphCategory category = categories.get(categoryName);

            if (category == null)
            {
                category = new MorphCategory(categoryName, categoryName);
                categories.put(categoryName, category);
            }

            category.cells.add(cell);
        }

        /* Sort categories by the name */
        this.categories.addAll(categories.values());

        Collections.sort(this.categories, new Comparator<MorphCategory>()
        {
            @Override
            public int compare(MorphCategory a, MorphCategory b)
            {
                return a.title.compareTo(b.title);
            }
        });

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Name", "metamorph.Block");
        tag.setString("Block", "minecraft:stone");

        this.acquired = new MorphCategory("acquired", "acquired");
        this.acquired.cells.add(this.selectedCell = new MorphCell());
        this.selectedCell.variants.add(new MorphVariant("Selected", MorphManager.INSTANCE.morphFromNBT(tag)));
        this.categories.add(0, this.acquired);

        /* Add also acquired morphs category, in case if capability was provided */
        if (morphing != null)
        {
            for (AbstractMorph morph : morphing.getAcquiredMorphs())
            {
                this.acquired.cells.add(new MorphCell(MorphManager.INSTANCE.morphDisplayNameFromMorph(morph), morph));
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
                    return a.current().name.compareTo(b.current().name);
                }
            });

            /* Calculate the scroll height and per category height */
            category.height = MathHelper.ceiling_float_int((float) category.cells.size() / (float) this.perRow);
            category.y = this.scrollHeight + 10;

            this.scrollHeight += category.height * CELL_HEIGHT + 40;

            /* Select current morph */
            for (MorphCell cell : category.cells)
            {
                Collections.sort(cell.variants, new Comparator<MorphVariant>()
                {
                    @Override
                    public int compare(MorphVariant a, MorphVariant b)
                    {
                        return a.name.compareTo(b.name);
                    }
                });

                if (selectedCat == -1 && morph != null && cell.hasMorph(morph))
                {
                    selectedCat = i;
                    selectedMorph = j;

                    y = category.y + j / this.perRow * CELL_HEIGHT;
                }

                cell.index = j;

                j++;
            }

            i++;
        }

        this.scrollHeight -= 10;
        this.scrollTo(y);

        this.selected = selectedCat;
        this.selectedMorph = selectedMorph;

        if ((selectedCat == -1 || selectedMorph == -1) && morph != null)
        {
            this.selected = 0;
            this.selectedMorph = this.selectedCell.index;
            this.scrollTo(0);
            this.selectedCell.variants.clear();
            this.selectedCell.variants.add(new MorphVariant(I18n.format("metamorph.gui.selected"), morph.clone(true)));
            this.selectedCell.selected = 0;
        }
    }

    /**
     * Set amount of morphs per row 
     */
    public void setPerRow(int perRow)
    {
        MorphCell selected = this.getSelected();

        this.perRow = perRow;
        this.initiateCategories(selected != null ? selected.current().morph : null);
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

        String lcfilter = filter.toLowerCase();

        this.scrollY = 0;
        this.scrollHeight = 0;
        this.previousFilter = filter;

        for (MorphCategory cat : this.categories)
        {
            int i = 0;

            for (MorphCell cell : cat.cells)
            {
                boolean selected = false;
                int j = 0;

                cell.hasVisible = false;

                for (MorphVariant variant : cell.variants)
                {
                    variant.hidden = filter.isEmpty() ? false : variant.name.toLowerCase().indexOf(lcfilter) == -1 && variant.morph.name.toLowerCase().indexOf(lcfilter) == -1;

                    if (!variant.hidden)
                    {
                        if (!selected)
                        {
                            cell.selected = j;
                            selected = true;
                        }

                        cell.hasVisible = true;
                    }

                    j++;
                }

                if (cell.hasVisible)
                {
                    i++;
                }
            }

            cat.height = MathHelper.ceiling_float_int((float) i / (float) this.perRow);
            cat.y = this.scrollHeight + 10;

            this.scrollHeight += i == 0 ? 0 : cat.height * CELL_HEIGHT + 40;
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
                MorphCell cell = category.cells.get(this.selectedMorph);

                if (!cell.variants.isEmpty())
                {
                    return cell;
                }
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

        if (this.picker.isActive() && mouseY >= this.y + this.h - CELL_HEIGHT)
        {
            this.picker.mouseClicked(mouseX, mouseY, mouseButton);

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
            if (y >= category.y && y < category.y + category.height * CELL_HEIGHT)
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
            y = (y - cat.y) / CELL_HEIGHT;

            this.selected = i;
            this.selectedMorph = -1;

            int j = 0;
            int index = x + y * this.perRow;

            for (MorphCell cell : cat.cells)
            {
                if (cell.hasVisible)
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

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.picker.isActive())
        {
            this.picker.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Don't draw the background 
     */
    @Override
    protected void drawBackground(int mouseX, int mouseY, float partialTicks)
    {}

    @Override
    protected void drawScrollBar(int mouseX, int mouseY, float partialTicks)
    {
        if (this.picker.isActive())
        {
            this.picker.drawPane(mouseX, mouseY, partialTicks);
        }

        super.drawScrollBar(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawPane(int mouseX, int mouseY, float partialTicks)
    {
        int m = this.w / this.perRow;
        int j = 0;

        /* Render morphs */
        for (MorphCategory category : this.categories)
        {
            int h = (MathHelper.ceiling_float_int(category.cells.size() / this.perRow) + 1) * CELL_HEIGHT;

            if (category.height == 0 || category.y < this.scrollY - h || category.y > this.scrollY + this.h)
            {
                j++;

                continue;
            }

            int k = 0;
            this.drawString(fontRendererObj, category.title, this.x + this.shiftX + 1, category.y + this.y, 0xFFFFFFFF);

            for (MorphCell cell : category.cells)
            {
                int x = k % this.perRow * m + this.x;
                int y = k / this.perRow * CELL_HEIGHT + category.y + this.y;

                boolean insidePicker = this.picker.isActive() && mouseY >= this.y + this.h - CELL_HEIGHT;
                boolean hover = mouseX >= x && mouseY + this.scrollY >= y && mouseX < x + m && mouseY + this.scrollY < y + CELL_HEIGHT && !insidePicker;

                MorphVariant variant = cell.current();

                if (!cell.hasVisible)
                {
                    continue;
                }

                if (y < this.scrollY - CELL_HEIGHT / 4)
                {
                    k++;
                    continue;
                }

                float scale = hover ? 28F : 21.5F;

                variant.render(Minecraft.getMinecraft().thePlayer, x + m / 2, y + 50, scale);

                if (j == this.selected && cell.index == this.selectedMorph)
                {
                    this.renderSelected(x + 1, y + 10, m - 2, CELL_HEIGHT, variant.error);
                }

                k++;
            }

            j++;
        }
    }

    /**
     * Render a grey outline around the given area.
     * 
     * Basically, this method renders selection.
     */
    private void renderSelected(int x, int y, int width, int height, boolean error)
    {
        int color = error ? 0xffff0000 : 0xffcccccc;

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
            else
                if (result.equals(KEY + title))
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
        public List<MorphVariant> variants = new ArrayList<MorphVariant>();

        /**
         * Index of this morph cell in the category 
         */
        public int index;

        /**
         * Index of selected morph variant 
         */
        public int selected;

        /**
         * Whether this morph has visible
         */
        public boolean hasVisible;

        public MorphCell()
        {
            this.hasVisible = true;
        }

        public MorphCell(String name, AbstractMorph morph)
        {
            this();
            this.variants.add(new MorphVariant(name, morph));
        }

        /**
         * Checks whether this morph has similar  
         */
        public boolean hasMorph(AbstractMorph morph)
        {
            for (int i = 0, c = this.variants.size(); i < c; i++)
            {
                MorphVariant variant = this.variants.get(i);

                if (variant.morph.equals(morph))
                {
                    this.selected = i;

                    return true;
                }
            }

            return false;
        }

        /**
         * Get currently selected morph variant
         */
        public MorphVariant current()
        {
            return this.variants.get(this.selected);
        }
    }

    public static class MorphVariant
    {
        public String name;
        public AbstractMorph morph;

        public boolean hidden = false;
        public boolean first = true;
        public boolean error = false;

        public MorphVariant(String name, AbstractMorph morph)
        {
            this.name = name;
            this.morph = morph;
        }

        public void render(EntityPlayer player, int x, int y, float scale)
        {
            if (this.first)
            {
                try
                {
                    this.morph.renderOnScreen(player, x, y, scale, 1.0F);
                }
                catch (Exception e)
                {
                    String name = this.morph != null ? this.morph.name : "unknown";

                    System.out.println("Failed to render morph by name " + name + "!");
                    e.printStackTrace();

                    this.error = true;
                }

                this.first = false;
            }
            else
                if (!this.error)
                {
                    this.morph.renderOnScreen(player, x, y, scale, 1.0F);
                }
        }
    }
}
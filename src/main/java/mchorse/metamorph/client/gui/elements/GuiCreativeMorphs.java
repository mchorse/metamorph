package mchorse.metamorph.client.gui.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.events.ReloadMorphs;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * Scroll list of available morphs
 * 
 * More morphs than presented in this menu are available, but the problem that 
 * it's impossible to list all variation of those morphs. iChun probably knows 
 * it, that's why he doesn't bother with a GUI of all available morphs.
 */
public class GuiCreativeMorphs extends GuiElement
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
     * Acquired morphs category
     */
    private MorphCategory acquired;

    /**
     * Morph that was selected 
     */
    private MorphCell selectedCell;

    /**
     * Scroll area responsible for handling area scroll 
     */
    public ScrollArea scroll = new ScrollArea(60);

    /**
     * Morph consumer 
     */
    public Consumer<AbstractMorph> callback;

    /**
     * Category label shift
     */
    public int shiftX = 0;

    /**
     * Available morph editors 
     */
    private List<GuiAbstractMorph> editors;

    /**
     * Variant picker 
     */
    private GuiCreativeVariantPicker picker;

    /**
     * Morph editor 
     */
    public GuiDelegateElement<GuiAbstractMorph> editor;

    public GuiButtonElement<GuiButton> top;
    public GuiButtonElement<GuiButton> edit;
    public GuiTextElement search;

    /**
     * Initiate this GUI.
     * 
     * Compile the categories list and compute the scroll height of this scroll pane 
     */
    public GuiCreativeMorphs(Minecraft mc, int perRow, AbstractMorph selected, IMorphing morphing)
    {
        super(mc);

        MinecraftForge.EVENT_BUS.post(new ReloadMorphs());

        this.perRow = perRow;
        this.compileCategories(morphing);
        this.initiateCategories(selected, true);
        this.picker = new GuiCreativeVariantPicker(mc, this);
        this.picker.resizer().parent(this.area).set(0, 0, 0, 60).w(1, 0).y(1, -60);

        this.editor = new GuiDelegateElement<GuiAbstractMorph>(mc, null);
        this.editor.resizer().parent(this.area).set(0, 0, 1, 1, Measure.RELATIVE);

        this.createChildren();

        this.top = GuiButtonElement.button(mc, "^", (b) -> this.scroll.scrollTo(0));
        this.edit = GuiButtonElement.button(mc, I18n.format("metamorph.gui.edit"), (b) ->
        {
            this.toggleEditMode();
        });

        this.search = new GuiTextElement(mc, (filter) -> this.setFilter(filter));
        this.search.field.setFocused(true);

        this.edit.resizer().parent(this.area).set(0, 10, 55, 20).x(1, -35 - 55);
        this.top.resizer().relative(this.edit.resizer()).set(60, 0, 20, 20);

        this.search.resizer().parent(this.area).set(10, 10, 0, 20).w(1, -105);
        this.children.add(this.picker, this.edit, this.search, this.top, this.editor);

        this.scroll.scrollSpeed = 40;
    }

    public GuiCreativeMorphs(Minecraft mc, int perRow, AbstractMorph selected)
    {
        this(mc, perRow, selected, null);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.scroll.copy(this.area);
    }

    public boolean isEditMode()
    {
        return this.editor.delegate != null;
    }

    public void toggleEditMode()
    {
        MorphCell cell = this.getSelected();

        if (cell == null)
        {
            return;
        }

        AbstractMorph morph = cell.current().morph;

        if (!this.isEditMode())
        {
            if (morph != null)
            {
                morph = morph.clone(true);
            }

            GuiAbstractMorph editor = this.getMorphEditor(morph);

            editor.finish.callback = this.getToggleCallback();

            if (editor != null)
            {
                this.editor.setDelegate(editor);
                this.setSelected(morph, false, false);
                this.setMorph(morph);
            }
        }
        else
        {
            this.editor.delegate.finishEdit();
            this.editor.setDelegate(null);

            this.setMorph(morph);
        }

        boolean hide = this.editor.delegate == null;

        this.picker.setVisible(hide);
        this.search.setVisible(hide);
        this.top.setVisible(hide);
        this.edit.setVisible(hide);
    }

    public void finish()
    {
        if (this.isEditMode())
        {
            this.editor.delegate.finishEdit();
        }
    }

    protected Consumer<GuiButtonElement<GuiButton>> getToggleCallback()
    {
        return this.edit.callback;
    }

    private GuiAbstractMorph getMorphEditor(AbstractMorph morph)
    {
        if (this.editors == null)
        {
            this.editors = new ArrayList<GuiAbstractMorph>();
            MorphManager.INSTANCE.registerMorphEditors(this.editors);
        }

        for (GuiAbstractMorph editor : this.editors)
        {
            if (editor.canEdit(morph))
            {
                editor.startEdit(morph);

                return editor;
            }
        }

        return null;
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
        World world = Minecraft.getMinecraft().world;

        /* Compile categories */
        for (List<MorphList.MorphCell> morphs : MorphManager.INSTANCE.getMorphs(world).morphs.values())
        {
            if (morphs.size() == 0)
            {
                continue;
            }

            MorphCell cell = new MorphCell();
            String name = morphs.get(0).category;
            String variant = morphs.get(0).categoryVariant;
            String key = name + "#" + variant;

            for (MorphList.MorphCell morph : morphs)
            {
                String morphVariant = morph.variant.isEmpty() ? morph.variant : " (" + morph.variant + ")";
                String title = MorphManager.INSTANCE.morphDisplayNameFromMorph(morph.morph) + morphVariant;

                cell.variants.add(new MorphVariant(title, morph.morph));
            }

            MorphCategory category = categories.get(key);

            if (category == null)
            {
                category = new MorphCategory(name, key, variant);
                categories.put(key, category);
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

        this.acquired = new MorphCategory("acquired", "acquired", "");
        this.acquired.cells.add(this.selectedCell = new MorphCell());
        this.selectedCell.variants.add(new MorphVariant("Selected", MorphManager.INSTANCE.morphFromNBT(tag)));
        this.categories.add(0, this.acquired);

        /* Add also acquired morphs category, in case if capability was provided */
        if (morphing != null)
        {
            for (AbstractMorph morph : morphing.getAcquiredMorphs())
            {
                /* Cloning morphs so the survival morphs won't get modified */
                this.acquired.cells.add(new MorphCell(MorphManager.INSTANCE.morphDisplayNameFromMorph(morph), morph.clone(true)));
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
    protected void initiateCategories(AbstractMorph morph, boolean compare)
    {
        int i = 0;
        int y = 0;

        int selectedCat = -1;
        int selectedMorph = -1;

        this.scroll.scrollSize = 30;

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
            category.height = MathHelper.ceil((float) category.cells.size() / (float) this.perRow);
            category.y = this.scroll.scrollSize + 10;

            this.scroll.scrollSize += category.height * CELL_HEIGHT + 40;

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

                if (selectedCat == -1 && morph != null && cell.hasMorph(morph) && compare)
                {
                    selectedCat = i;
                    selectedMorph = j;

                    y = category.y + j / this.perRow * CELL_HEIGHT;
                }

                cell.index = j;
                cell.hasVisible = true;

                j++;
            }

            i++;
        }

        this.scroll.scrollSize -= 10;
        this.scroll.scrollTo(y - 40);

        this.selected = selectedCat;
        this.selectedMorph = selectedMorph;

        if ((selectedCat == -1 || selectedMorph == -1) && morph != null)
        {
            this.selected = 0;
            this.selectedMorph = this.selectedCell.index;
            this.scroll.scrollTo(0);
            this.selectedCell.variants.clear();
            this.selectedCell.variants.add(new MorphVariant(I18n.format("metamorph.gui.selected"), compare ? morph.clone(true) : morph));
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
        this.initiateCategories(selected != null ? selected.current().morph : null, true);
    }

    /**
     * Added for compatibility 
     */
    public void setFilter(String filter)
    {
        this.setFilter(filter, true);
    }

    /**
     * Set filter for search
     * 
     * This method is responsible for recalculating the hidden flag of the 
     * individual cells and changing heights and y position of each category.
     */
    public void setFilter(String filter, boolean select)
    {
        if (filter.equals(this.previousFilter))
        {
            return;
        }

        String lcfilter = filter.toLowerCase();

        this.scroll.scroll = 0;
        this.scroll.scrollSize = 30;
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
                        if (!selected && select)
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

            cat.height = MathHelper.ceil((float) i / (float) this.perRow);
            cat.y = this.scroll.scrollSize + 10;

            this.scroll.scrollSize += i == 0 ? 0 : cat.height * CELL_HEIGHT + 40;
        }
    }

    public void setSelected(AbstractMorph morph)
    {
        this.setSelected(morph, true, true);
    }

    /**
     * Set selected morph 
     */
    public void setSelected(AbstractMorph morph, boolean restore, boolean compare)
    {
        this.initiateCategories(morph, compare);

        /* Make sure to restore the state from previous time */
        if (restore)
        {
            String prevFilter = this.previousFilter;

            if (!prevFilter.isEmpty())
            {
                this.previousFilter = "";
                this.setFilter(prevFilter, false);
            }
        }
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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.scroll.mouseClicked(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (!this.area.isInside(mouseX, mouseY) || this.isEditMode() || this.search.field.isFocused())
        {
            return false;
        }

        /* Computing x and y. X is horizontal index, and y is simply shifted 
         * mouseY relative to the scroll pane's top edge*/
        int y = mouseY - this.scroll.y + this.scroll.scroll - 10;
        int x = (mouseX - this.scroll.x) / (this.scroll.w / this.perRow);
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
                        this.setMorph(this.getSelected().current().morph);

                        break;
                    }

                    j++;
                }
            }

            if (this.selectedMorph == -1)
            {
                this.setMorph(null);
            }
        }
        else
        {
            this.selected = -1;
            this.selectedMorph = -1;
            this.setMorph(null);
        }

        return true;
    }

    public void setMorph(AbstractMorph morph)
    {
        if (this.callback != null)
        {
            this.callback.accept(morph);
        }
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return super.mouseScrolled(mouseX, mouseY, scroll) || this.scroll.mouseScroll(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.scroll.mouseReleased(mouseX, mouseY);
    }

    /**
     * Shortcuts for scrolling the morph menu up and down with arrow keys. 
     * 
     * There are also shortcuts for getting in the end or beginning of the 
     * screen (left and right arrow keys).
     */
    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        if (!this.isEditMode())
        {
            if (keyCode == Keyboard.KEY_DOWN)
            {
                this.scroll.scrollBy(30);
            }
            else if (keyCode == Keyboard.KEY_UP)
            {
                this.scroll.scrollBy(-30);
            }
            else if (keyCode == Keyboard.KEY_LEFT)
            {
                this.scroll.scrollTo(0);
            }
            else if (keyCode == Keyboard.KEY_RIGHT)
            {
                this.scroll.scrollTo(this.scroll.scrollSize);
            }
        }
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.isEditMode())
        {
            GuiScreen screen = this.mc.currentScreen;

            this.scroll.drag(mouseX, mouseY);

            GL11.glPushMatrix();
            GL11.glTranslatef(0, -this.scroll.scroll, 0);

            GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);

            this.drawMorphs(mouseX, mouseY);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();

            this.scroll.drawScrollbar();
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (!this.isEditMode() && !this.search.field.isFocused() && this.search.field.getText().isEmpty())
        {
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.search"), this.search.area.x + 5, this.search.area.y + 6, 0x888888);
        }
    }

    private void drawMorphs(int mouseX, int mouseY)
    {
        int m = this.scroll.w / this.perRow;
        int j = 0;

        /* Render morphs */
        for (MorphCategory category : this.categories)
        {
            int h = (MathHelper.ceil(category.cells.size() / this.perRow) + 1) * CELL_HEIGHT;

            if (category.height == 0 || category.y < this.scroll.scroll - h || category.y > this.scroll.scroll + this.scroll.h)
            {
                j++;

                continue;
            }

            int k = 0;
            this.drawString(this.font, category.title, this.scroll.x + this.shiftX + 1, category.y + this.scroll.y, 0xFFFFFFFF);

            for (MorphCell cell : category.cells)
            {
                int x = k % this.perRow * m + this.scroll.x;
                int y = k / this.perRow * CELL_HEIGHT + category.y + this.scroll.y;

                boolean insidePicker = this.picker.isActive() && mouseY >= this.scroll.y + this.scroll.h - CELL_HEIGHT;
                boolean hover = mouseX >= x && mouseY + this.scroll.scroll >= y && mouseX < x + m && mouseY + this.scroll.scroll < y + CELL_HEIGHT && !insidePicker;

                MorphVariant variant = cell.current();

                if (!cell.hasVisible)
                {
                    continue;
                }

                if (y < this.scroll.scroll - CELL_HEIGHT / 4)
                {
                    k++;
                    continue;
                }

                float scale = hover ? 28F : 21.5F;

                variant.render(Minecraft.getMinecraft().player, x + m / 2, y + 50, scale);

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

        public MorphCategory(String title, String key, String variant)
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

            if (!variant.isEmpty())
            {
                this.title += " (" + variant + ")";
            }
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
            else if (!this.error)
            {
                this.morph.renderOnScreen(player, x, y, scale, 1.0F);
            }
        }
    }
}
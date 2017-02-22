package mchorse.metamorph.client.gui.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketFavoriteMorph;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import mchorse.metamorph.network.common.PacketSelectMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Morphing survival GUI menu
 * 
 * This menu is responsible for rendering and storing the index of selected 
 * morph of currently player acquired morphs. 
 * 
 * This menu looks pretty similar to (alt / cmd) + tab menu like in most 
 * GUI based Operating Systems.
 * 
 * Sorry for this mess.
 */
public class GuiSurvivalMorphs extends Gui
{
    /**
     * Latest used morph cell. Used for favoriting 
     */
    private MorphCell latest;

    /**
     * Indexed map of things needed to be removed 
     */
    private Map<Integer, MorphRemove> toRemove = new HashMap<Integer, MorphRemove>();

    /**
     * This variable is responsible for indication of inGUI mode 
     */
    public boolean inGUI = false;

    /**
     * Show favorites only 
     */
    public boolean showFavorites = false;

    /**
     * Cached Minecraft instance 
     */
    public Minecraft mc = Minecraft.getMinecraft();

    /**
     * Morphs list
     */
    public List<MorphType> morphs = new ArrayList<MorphType>();

    /**
     * Index of selected morph 
     */
    public int index = 0;

    /**
     * "Fade out" timer 
     */
    public int timer = 0;

    /**
     * Setup morphs
     * 
     * This method should be called every time client receives 
     */
    public void setupMorphs(IMorphing morphing)
    {
        /* Collect all variations into one cells */
        Map<String, MorphType> separated = new HashMap<String, MorphType>();
        List<Integer> favorites = morphing.getFavorites();
        int i = 0;

        for (AbstractMorph morph : morphing.getAcquiredMorphs())
        {
            MorphType list = separated.get(morph.name);

            if (list == null)
            {
                list = new MorphType();
                separated.put(morph.name, list);
            }

            list.morphs.add(new MorphCell(i, morph, favorites.indexOf(i) >= 0));
            i++;
        }

        /* Clear the morphs and add all merged morphs */
        this.morphs.clear();
        this.morphs.addAll(separated.values());

        if (this.showFavorites)
        {
            Iterator<MorphType> it = this.morphs.iterator();

            while (it.hasNext())
            {
                MorphType type = it.next();

                if (!type.hasFavorites())
                {
                    it.remove();
                    continue;
                }

                Iterator<MorphCell> cellIt = type.morphs.iterator();

                while (cellIt.hasNext())
                {
                    if (!cellIt.next().favorite)
                    {
                        cellIt.remove();
                    }
                }
            }
        }

        int j = 0;

        for (MorphType type : this.morphs)
        {
            for (MorphCell cell : type.morphs)
            {
                cell.typeIndex = j;
            }

            j++;
        }

        /* Sort those morphs alphabetically */
        Collections.sort(this.morphs, new Comparator<MorphType>()
        {
            @Override
            public int compare(MorphType a, MorphType b)
            {
                return a.morphs.get(0).morph.name.compareTo(b.morphs.get(0).morph.name);
            }
        });

        this.index = MathHelper.clamp_int(this.index, -1, this.getMorphCount() - 1);
    }

    /**
     * Skip to the beginning or to the end of the morphing list.
     */
    public void skip(int factor)
    {
        int length = this.getMorphCount();
        this.timer = this.getDelay();

        if (length == 0)
        {
            return;
        }

        this.timer = this.getDelay();

        if (factor > 0)
        {
            this.index = length - 1;
        }
        else if (factor < 0)
        {
            this.index = -1;
        }
    }

    /**
     * Advance given indices forward or backward (depending on the provided 
     * argument). 
     */
    public void advance(int factor)
    {
        int length = this.getMorphCount();

        if (length == 0)
        {
            return;
        }

        this.timer = this.getDelay();
        this.index += factor;
        this.index = MathHelper.clamp_int(this.index, -1, length - 1);
    }

    /**
     * Switch to upper variation of the morph 
     */
    public void up()
    {
        this.timer = this.getDelay();

        if (this.index >= 0)
        {
            this.morphs.get(this.index).up();
        }
    }

    /**
     * Switch to lower variation of the morph 
     */
    public void down()
    {
        this.timer = this.getDelay();

        if (this.index >= 0)
        {
            this.morphs.get(this.index).down();
        }
    }

    /**
     * Favorite latest morph cell
     * 
     * This method makes sure that latest favorited morph doesn't go out of 
     * sync with the favorites. Data desync is total shit.
     */
    public void favorite(int index)
    {
        if (latest != null && latest.index == index)
        {
            latest.favorite = Morphing.get(this.mc.thePlayer).getFavorites().indexOf(index) >= 0;

            if (this.showFavorites && !latest.favorite)
            {
                this.toRemove.put(index, new MorphRemove(index, latest.typeIndex));
                this.remove(index);
            }

            latest = null;
        }
    }

    /**
     * Remove latest morph cell 
     */
    public void remove(int index)
    {
        MorphRemove toRemove = this.toRemove.get(index);

        if (toRemove != null)
        {
            MorphType type = this.morphs.get(toRemove.typeIndex);
            Iterator<MorphCell> it = type.morphs.iterator();

            while (it.hasNext())
            {
                if (it.next().index == index)
                {
                    it.remove();
                    break;
                }
            }

            if (type.morphs.isEmpty())
            {
                this.morphs.remove(toRemove.typeIndex);
            }
            else
            {
                type.clamp();
            }

            this.toRemove.remove(index);
            this.setupMorphs(Morphing.get(this.mc.thePlayer));
            this.index = MathHelper.clamp_int(this.index, -1, this.getMorphCount() - 1);
        }
    }

    /**
     * Get delay for the timer
     * 
     * Delay is about 2 seconds, however you may never know which is it since 
     * the game may lag. 
     */
    private int getDelay()
    {
        int frameRate = this.mc.gameSettings.limitFramerate;

        if (frameRate > 120)
        {
            frameRate = 120;
        }

        return frameRate * 2;
    }

    /**
     * Get how much player has acquired morphs 
     */
    private int getMorphCount()
    {
        return this.morphs.size();
    }

    public void selectCurrent()
    {
        IMorphing morphing = Morphing.get(mc.thePlayer);

        /* Checking if we're morphing in the same thing */
        boolean isSame = false;
        boolean morphed = morphing.isMorphed();
        int index = this.getSelected();

        if (index == -1)
        {
            isSame = !morphed;
        }

        if (index >= 0 && morphed)
        {
            isSame = morphing.getCurrentMorph().equals(morphing.getAcquiredMorphs().get(index));
        }

        /* No need to send packet if it's the same */
        if (!isSame)
        {
            Dispatcher.sendToServer(new PacketSelectMorph(index));
            this.timer = 0;
        }
    }

    public int getSelected()
    {
        return this.index == -1 ? -1 : this.morphs.get(this.index).current().index;
    }

    public MorphCell getCurrent()
    {
        if (this.index < 0)
        {
            return null;
        }

        if (this.morphs.isEmpty() || this.index >= this.morphs.size())
        {
            return null;
        }

        return this.morphs.get(this.index).current();
    }

    public void exitGUI()
    {
        this.timer = 0;
        this.inGUI = false;
    }

    /**
     * Remove a morph 
     */
    public void remove()
    {
        if (this.index == -1)
        {
            return;
        }

        int index = this.getSelected();

        if (!this.toRemove.containsKey(index))
        {
            this.toRemove.put(index, new MorphRemove(index, this.index));
            Dispatcher.sendToServer(new PacketRemoveMorph(index));
        }
    }

    /**
     * Favorite given morph
     */
    public void favorite(MorphCell cell)
    {
        if (latest == null)
        {
            latest = cell;
            latest.favorite = !latest.favorite;
            Dispatcher.sendToServer(new PacketFavoriteMorph(cell.index));
        }
    }

    /**
     * This method is responsible for updating morphs list to display only 
     * favorite morphs or all morphs.
     */
    public void toggleFavorites()
    {
        this.showFavorites = !this.showFavorites;
        this.setupMorphs(Morphing.get(this.mc.thePlayer));
    }

    /**
     * Render the overlay part
     */
    public void render(int width, int height)
    {
        if (!this.inGUI && timer <= 0)
        {
            return;
        }

        this.timer--;

        /* GUI size */
        int w = (int) (width * 0.8F);
        int h = (int) (height * 0.3F);

        w = w - w % 20;
        h = h - h % 2;

        /* Background */
        int x1 = width / 2 - w / 2;
        int y1 = height / 2 - h / 2;
        int x2 = width / 2 + w / 2;
        int y2 = height / 2 + h / 2;

        if (this.inGUI)
        {
            GuiUtils.scissor(0, 30, width, height - 30, width, height);
            w = width - 20;
            h = (int) (height * 0.375F);
        }
        else
        {
            Gui.drawRect(x1, y1, x2, y2, 0x99000000);
            GuiUtils.scissor(x1, y1, w, h, width, height);
        }

        this.renderMenu(width, height, w, h);

        GlStateManager.enableDepth();
    }

    /**
     * Render the menu
     * 
     * Pretty big method, huh? Big mix of math and rendering combined in this 
     * method.
     */
    public void renderMenu(int width, int height, int w, int h)
    {
        boolean renderDemorph = Metamorph.proxy.config.show_demorph;

        EntityPlayer player = this.mc.thePlayer;
        String label = "Demorph";

        /* Setup scale and margin */
        int scale = (int) (height * 0.17F / 2);
        int margin = width / 10;

        /* Make sure that margin and scale are divided even */
        scale -= scale % 2;
        margin -= margin % 2;

        if (this.inGUI)
        {
            margin = width / 7;
            scale = (int) (height * 0.17F / 1.4);
        }

        /* And compute the offset */
        int offset = this.index * margin;
        int maxScroll = this.getMorphCount() * margin - w / 2 - margin / 2 + 2;

        offset = (int) MathHelper.clamp_float(offset, 0, maxScroll);

        /* Render morphs */
        for (int i = 0, c = this.morphs.size(); i <= c; i++)
        {
            int x = width / 2 - w / 2 + (renderDemorph ? i : i - 1) * margin + margin / 2 + 1;
            int y = height / 2 + h / 5;
            boolean selected = this.index + 1 == i;

            MorphType type = i != 0 ? this.morphs.get(i - 1) : null;
            AbstractMorph morph = i != 0 ? type.current().morph : null;

            String name = Metamorph.proxy.config.hide_username ? "Demorph" : player.getName();

            if (i != 0)
            {
                name = MorphManager.INSTANCE.morphDisplayNameFromMorph(morph);
            }

            /* Scroll the position */
            if (offset > w / 2 - margin * 1.5)
            {
                x -= offset - (w / 2 - margin * 1.5);
            }

            /* Render morph itself */
            if (i == 0)
            {
                if (renderDemorph)
                {
                    this.renderPlayer(player, x, y - 2, scale);
                }
            }
            else
            {
                boolean renderUp = type.index < type.morphs.size() - 1;
                boolean renderDown = type.index > 0;
                int shift = this.inGUI ? h : (int) (scale * 2.5);

                this.renderMorph(player, type.current(), x, y - 2, margin, h, scale);

                if (selected)
                {
                    if (renderUp)
                    {
                        this.renderMorph(player, type.morphs.get(type.index + 1), x, y - shift, margin, h, scale);

                        int ay = height / 2 - h / 2 + 4;

                        Gui.drawRect(x - 1, ay, x + 1, ay + 1, 0xffffffff);
                        Gui.drawRect(x - 2, ay + 1, x + 2, ay + 2, 0xffffffff);
                        Gui.drawRect(x - 3, ay + 2, x + 3, ay + 3, 0xffffffff);
                    }

                    if (renderDown)
                    {
                        this.renderMorph(player, type.morphs.get(type.index - 1), x, y + shift, margin, h, scale);

                        int ay = height / 2 + h / 2 - 7;

                        Gui.drawRect(x - 3, ay, x + 3, ay + 1, 0xffffffff);
                        Gui.drawRect(x - 2, ay + 1, x + 2, ay + 2, 0xffffffff);
                        Gui.drawRect(x - 1, ay + 2, x + 1, ay + 3, 0xffffffff);
                    }
                }
            }

            /* Render border around the selected morph */
            if (selected)
            {
                if (i != 0 || (i == 0 && renderDemorph))
                {
                    this.renderSelected(x - margin / 2, height / 2 - h / 2 + 1, margin, h - 2);
                }

                label = name;
            }
        }

        /* Disable scissoring */
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        /* Draw the title */
        int labelY = this.inGUI ? height - 24 : height / 2 + h / 2 + 4;

        this.drawCenteredString(this.mc.fontRendererObj, label, width / 2, labelY, 0xffffffff);
    }

    /**
     * Render a grey outline around the given area.
     * 
     * Basically, this method renders selection.
     */
    public void renderSelected(int x, int y, int width, int height)
    {
        int color = 0xffcccccc;

        this.drawHorizontalLine(x, x + width - 1, y, color);
        this.drawHorizontalLine(x, x + width - 1, y + height - 1, color);

        this.drawVerticalLine(x, y, y + height - 1, color);
        this.drawVerticalLine(x + width - 1, y, y + height - 1, color);
    }

    /**
     * Render morph 
     * 
     * This method is accumulation of some rendering code in vanilla minecraft 
     * which can (theoretically) render any type of ModelBase on the screen 
     * without require the render.  
     * 
     * This method takes code from 
     * {@link RenderLivingBase#doRender(net.minecraft.entity.EntityLivingBase, double, double, double, float, float)} 
     * and {@link GuiInventory#drawEntityOnScreen(int, int, int, float, float, net.minecraft.entity.EntityLivingBase)}.
     */
    public void renderMorph(EntityPlayer player, MorphCell morph, int x, int y, int w, int h, float scale)
    {
        morph.morph.renderOnScreen(player, x, y, scale, 1.0F);

        if (morph.favorite)
        {
            GlStateManager.enableAlpha();
            this.mc.renderEngine.bindTexture(new ResourceLocation("metamorph", "textures/gui/icons.png"));

            if (this.inGUI)
            {
                this.drawTexturedModalRect(x + w / 2 - 16, y - h / 1.5F, 0, 0, 16, 16);
            }
            else
            {
                this.drawTexturedModalRect(x - w / 2, y - 16, 0, 0, 16, 16);
            }
        }
    }

    /**
     * Render player 
     */
    public void renderPlayer(EntityPlayer player, int x, int y, int scale)
    {
        EntityPlayerSP entity = (EntityPlayerSP) player;
        RenderLivingBase<EntityPlayerSP> render = (RenderLivingBase<EntityPlayerSP>) this.mc.getRenderManager().getEntityRenderObject(entity);
        ModelBase model = render.getMainModel();

        model.isChild = false;
        model.swingProgress = 0;

        this.mc.renderEngine.bindTexture(entity.getLocationSkin());
        GuiUtils.drawModel(model, player, x, y, scale);
    }

    /* Data types */

    /**
     * Morph type class
     * 
     * This class is responsible
     */
    public static class MorphType
    {
        public List<MorphCell> morphs = new ArrayList<MorphCell>();
        public int index;

        /**
         * Get currently selected morph cell
         */
        public MorphCell current()
        {
            return this.morphs.get(this.index);
        }

        public boolean hasFavorites()
        {
            for (MorphCell cell : this.morphs)
            {
                if (cell.favorite)
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * Remove currently selected morph cell 
         */
        public void remove()
        {
            this.morphs.remove(this.index);
            this.clamp();
        }

        public void up()
        {
            this.index++;
            this.clamp();
        }

        public void down()
        {
            this.index--;
            this.clamp();
        }

        public void clamp()
        {
            this.index = MathHelper.clamp_int(this.index, 0, this.morphs.size() - 1);
        }
    }

    /**
     * Morph cell class
     * 
     * This class is responsible for storing morph and index from acquired 
     * morphs.
     */
    public static class MorphCell
    {
        public int index;
        public int typeIndex;
        public boolean favorite;
        public AbstractMorph morph;

        public MorphCell(int index, AbstractMorph morph, boolean favorite)
        {
            this.index = index;
            this.morph = morph;
            this.favorite = favorite;
        }
    }

    /**
     * Morph removal information
     * 
     * This class is responsible for containing information about morph 
     * removal.
     */
    public static class MorphRemove
    {
        public int index;
        public int typeIndex;

        public MorphRemove(int index, int typeIndex)
        {
            this.index = index;
            this.typeIndex = typeIndex;
        }
    }
}
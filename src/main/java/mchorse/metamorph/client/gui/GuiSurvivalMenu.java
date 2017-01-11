package mchorse.metamorph.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Morphing survival GUI menu
 * 
 * This menu is responsible for rendering and storing the index of selected 
 * morph of currently player acquired morphs. 
 * 
 * This menu looks pretty similar to (alt / cmd) + tab menu like in most 
 * GUI based Operating Systems.
 */
public class GuiSurvivalMenu extends GuiScreen
{
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
    public void setupMorphs(List<AbstractMorph> morphs)
    {
        /* Collect all variations into one cells */
        Map<String, MorphType> separated = new HashMap<String, MorphType>();
        int i = 0;

        for (AbstractMorph morph : morphs)
        {
            MorphType list = separated.get(morph.name);

            if (list == null)
            {
                list = new MorphType();
                separated.put(morph.name, list);
            }

            list.morphs.add(new MorphCell(i, morph));
            i++;
        }

        /* Clear the morphs and add all merged morphs */
        this.morphs.clear();
        this.morphs.addAll(separated.values());

        /* Sort those morphs alphabetically */
        Collections.sort(this.morphs, new Comparator<MorphType>()
        {
            @Override
            public int compare(MorphType a, MorphType b)
            {
                return a.morphs.get(0).morph.name.compareTo(b.morphs.get(0).morph.name);
            }
        });
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
        this.morphs.get(this.index).up();
    }

    /**
     * Switch to lower variation of the morph 
     */
    public void down()
    {
        this.timer = this.getDelay();
        this.morphs.get(this.index).down();
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

    public int getSelected()
    {
        return this.index == -1 ? -1 : this.morphs.get(this.index).current().index;
    }

    /* Drawing code */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.timer = this.getDelay();
        this.render(width, height);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Render the overlay part
     */
    public void render(int width, int height)
    {
        if (timer == 0)
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

        Gui.drawRect(x1, y1, x2, y2, 0x99000000);
        GuiUtils.scissor(x1, y1, w, h, width, height);

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
        EntityPlayer player = this.mc.thePlayer;
        String label = "Demorph";

        /* Setup scale and margin */
        int scale = (int) (height * 0.17F / 2);
        int margin = width / 10;

        /* Make sure that margin and scale are divided even */
        scale -= scale % 2;
        margin -= margin % 2;

        /* And compute the offset */
        int offset = this.index * margin;
        int maxScroll = this.getMorphCount() * margin - w / 2 - margin / 2 + 2;

        offset = (int) MathHelper.clamp_float(offset, 0, maxScroll);

        /* Render morphs */
        for (int i = 0, c = this.morphs.size(); i <= c; i++)
        {
            int x = width / 2 - w / 2 + i * margin + margin / 2 + 1;
            int y = height / 2 + h / 5;
            boolean selected = this.index + 1 == i;

            MorphType type = i != 0 ? this.morphs.get(i - 1) : null;
            AbstractMorph morph = i != 0 ? type.current().morph : null;

            String name = Metamorph.proxy.config.hide_username ? "Demorph" : player.getName();

            if (i != 0)
            {
                name = MorphManager.INSTANCE.morphDisplayNameFromMorph(morph.name);
            }

            /* Scroll the position */
            if (offset > w / 2 - margin * 1.5)
            {
                x -= offset - (w / 2 - margin * 1.5);
            }

            /* Render morph itself */
            if (i == 0)
            {
                this.renderPlayer(player, x, y - 2, scale);
            }
            else
            {
                boolean renderUp = type.index < type.morphs.size() - 1;
                boolean renderDown = type.index > 0;

                if (selected)
                {
                    if (renderUp)
                    {
                        this.renderMorph(player, type.morphs.get(type.index + 1).morph, x, y - scale * 2, scale);
                    }
                }

                this.renderMorph(player, morph, x, y - 2, scale);

                if (selected)
                {
                    if (renderDown)
                    {
                        this.renderMorph(player, type.morphs.get(type.index - 1).morph, x, y + scale * 2, scale);
                    }

                    if (renderUp)
                    {
                        int ay = height / 2 - h / 2 + 4;

                        Gui.drawRect(x - 1, ay, x + 1, ay + 1, 0xffffffff);
                        Gui.drawRect(x - 2, ay + 1, x + 2, ay + 2, 0xffffffff);
                        Gui.drawRect(x - 3, ay + 2, x + 3, ay + 3, 0xffffffff);
                    }

                    if (renderDown)
                    {
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
                this.renderSelected(x - margin / 2, height / 2 - h / 2 + 1, margin, h - 2);

                label = name;
            }
        }

        /* Disable scissoring */
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        /* Draw the title */
        this.drawCenteredString(this.mc.fontRendererObj, label, width / 2, height / 2 + h / 2 + 4, 0xffffffff);
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
    public void renderMorph(EntityPlayer player, AbstractMorph morph, int x, int y, float scale)
    {
        morph.renderOnScreen(player, x, y, scale, 1.0F);
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

    /**
     * Morph type class
     * 
     * This class is responsible
     */
    public static class MorphType
    {
        public List<MorphCell> morphs = new ArrayList<MorphCell>();
        public int index;

        public MorphCell current()
        {
            return this.morphs.get(this.index);
        }

        public void up()
        {
            this.index = MathHelper.clamp_int(index + 1, 0, this.morphs.size() - 1);
        }

        public void down()
        {
            this.index = MathHelper.clamp_int(index - 1, 0, this.morphs.size() - 1);
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
        public AbstractMorph morph;

        public MorphCell(int index, AbstractMorph morph)
        {
            this.index = index;
            this.morph = morph;
        }
    }
}
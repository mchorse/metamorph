package mchorse.metamorph.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
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
public class GuiSurvivalMenu extends Gui
{
    /**
     * Cached Minecraft instance 
     */
    public Minecraft mc = Minecraft.getMinecraft();

    /**
     * Index of selected morph 
     */
    public int index = 0;

    /**
     * "Fade out" timer 
     */
    public int timer = 0;

    /**
     * Render the GUI 
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
        List<AbstractMorph> morphs = this.getMorph().getAcquiredMorphs();
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
        for (int i = 0; i <= morphs.size(); i++)
        {
            int x = width / 2 - w / 2 + i * margin + margin / 2 + 1;
            int y = height / 2 + h / 5;

            String name = Metamorph.proxy.config.hide_username ? "Demorph" : player.getName();

            if (i != 0)
            {
                name = MorphManager.INSTANCE.morphDisplayNameFromMorph(morphs.get(i - 1).name);
            }

            /* Scroll the position */
            if (offset > w / 2 - margin * 1.5)
            {
                x -= offset - (w / 2 - margin * 1.5);
            }

            /* Render border around the selected morph */
            if (this.index + 1 == i)
            {
                this.renderSelected(x - margin / 2, height / 2 - h / 2 + 1, margin, h - 2);

                label = name;
            }

            /* Render morph itself */
            if (i == 0)
            {
                this.renderPlayer(player, x, y - 2, scale);
            }
            else
            {
                this.renderMorph(player, morphs.get(i - 1), x, y - 2, scale);
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
     * Get morphing 
     */
    private IMorphing getMorph()
    {
        return Morphing.get(this.mc.thePlayer);
    }

    /**
     * Get how much player has acquired morphs 
     */
    private int getMorphCount()
    {
        return this.getMorph().getAcquiredMorphs().size();
    }
}
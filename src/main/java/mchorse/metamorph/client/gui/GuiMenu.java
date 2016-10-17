package mchorse.metamorph.client.gui;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class GuiMenu extends Gui
{
    public Minecraft mc = Minecraft.getMinecraft();

    public int index = 0;
    public int timer = 0;

    public float opacity = 0;
    public byte state = 0;

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
        int w = (int) ((float) width * 0.8F);
        int h = (int) ((float) height * 0.2F);

        /* F*$! those ints */
        float rx = (float) this.mc.displayWidth / (float) width;
        float ry = (float) this.mc.displayHeight / (float) height;

        /* Background */
        int x1 = MathHelper.floor_float(width / 2 - w / 2);
        int y1 = MathHelper.floor_float(height / 2 - h / 2);
        int x2 = MathHelper.floor_float(width / 2 + w / 2);
        int y2 = MathHelper.floor_float(height / 2 + h / 2);

        Gui.drawRect(x1, y1, x2, y2, 0x99000000);

        /* Clipping area around scroll area */
        int x = (int) (x1 * rx);
        int y = (int) (this.mc.displayHeight - y2 * ry);
        int ww = (int) (w * rx);
        int hh = (int) (h * ry);

        GL11.glScissor(x, y, ww, hh);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        this.renderMenu(width, height, w, h);
    }

    /**
     * Render the menu
     */
    public void renderMenu(int width, int height, int w, int h)
    {
        EntityPlayer player = this.mc.thePlayer;
        String label = "";

        float scale = (float) height * 0.17F / 2;
        float margin = 36;
        float offset = this.index * 36;
        float max_scroll = this.getMorphCount() * margin - w / 2 - scale / 2 - 4;

        int i = 0;

        offset = (float) Math.floor(MathHelper.clamp_float(offset, 0, max_scroll));

        for (String name : this.getMorph().getAcquiredMorphs())
        {
            float x = (float) Math.floor(width / 2 - w / 2 + i * margin + scale - 1);
            float y = (float) Math.floor(height / 2 + h / 2);

            /* Scroll the position */
            if (offset > w / 2 - margin / 2)
            {
                x -= offset - (w / 2 - margin / 2);
            }

            /* Render border around the selected morph */
            if (this.index == i)
            {
                this.renderSelected((int) (x - margin / 2), height / 2 - h / 2 + 1, (int) margin, h - 3);

                label = name;
            }

            /* Render morph itself */
            this.renderMorph(player, name, (int) x, (int) y - 2, scale);

            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();

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

        this.drawHorizontalLine(x, x + width, y, color);
        this.drawHorizontalLine(x, x + width, y + height, color);

        this.drawVerticalLine(x, y, y + height, color);
        this.drawVerticalLine(x + width, y, y + height, color);
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
    public void renderMorph(EntityPlayer player, String name, int x, int y, float scale)
    {
        float factor = 0.0625F;

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        Model data = MorphManager.INSTANCE.morphs.get(name).model;
        ModelCustom model = ModelCustom.MODELS.get(name);

        model.pose = model.model.poses.get("standing");
        model.swingProgress = 0;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        GlStateManager.enableAlpha();

        model.setLivingAnimations(player, 0, 0, 0);
        model.setRotationAngles(0, 0, 0, 0, 0, factor, player);

        Minecraft.getMinecraft().renderEngine.bindTexture(data.defaultTexture);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        model.render(player, 0, 0, 0, 0, 0, factor);

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Proceed to the next morph 
     */
    public void next()
    {
        int length = this.getMorphCount();

        if (length == 0)
        {
            return;
        }

        if (this.index < length - 1)
        {
            this.index++;
            this.timer = 80;
        }
    }

    /**
     * Proceed to the previous morph 
     */
    public void prev()
    {
        int length = this.getMorphCount();

        if (length == 0)
        {
            return;
        }

        if (this.index > 0)
        {
            this.index--;
            this.timer = 80;
        }
    }

    /**
     * Get morphing 
     */
    private IMorphing getMorph()
    {
        return Minecraft.getMinecraft().thePlayer.getCapability(MorphingProvider.MORPHING_CAP, null);
    }

    /**
     * Get how much player has acquired morphs 
     */
    private int getMorphCount()
    {
        return this.getMorph().getAcquiredMorphs().size();
    }
}
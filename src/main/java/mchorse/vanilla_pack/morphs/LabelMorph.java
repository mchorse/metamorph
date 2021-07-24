package mchorse.vanilla_pack.morphs;

import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.TextUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.List;
import java.util.Objects;

public class LabelMorph extends AbstractMorph
{
    public static final String DEFAULT_LABEL = "Lorem ipsum";
    public static final Matrix4f matrix = new Matrix4f();

    public String label = DEFAULT_LABEL;
    public int max = -1;
    public float anchorX = 0.5F;
    public float anchorY = 0.5F;
    public int color = 0xffffff;
    public boolean lighting = true;

    /* Shadow properties */
    public boolean shadow = false;
    public float shadowX = 1F;
    public float shadowY = 1F;
    public int shadowColor = 0;

    /* Background */
    public int background = 0x00000000;
    public float offset = 3;

    public boolean billboard;

    public LabelMorph()
    {
        this.name = "label";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 10);

        this.renderString();

        GlStateManager.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        /* Approximately 16 letters per block */
        double scale = 1D / 6D / 8D;

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.scale(scale, -scale, scale);

        if (this.billboard)
        {
            /* Get matrix */
            Matrix4f matrix4f = MatrixUtils.readModelView(matrix);
            Vector4f zero = new Vector4f(0, 0, 0, 1);

            matrix4f.transform(zero);
            matrix4f.setIdentity();
            matrix4f.setTranslation(new Vector3f(zero.x, zero.y, zero.z));
            matrix4f.transpose();

            MatrixUtils.loadModelView(matrix4f);
            GlStateManager.scale(-scale, scale, scale);

            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        }

        this.renderString();

        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }

    }

    @SideOnly(Side.CLIENT)
    private void renderString()
    {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String text = TextUtils.processColoredText(this.label);

        if (this.max <= 0)
        {
            int w = font.getStringWidth(text);
            int x = -(int) (w * this.anchorX);
            int y = -(int) (font.FONT_HEIGHT * this.anchorY);

            this.drawShadow(x, y, w, font.FONT_HEIGHT);

            if (this.shadow)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.shadowX, this.shadowY, -0.1F);
                font.drawString(text, x, y, this.shadowColor);
                GlStateManager.popMatrix();
            }

            font.drawString(text, x, y, this.color);
        }
        else
        {
            int min = 6;

            for (int i = 0; i < text.length(); i++)
            {
                min = Math.max(font.getCharWidth(text.charAt(i)), min);
            }
            
            int max = MathUtils.clamp(this.max, min, Integer.MAX_VALUE);
            List<String> labels = font.listFormattedStringToWidth(text, max);
            int h = MathUtils.clamp(labels.size() - 1, 0, 100) * 12 + font.FONT_HEIGHT;
            int y = -(int) (h * this.anchorY);

            this.drawShadow(-(int) (max * this.anchorX), y, max, h);

            if (this.shadow)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.shadowX, this.shadowY, -0.1F);

                for (String label : labels)
                {
                    int w = font.getStringWidth(label);

                    font.drawString(label, -(int) (w * this.anchorX), y, this.shadowColor);
                    y += 12;
                }

                GlStateManager.popMatrix();
            }

            y = -(int) (h * this.anchorY);

            for (String label : labels)
            {
                int w = font.getStringWidth(label);

                font.drawString(label, -(int) (w * this.anchorX), y, this.color);
                y += 12;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void drawShadow(int x, int y, int w, int h)
    {
        Color color = ColorUtils.COLOR.set(this.background, true);

        if (color.a <= 0)
        {
            return;
        }

        GlStateManager.disableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.translate(0, 0, -0.2F);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x + w + this.offset, y - this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(x - this.offset, y - this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(x - this.offset, y + h + this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(x + w + this.offset, y + h + this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof LabelMorph)
        {
            LabelMorph label = (LabelMorph) obj;

            result = result && Objects.equals(this.label, label.label);
            result = result && this.max == label.max;
            result = result && this.anchorX == label.anchorX;
            result = result && this.anchorY == label.anchorY;
            result = result && this.color == label.color;
            result = result && this.shadow == label.shadow;
            result = result && this.shadowX == label.shadowX;
            result = result && this.shadowY == label.shadowY;
            result = result && this.shadowColor == label.shadowColor;
            result = result && this.lighting == label.lighting;
            result = result && this.background == label.background;
            result = result && this.offset == label.offset;
            result = result && this.billboard == label.billboard;
        }

        return result;
    }

    @Override
    public AbstractMorph create()
    {
        return new LabelMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof LabelMorph)
        {
            LabelMorph label = (LabelMorph) from;

            this.label = label.label;
            this.max = label.max;
            this.anchorX = label.anchorX;
            this.anchorY = label.anchorY;
            this.color = label.color;
            this.shadow = label.shadow;
            this.shadowX = label.shadowX;
            this.shadowY = label.shadowY;
            this.shadowColor = label.shadowColor;
            this.lighting = label.lighting;
            this.background = label.background;
            this.offset = label.offset;
            this.billboard = label.billboard;
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return target.width;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return target.height;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (!this.label.equals(DEFAULT_LABEL)) tag.setString("Label", this.label);
        if (this.max > 0) tag.setInteger("Max", this.max);
        if (this.anchorX != 0.5F) tag.setFloat("AnchorX", this.anchorX);
        if (this.anchorY != 0.5F) tag.setFloat("AnchorY", this.anchorY);
        if (this.color != 0xffffff) tag.setInteger("Color", this.color);
        if (this.shadow) tag.setBoolean("Shadow", this.shadow);
        if (this.shadowX != 1F) tag.setFloat("ShadowX", this.shadowX);
        if (this.shadowY != 1F) tag.setFloat("ShadowY", this.shadowY);
        if (this.shadowColor != 0) tag.setInteger("ShadowColor", this.shadowColor);
        if (!this.lighting) tag.setBoolean("Lighting", this.lighting);
        if (this.background != 0) tag.setInteger("Background", this.background);
        if (this.offset != 3) tag.setFloat("Offset", this.offset);
        if (this.billboard) tag.setBoolean("Billboard", this.billboard);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Label")) this.label = tag.getString("Label");
        if (tag.hasKey("Max")) this.max = tag.getInteger("Max");
        if (tag.hasKey("AnchorX")) this.anchorX = tag.getFloat("AnchorX");
        if (tag.hasKey("AnchorY")) this.anchorY = tag.getFloat("AnchorY");
        if (tag.hasKey("Color")) this.color = tag.getInteger("Color");
        if (tag.hasKey("Shadow")) this.shadow = tag.getBoolean("Shadow");
        if (tag.hasKey("ShadowX")) this.shadowX = tag.getFloat("ShadowX");
        if (tag.hasKey("ShadowY")) this.shadowY = tag.getFloat("ShadowY");
        if (tag.hasKey("ShadowColor")) this.shadowColor = tag.getInteger("ShadowColor");
        if (tag.hasKey("Lighting")) this.lighting = tag.getBoolean("Lighting");
        if (tag.hasKey("Background")) this.background = tag.getInteger("Background");
        if (tag.hasKey("Offset")) this.offset = tag.getFloat("Offset");
        if (tag.hasKey("Billboard")) this.billboard = tag.getBoolean("Billboard");
    }
}
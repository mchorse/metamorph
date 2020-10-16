package mchorse.metamorph.client.render;

import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.entity.EntityMorph;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMorph extends RenderLivingBase<EntityMorph>
{
    public RenderMorph(RenderManager manager, ModelBase model, float shadowSize)
    {
        super(manager, model, shadowSize);
    }

    /**
     * Render morph's name only if the player is pointed at the entity
     */
    @Override
    protected boolean canRenderName(EntityMorph entity)
    {
        return super.canRenderName(entity) && entity.hasCustomName() && entity == this.renderManager.pointedEntity;
    }

    /**
     * Get entity texture
     * 
     * Returns null, because this method isn't used
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityMorph entity)
    {
        return null;
    }

    /**
     * Render the morph entity with some blending going on and blue-ish 
     * coloring. 
     */
    @Override
    public void doRender(EntityMorph entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity.morph == null)
        {
            return;
        }

        this.shadowSize = 0.35F;

        float range = (entity.timer - partialTicks) / 30.0F;
        float alpha = 0.7F - (range <= 0 ? 0.0F : range) * 0.7F;

        GlStateManager.pushMatrix();
        GlStateManager.color(0.1F, 0.9F, 1.0F, alpha > 0.7F ? 0.7F : alpha);

        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float ticks = entity.ticksExisted + partialTicks;
        float rotation = (ticks) * 5.0F;

        GlStateManager.translate(x, y + Math.sin(ticks / 5.0F) * 0.1F + 0.2F, z);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(alpha, alpha, alpha);

        MorphUtils.render(entity.morph, entity, 0, 0, 0, entityYaw, partialTicks);

        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
        GlStateManager.popMatrix();
    }

    /**
     * Scale shit out of this morph
     * 
     * This method is responsible for scaling the model. This suppose to make 
     * a very cool effect of entity appearing.
     */
    @Override
    protected void preRenderCallback(EntityMorph entity, float partialTickTime)
    {
        /* Interpolate scale */
        float scale = 1.0F - ((float) entity.timer / 30);

        if (scale > 1)
        {
            scale = 1.0F;
        }

        float x = 1.0F;
        float y = 1.0F;
        float z = 1.0F;

        x = MathHelper.clamp(x, 0.0F, 1.5F);
        y = MathHelper.clamp(y, 0.0F, 1.5F);
        z = MathHelper.clamp(z, 0.0F, 1.5F);

        GlStateManager.scale(x * scale, y * scale, z * scale);
    }

    /**
     * Rendering factory
     * 
     * Returns new instance of the morph renderer
     */
    public static class MorphFactory implements IRenderFactory<EntityMorph>
    {
        @Override
        public Render<? super EntityMorph> createRenderFor(RenderManager manager)
        {
            return new RenderMorph(manager, null, 0.5F);
        }
    }
}
package mchorse.metamorph.client.render;

import java.util.Map;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.entity.EntityMorph;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;

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
     * Render the morph entity with some blending going on and blue-ish 
     * coloring. 
     */
    @Override
    public void doRender(EntityMorph entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.setupModel(entity);

        if (this.mainModel == null) return;

        GlStateManager.color(0.1F, 0.9F, 1.0F, 0.7F);

        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }

    /**
     * Get default texture for entity 
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityMorph entity)
    {
        return this.mainModel == null ? null : ((ModelCustom) this.mainModel).model.defaultTexture;
    }

    /**
     * Setup the model for player instance.
     *
     * This method is responsible for picking the right model and pose based
     * on player properties.
     */
    public void setupModel(EntityMorph entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;

        String pose = entity.isSneaking() ? "sneaking" : (entity.isElytraFlying() ? "flying" : "standing");
        ModelCustom model = models.get(entity.morph);

        if (model != null)
        {
            model.pose = model.model.poses.get(pose);
            this.mainModel = model;
        }
    }

    /**
     * Make player a little bit smaller (so he looked like steve, and not like an 
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityMorph entity, float partialTickTime)
    {
        Model data = ((ModelCustom) this.mainModel).model;

        /* Interpolate scale */
        float scale = 1.0F - ((float) entity.timer / 30);

        if (scale > 1)
        {
            scale = 1.0F;
        }

        float x = MathHelper.clamp_float(data.scale[0], 0.0F, 1.5F);
        float y = MathHelper.clamp_float(data.scale[1], 0.0F, 1.5F);
        float z = MathHelper.clamp_float(data.scale[2], 0.0F, 1.5F);

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
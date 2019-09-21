package net.minecraft.client.renderer.entity;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

/**
 * Render sub player renderer
 *
 * This class is responsible for substituting native player renderer classes
 * in the skinMap to achieve the rendering of the custom morphed hands.
 *
 * I hope nobody will want to substitute the same map as I did :D
 */
public class RenderSubPlayer extends RenderPlayer
{
    public RenderPlayer original;

    /**
     * Initiate with render manager, player renderer and smallArms flag.
     */
    public RenderSubPlayer(RenderManager renderManager, RenderPlayer original, boolean smallArms)
    {
        super(renderManager, smallArms);
        this.original = original;
        // This improves compat with render override code that thinks this is the original RenderPlayer
        this.layerRenderers = original.layerRenderers;
    }

    /**
     * Should make layers added to the original player renderer
     */
    @Override
    public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer)
    {
        if (this.original == null)
        {
            return false;
        }
        
        return this.original.addLayer(layer);
    }

    /**
     * Render default or morphed left hand.
     */
    @Override
    public void renderLeftArm(AbstractClientPlayer clientPlayer)
    {
        IMorphing morph = Morphing.get(clientPlayer);

        if (morph != null && morph.isMorphed())
        {
            if (morph.getCurrentMorph().renderHand(clientPlayer, EnumHand.OFF_HAND))
            {
                return;
            }
        }

        this.original.renderLeftArm(clientPlayer);
    }

    /**
     * Render default or morphed right hand.
     */
    @Override
    public void renderRightArm(AbstractClientPlayer clientPlayer)
    {
        IMorphing morph = Morphing.get(clientPlayer);

        if (morph != null && morph.isMorphed())
        {
            if (morph.getCurrentMorph().renderHand(clientPlayer, EnumHand.MAIN_HAND))
            {
                return;
            }
        }

        this.original.renderRightArm(clientPlayer);
    }

    /* Overriding RenderPlayer methods */

    @Override
    public ModelPlayer getMainModel()
    {
        return this.original == null ? super.getMainModel() : this.original.getMainModel();
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.original.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity)
    {
        return this.original.getEntityTexture(entity);
    }

    @Override
    public void transformHeldFull3DItemLayer()
    {
        this.original.transformHeldFull3DItemLayer();
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        this.original.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    @Override
    protected void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq)
    {
        this.original.renderEntityName(entityIn, x, y, z, name, distanceSq);
    }

    @Override
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z)
    {
        this.original.renderLivingAt(entityLivingBaseIn, x, y, z);
    }

    @Override
    protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        this.original.applyRotations(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    }
}
package mchorse.metamorph.client.render;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

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
    private mchorse.metamorph.client.render.RenderPlayer render;

    /**
     * Initiate with render manager, player renderer and smallArms flag.
     */
    public RenderSubPlayer(RenderManager renderManager, mchorse.metamorph.client.render.RenderPlayer render, boolean smallArms)
    {
        super(renderManager, smallArms);

        this.render = render;
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
            this.render.setupModel(clientPlayer);

            if (this.render.getMainModel() != null)
            {
                this.render.renderLeftArm(clientPlayer);

                return;
            }
        }

        super.renderLeftArm(clientPlayer);
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
            this.render.setupModel(clientPlayer);

            if (this.render.getMainModel() != null)
            {
                this.render.renderRightArm(clientPlayer);

                return;
            }
        }

        super.renderRightArm(clientPlayer);
    }
}
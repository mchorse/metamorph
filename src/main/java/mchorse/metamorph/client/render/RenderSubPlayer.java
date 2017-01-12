package mchorse.metamorph.client.render;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumHand;

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
    /**
     * Initiate with render manager, player renderer and smallArms flag.
     */
    public RenderSubPlayer(RenderManager renderManager, boolean smallArms)
    {
        super(renderManager, smallArms);
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
            if (morph.getCurrentMorph().renderHand(clientPlayer, EnumHand.MAIN_HAND))
            {
                return;
            }
        }

        super.renderRightArm(clientPlayer);
    }
}
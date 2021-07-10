package mchorse.metamorph.api.morphs.utils;

import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.IBodyPartProvider;

public interface ISyncableMorph
{
    public void pause(AbstractMorph previous, int offset);

    public boolean isPaused();

    default public void resume()
    {
        Object morph = this;

        if (morph instanceof IMorphProvider)
        {
            morph = ((IMorphProvider) morph).getMorph();
        }
        
        if (morph instanceof IAnimationProvider)
        {
            ((IAnimationProvider) morph).getAnimation().paused = false;
        }
        
        if (morph instanceof IBodyPartProvider)
        {
            for (BodyPart part : ((IBodyPartProvider) morph).getBodyPart().parts)
            {
                if (!part.morph.isEmpty() && part.morph.get() instanceof ISyncableMorph)
                {
                    ((ISyncableMorph) part.morph.get()).resume();
                }
            }
        }
    }
}
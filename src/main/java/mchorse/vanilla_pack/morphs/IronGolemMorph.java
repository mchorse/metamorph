package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Iron golem morph
 * 
 * This morph is responsible for making IronGolem morph great again! This morph 
 * is very powerful. I should make him much slower.
 */
public class IronGolemMorph extends EntityMorph
{
    @Override
    public void update(EntityLivingBase target)
    {
        if (target.motionY > 0)
        {
            target.motionY *= 0.9;
        }
        else
        {
            target.motionX *= 0.5;
            target.motionZ *= 0.5;

            if (target.motionY > -5)
            {
                target.motionY *= (1.1 + target.motionY / 50.0);
            }
        }

        target.motionX *= 0.5;
        target.motionZ *= 0.5;

        super.update(target);
    }

    @Override
    public AbstractMorph create()
    {
        return new IronGolemMorph();
    }
}

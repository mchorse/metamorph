package mchorse.vanilla_pack.actions;

import javax.annotation.Nullable;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;

/**
 * Jump action
 * 
 * Makes player jump very high like a <s>horse</s> rabbit. The strength of 
 * this jump is about 3 blocks high. 
 */
public class Jump implements IAction
{
    @Override
    public void execute(EntityLivingBase target, @Nullable AbstractMorph morph)
    {
        if (target.onGround && !target.isWet())
        {
            target.motionX *= 4.0;
            target.motionY = 0.75;
            target.motionZ *= 4.0;
        }
    }
}
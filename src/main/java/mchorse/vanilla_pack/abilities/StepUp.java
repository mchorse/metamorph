package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;

/**
 * Step Up ability
 * 
 * This ability makes player walk up blocks, like a horse.
 */
public class StepUp extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
		target.stepHeight = 1.0f;
    }
	
	@Override
    public void onMorph(EntityLivingBase target)
    {
		target.stepHeight = 1.0f;
    }
	
	@Override
    public void onDemorph(EntityLivingBase target)
    {
		target.stepHeight = 0.6f;
    }
}
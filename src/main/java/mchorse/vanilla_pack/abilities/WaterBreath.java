package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Water breath ability
 * 
 * This ability grants its owner ability to stay in water and refill its air. 
 */
public class WaterBreath extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        updateAir(target);
    }

    private void updateAir(EntityLivingBase target)
    {
        if (target instanceof EntityPlayer)
        {
            IMorphing morphing = Morphing.get((EntityPlayer) target);

            if (morphing != null)
            {
                if (target.isInWater())
                {
                    morphing.setSquidAir(300);
                    target.setAir(300);
                }
                else
                {
                    int air = morphing.getSquidAir() - 1;

                    if (air <= -20)
                    {
                        air = 0;
                        target.attackEntityFrom(DamageSource.DROWN, 2.0F);
                    }

                    morphing.setSquidAir(air);
                }
            }
        }
    }

    /**
     * On morph, show squid air
     */
    @Override
    public void onMorph(EntityLivingBase target)
    {
        IMorphing morphing = Morphing.get((EntityPlayer) target);

        if (morphing != null)
        {
            morphing.setSquidAir(target.getAir());
            morphing.setHasSquidAir(true);
        }
    }

    /**
     * On demorph, show regular player air again
     */
    @Override
    public void onDemorph(EntityLivingBase target)
    {
        IMorphing morphing = Morphing.get((EntityPlayer) target);

        if (morphing != null)
        {
            target.setAir(morphing.getSquidAir());
            morphing.setHasSquidAir(false);
        }
    }
}
package mchorse.vanilla_pack.attacks;

import mchorse.metamorph.api.MorphHandler;
import mchorse.metamorph.api.abilities.IAttackAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

/**
 * Knock back attack
 * 
 * This makes the entity go up in the air. Really cool!
 */
public class KnockbackAttack implements IAttackAbility
{
    @Override
    public void attack(final Entity target, EntityLivingBase source)
    {
        final Vec3d look = source.getLook(1.0F);
        final double d = 1;

        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                target.motionX = look.xCoord * d;
                target.motionY = 1;
                target.motionZ = look.zCoord * d;
            }
        };

        if (!source.worldObj.isRemote)
        {
            MorphHandler.FUTURE_TASKS_SERVER.add(task);
        }
        else
        {
            MorphHandler.FUTURE_TASKS_CLIENT.add(task);
        }
    }
}
package mchorse.metamorph.api.attacks;

import mchorse.metamorph.api.IAttackAbility;
import mchorse.metamorph.api.morph.MorphHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * Knock back attack
 * 
 * This makes the entity go up in the air. Really cool!
 */
public class KnockbackAttack implements IAttackAbility
{
    @Override
    public void attack(final Entity target, EntityPlayer player)
    {
        final Vec3d look = player.getLook(1.0F);
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

        if (!player.worldObj.isRemote)
        {
            MorphHandler.FUTURE_TASKS_SERVER.add(task);
        }
        else
        {
            MorphHandler.FUTURE_TASKS_CLIENT.add(task);
        }
    }
}
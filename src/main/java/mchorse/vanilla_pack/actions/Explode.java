package mchorse.vanilla_pack.actions;

import mchorse.metamorph.api.abilities.IAction;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/**
 * Explode action
 * 
 * This action makes an explosion and also kills the player. Why kill also the 
 * player? Because it won't be so creeper if he won't die. 
 * 
 * EXPLOSIONS!!! Mr. Torgue approves this action. 
 */
public class Explode implements IAction
{
    @Override
    public void execute(EntityLivingBase target)
    {
        if (target.worldObj.isRemote)
        {
            return;
        }

        target.worldObj.createExplosion(target, target.posX, target.posY, target.posZ, 3, true);

        if (!(target instanceof EntityPlayer) || (target instanceof EntityPlayer && !((EntityPlayer) target).isCreative()))
        {
            target.attackEntityFrom(DamageSource.outOfWorld, target.getMaxHealth());
        }
    }
}
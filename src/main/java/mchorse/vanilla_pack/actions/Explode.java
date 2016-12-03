package mchorse.vanilla_pack.actions;

import mchorse.metamorph.api.IAction;
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
    public void execute(EntityPlayer player)
    {
        if (player.worldObj.isRemote)
        {
            return;
        }

        player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 3, true);

        if (!player.isCreative())
        {
            player.attackEntityFrom(DamageSource.outOfWorld, player.getMaxHealth());
        }
    }
}
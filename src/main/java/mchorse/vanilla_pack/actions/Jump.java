package mchorse.vanilla_pack.actions;

import mchorse.metamorph.api.abilities.IAction;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Jump action
 * 
 * Makes player jump very high like a <s>horse</s> rabbit. The strength of 
 * this jump is about 3 blocks high. 
 */
public class Jump implements IAction
{
    @Override
    public void execute(EntityPlayer player)
    {
        if (player.onGround)
        {
            player.motionX *= 4.0;
            player.motionY = 0.75;
            player.motionZ *= 4.0;
        }
    }
}
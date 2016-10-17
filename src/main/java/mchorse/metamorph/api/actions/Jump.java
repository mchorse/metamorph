package mchorse.metamorph.api.actions;

import mchorse.metamorph.api.IAction;
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
            player.motionY = 0.75;
        }
    }
}